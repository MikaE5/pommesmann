package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.GameParts.GameEngine;
import de.androidmika.pommesmann.GameScreen.GameView;
import de.androidmika.pommesmann.GameScreen.JoystickView;
import de.androidmika.pommesmann.R;

public class GameActivity extends Activity implements JoystickView.JoystickListener,
        View.OnClickListener, GameView.GameOperations, GameEngine.SoundManager {

    private GameView gameView;
    private Button fireButton;
    private GameEngine gameEngine;

    private MediaPlayer mpLaser;
    private MediaPlayer mpBox;
    private MediaPlayer mpHit;
    private MediaPlayer mpPowerup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
        }

        if (App.getLeftHanded()) {
            setContentView(R.layout.game_activity_left);
        } else {
            setContentView(R.layout.game_activity_right);
        }

        gameView = findViewById(R.id.gameView);
        gameView.setOnClickListener(this);
        fireButton = findViewById(R.id.fireButton);
        fireButton.setOnClickListener(this);


        gameEngine = new GameEngine(this);
        if (App.getSound()) {
            mpLaser = MediaPlayer.create(this, R.raw.lasersound);
            mpBox = MediaPlayer.create(this, R.raw.boxsound);
            mpHit = MediaPlayer.create(this, R.raw.hitsound);
            mpPowerup = MediaPlayer.create(this, R.raw.powerupsound);
        }
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayers();
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        releaseMediaPlayers();
        super.onPause();
        recreate();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fireButton) {
            gameEngine.fire();
        }
        if (v.getId() == R.id.gameView) {
            gameEngine.setStarted(true);
            gameView.setClickable(false);
        }
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent) {
        gameEngine.player.setAcc(0.5f * xPercent, 0.5f * yPercent);
    }

    @Override
    public void gameUpdate() {
        gameEngine.update(gameView.getWidth(), gameView.getHeight());
    }

    @Override
    public void gameDraw(Canvas canvas) {
        gameEngine.show(canvas);
    }

    @Override
    public void endOfGame() {
        Intent intent = new Intent(this, GameoverActivity.class);
        intent.putExtra("points", gameEngine.getPoints());
        startActivity(intent);
        onDestroy();
    }

    @Override
    public boolean gameOver() {
        return gameEngine.gameOver();
    }

    @Override
    public void laserSound() {
        if (mpLaser != null) {
            mpLaser.seekTo(0);
            mpLaser.start();
        }
    }

    @Override
    public void boxSound() {
        if (mpBox != null) {
            mpBox.seekTo(0);
            mpBox.start();
        }
    }

    @Override
    public void hitSound() {
        if (mpHit != null) {
            mpHit.seekTo(0);
            mpHit.start();
        }
    }

    @Override
    public void powerupSound() {
        if (mpPowerup != null) {
            mpPowerup.seekTo(0);
            mpPowerup.start();
        }
    }

    private void releaseMediaPlayers() {
        if (mpLaser != null) {
            mpLaser.release();
            mpLaser = null;
        }
        if (mpBox != null) {
            mpBox.release();
            mpBox = null;
        }
        if (mpHit != null) {
            mpHit.release();
            mpHit = null;
        }
        if (mpPowerup != null) {
            mpPowerup.release();
            mpPowerup = null;
        }
    }
}
