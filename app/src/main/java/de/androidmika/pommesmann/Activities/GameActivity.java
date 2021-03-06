package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.GameParts.Game.CollisionManager;
import de.androidmika.pommesmann.GameParts.Game.Game;
import de.androidmika.pommesmann.GameScreen.GameView;
import de.androidmika.pommesmann.GameScreen.JoystickView;
import de.androidmika.pommesmann.R;

public class GameActivity extends Activity implements JoystickView.JoystickListener,
        View.OnClickListener, GameView.GameOperations, Game.SoundManager, CollisionManager.CollisionSoundManager {

    private GameView gameView;
    private Button fireButton;
    private Game game;


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


        game = new Game(this);
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
    protected void onRestart() {
        super.onRestart();
        recreate(); // recreate Activity because SurfaceHolder can not find Canvas
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fireButton) {
            game.fire();
        }
        if (v.getId() == R.id.gameView) {
            game.setStarted(true);
            gameView.setClickable(false);
        }
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent) {
        game.user.movePlayer(xPercent, yPercent);
    }

    @Override
    public void gameUpdate() {
        game.update(gameView.getWidth(), gameView.getHeight());
    }

    @Override
    public void gameDraw(Canvas canvas) {
        game.show(canvas);
    }

    @Override
    public void endOfGame() {
        Intent intent = new Intent(this, GameoverActivity.class);
        intent.putExtra("points", game.getPoints());
        startActivity(intent);
        onDestroy();
    }

    @Override
    public boolean gameOver() {
        return game.gameOver();
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
