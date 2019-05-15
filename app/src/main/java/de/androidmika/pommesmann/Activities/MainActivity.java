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
import android.widget.Toast;

import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.Firebase.FireManager;
import de.androidmika.pommesmann.Firebase.FireUserInterface;
import de.androidmika.pommesmann.R;

public class MainActivity extends Activity implements View.OnClickListener,
        FireManager.UIInterface, FireUserInterface.FireConnection {

    private FireManager manager;
    private FireUserInterface fireUserInterface;
    

    private Button startButton;
    private Button tutorialButton;
    private Button shopButton;
    private Button submitHighscoreButton;
    private Button highscoresButton;
    private CheckBox soundCheckBox;

    private Animation rotateAnim;
    private final int LOW_SCORE = 10;
    private Handler animHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLevelscore();
        manager = new FireManager(this);
        fireUserInterface = new FireUserInterface(this);



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
        highscoresButton = findViewById(R.id.highscoresButton);

        soundCheckBox = findViewById(R.id.soundCheckBox);
        soundCheckBox.setChecked(App.getSound()); // set volume in app accordingly
        soundCheckBox.setOnClickListener(this);
        findViewById(R.id.settingsButton).setOnClickListener(this);

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
            fireUserInterface.submitDialog();
        }
        if (v.getId() == R.id.highscoresButton) {
            fireUserInterface.highscoreDialog();
            manager.getTopTen();
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

                // needed if you come back to MainActivity after deleting your data in
                // SettingsActivity
                hideHighscoresButton();
            } else {
                showHighscoresButton();
            }
        }
    }

    private void showHighscoresButton() {
        highscoresButton.setClickable(true);
        highscoresButton.setOnClickListener(this);
        highscoresButton.setVisibility(View.VISIBLE);
    }

    private void hideHighscoresButton() {
        highscoresButton.setClickable(false);
        highscoresButton.setVisibility(View.GONE);
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

    private void initLevelscore() {
        // added levelscore after first releases
        // have to set a first value
        // if no value for levelscore is set, the current overall highscore is set for levelscore

        if (App.getLevelscore() == 0) {
            App.setLevelscore(App.getHighscore());
        }
    }



    // UIInterface from FireManager
    @Override
    public void hideButton() {
        submitHighscoreButton.setClickable(false);
        submitHighscoreButton.setVisibility(View.GONE);
        showHighscoresButton();
    }

    @Override
    public void setHint(String name) {
    }

    @Override
    public void chooseDifferentName(boolean firstSignIn) {
        fireUserInterface.differentNameDialog(firstSignIn);
    }

    @Override
    public void fillHighscoreDialog(ArrayList<String> scores, ArrayList<String> names) {
        fireUserInterface.fillHighscoreDialog(scores, names);
    }


    // ConnectionInterface from FireUserInterface
    @Override
    public void login(String name) {
        manager.signIn(name);
    }

    @Override
    public void dummyLogin() {
        manager.dummyLogin();
    }

    @Override
    public void differentName(String name) {
        if (manager.userExists()) {
            manager.validateName(name, false);
        } else {
            manager.validateName(name, true);
        }
    }
}
