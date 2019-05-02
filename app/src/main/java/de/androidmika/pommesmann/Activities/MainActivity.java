package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.Firebase.FireManager;
import de.androidmika.pommesmann.R;

public class MainActivity extends Activity implements View.OnClickListener,
        FireManager.DataInterface, FireManager.UIInterface {

    private FireManager manager;
    

    private Button startButton;
    private Button tutorialButton;
    private Button shopButton;
    private Button submitHighscoreButton;
    private TextView highscoreListTextView;
    private CheckBox soundCheckBox;

    private Animation rotateAnim;
    private final int LOW_SCORE = 10;
    private Handler animHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLevelscore();

        manager = new FireManager(this);
        highscoreListTextView = findViewById(R.id.highscoreListTextView);
        manager.initHighscoreList();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.main_activity);


        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        tutorialButton = findViewById(R.id.tutorialButton);
        tutorialButton.setOnClickListener(this);
        shopButton = findViewById(R.id.shopButton);
        shopButton.setOnClickListener(this);
        submitHighscoreButton = findViewById(R.id.submitHighscoreButton);
        soundCheckBox = findViewById(R.id.soundCheckBox);
        soundCheckBox.setChecked(App.getSound()); // set volume in app accordingly
        soundCheckBox.setOnClickListener(this);
        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);


        if (App.getHighscore() < LOW_SCORE) {
            rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        View v = findViewById(R.id.mainLayout);
        App.startFadeinAnim(v);
        App.startSlowFadeinAnim(startButton, 1000);
        App.startSlowFadeinAnim(tutorialButton, 1000);
        App.startSlowFadeinAnim(shopButton, 1000);
        App.startSlowFadeinAnim(soundCheckBox, 2000);
        showHighscore();
        showCoinsTextView();
        animateTutorialButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startButton) {
            Intent intent = new Intent(this, GameActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                App.showToast();
            } else {
                startActivity(intent);
                App.showToast();
            }
        }
        if (v.getId() == R.id.tutorialButton) {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.shopButton) {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.submitHighscoreButton) {
            if (manager.userExists()) {
                manager.updateScore(App.getHighscore());
                submitHighscoreButton.setClickable(false);
                submitHighscoreButton.setVisibility(View.GONE);
            } else {
                manager.showChooseNameDialog(this);
            }
        }
        if (v.getId() == R.id.soundCheckBox) {
            App.setSound(soundCheckBox.isChecked());
        }
        if (v.getId() == R.id.settingsButton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }


    private void showHighscore() {
        int highscore = App.getHighscore();

        if (highscore != 0) {
            LinearLayout highscoreLayout = findViewById(R.id.highscoreLayout);
            TextView currentHighscoreTextView = findViewById(R.id.currentHighscoreTextView);

            currentHighscoreTextView.setText(Integer.toString(highscore));

            highscoreLayout.setVisibility(View.VISIBLE);
            App.startSlowFadeinAnim(highscoreLayout, 3000);

            // check if user is not signed in yet
            if (!manager.userExists()) {
                submitHighscoreButton.setClickable(true);
                submitHighscoreButton.setOnClickListener(this);
                submitHighscoreButton.setVisibility(View.VISIBLE);
            }

        }
    }


    private void showCoinsTextView() {
        int temp = App.getCoins();
        if (temp < 0) temp = 0;


        TextView coinsTextView = findViewById(R.id.coinsTextView);
        coinsTextView.setText(Integer.toString(temp) + " Coins");
        App.startSlowFadeinAnim(coinsTextView, 3000);
    }

    private void animateTutorialButton() {
        if (rotateAnim != null) {
            animHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tutorialButton.startAnimation(rotateAnim);
                }
            }, 1000 * 2);
        }
    }


    @Override
    public void highscoreTopTen(ArrayList<String> scores, ArrayList<String> names) {
        int max = Math.max(names.size(), scores.size());
        String text = "";
        for (int i = 0; i < max; i++) {
            text += names.get(i) + " " + scores.get(i) + "\n";
        }
        highscoreListTextView.setText(text);
    }


    @Override
    public void userScoreName(String name, String score) {
        highscoreListTextView.setText(name + " " + score);
    }

    @Override
    public void hideButton() {
        submitHighscoreButton.setClickable(false);
        submitHighscoreButton.setVisibility(View.GONE);
    }

    private void initLevelscore() {
        // added levelscore after first releases
        // have to set a first value
        // if no value for levelscore is set, the current overall highscore is set for levelscore

        if (App.getLevelscore() == 0) {
            App.setLevelscore(App.getHighscore());
        }
    }
}
