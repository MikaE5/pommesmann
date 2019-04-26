package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.Firebase.FireContract;
import de.androidmika.pommesmann.Firebase.FireManager;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;

public class MainActivity extends Activity implements View.OnClickListener,
        FireManager.DataInterface, FireManager.UIInterface {

    private FireManager manager;

    // ShopdatabaseHelper
    private ShopDatabaseHelper dbHelper;


    private Button startButton;
    private Button tutorialButton;
    private Button shopButton;
    private Button submitHighscoreButton;
    private CheckBox soundCheckBox;

    private Animation rotateAnim;
    private final int LOW_SCORE = 10;
    private Handler animHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new FireManager(this);
        dbHelper = ShopDatabaseHelper.getInstance(this);

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
                manager.updateData(App.getHighscore());
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

            // check if highscore is up to date
            manager.isHighscoreUpdated();
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
    public void receivedHighscore(double score) {
        if (!manager.userExists() || score < App.getHighscore()) {
            submitHighscoreButton.setClickable(true);
            submitHighscoreButton.setOnClickListener(this);
            submitHighscoreButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideButton() {
        submitHighscoreButton.setClickable(false);
        submitHighscoreButton.setVisibility(View.GONE);
    }
}
