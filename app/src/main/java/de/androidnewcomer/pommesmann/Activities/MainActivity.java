package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button startButton;
    private CheckBox soundCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main_activity);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        soundCheckBox = findViewById(R.id.soundCheckBox);
        soundCheckBox.setChecked(App.getSound()); // set volume in app accordingly
        soundCheckBox.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View v = findViewById(R.id.mainLayout);
        App.startFadeinAnim(v);
        App.startSlowFadeinAnim(startButton, 1000);
        App.startSlowFadeinAnim(soundCheckBox, 2000);
        showHighscore();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startButton) {
            View fade = findViewById(R.id.mainLayout);
            App.startFadeoutAnim(fade);
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("sound", soundCheckBox.isChecked());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                App.showToast();
            } else {
                startActivity(intent);
                App.showToast();
            }
        }
        if (v.getId() == R.id.soundCheckBox) {
            App.setSound(soundCheckBox.isChecked());
        }

    }

    private int getHighscore() {
        SharedPreferences pref = getSharedPreferences("game", 0);
        return pref.getInt("highscore", 0);
    }

    private String getHighscoreName() {
        SharedPreferences pref = getSharedPreferences("game", 0);
        return pref.getString("highscore_name", "");
    }

    private void showHighscore() {
        int highscore = getHighscore();
        String name = getHighscoreName();

        if (highscore != 0) {
            LinearLayout highscoreLayout = findViewById(R.id.highscoreLayout);
            TextView currentHighscoreTextView = findViewById(R.id.currentHighscoreTextView);
            if (!name.equals("") && !name.equals(getResources().getString(R.string.nameEditText))) {
                currentHighscoreTextView.setText(name + ": " + Integer.toString(highscore));
            } else {
                currentHighscoreTextView.setText(Integer.toString(highscore));
            }
            highscoreLayout.setVisibility(View.VISIBLE);
            App.startSlowFadeinAnim(highscoreLayout, 3000);
        }
    }

}
