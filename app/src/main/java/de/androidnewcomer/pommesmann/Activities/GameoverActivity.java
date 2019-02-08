package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;

public class GameoverActivity extends Activity implements View.OnClickListener {

    private TextView scoreTextView;
    private Button mainMenuButton;
    private Button restartButton;
    private int points;
    private Button submitNameButton;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gameover_activity);

        int highscore = getHighscore();
        points = getIntent().getIntExtra("points", 0);
        setCoins(points);

        if (points > highscore) newHighscore(points);

        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText(Integer.toString(points));

        mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(this);
        restartButton = (Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View v = findViewById(R.id.mainLayout);
        App.startFadeinAnim(v);
        showCoinsTextView();
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mainMenuButton) {
            View fade = findViewById(R.id.mainLayout);
            App.startFadeoutAnim(fade);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.restartButton) {
            View fade = findViewById(R.id.mainLayout);
            App.startFadeoutAnim(fade);
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            App.showToast();
        }
        if (v.getId() == R.id.submitNameButton) {
            EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
            String name = nameEditText.getText().toString().trim();
            setHighscoreName(name);
            submitNameButton.setVisibility(View.GONE);
        }
    }

    private int getHighscore() {
        SharedPreferences pref = getSharedPreferences(App.SPgame, 0);
        return pref.getInt(App.SPhighscore, 0);
    }

    private void setHighscore(int newHighscore) {
        SharedPreferences pref = getSharedPreferences(App.SPgame, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(App.SPhighscore, newHighscore);
        editor.apply();
    }


    private void setHighscoreName(String name) {
        SharedPreferences pref = getSharedPreferences(App.SPgame, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(App.SPhighscorename, name);
        editor.apply();
    }

    private void newHighscore(int highscore) {
        if (App.getSound()) {
            mp = MediaPlayer.create(this, R.raw.highscoresound);
            mp.start();
        }
        LinearLayout highscoreLayout = findViewById(R.id.highscoreLayout);
        highscoreLayout.setVisibility(View.VISIBLE);

        submitNameButton = findViewById(R.id.submitNameButton);
        submitNameButton.setOnClickListener(this);

        setHighscore(points);
        setHighscoreName(""); // set name to nothing in case user doesn't submit his name
    }

    private void setCoins(int points) {
        SharedPreferences pref = getSharedPreferences(App.SPgame, 0);
        int temp = pref.getInt(App.SPcoins, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(App.SPcoins, temp + points);
        editor.apply();
    }

    private int getCoins() {
        SharedPreferences pref = getSharedPreferences(App.SPgame, 0);
        return pref.getInt(App.SPcoins, 0);
    }

    private void showCoinsTextView() {
        int temp = getCoins();

        if (temp > 0) {
            TextView coinsTextView = findViewById(R.id.coinsTextView);
            coinsTextView.setText(Integer.toString(temp) + "coins");
            coinsTextView.setVisibility(View.VISIBLE);
            App.startSlowFadeinAnim(coinsTextView, 3000);
        }
    }
}
