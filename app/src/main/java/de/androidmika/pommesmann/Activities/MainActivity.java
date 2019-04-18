package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import de.androidmika.pommesmann.R;

public class MainActivity extends Activity implements View.OnClickListener {

    // Firebase Authentication
    private FirebaseAuth auth;
    // Firestore Database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button startButton;
    private Button tutorialButton;
    private Button shopButton;
    private Button aboutButton;
    private CheckBox soundCheckBox;
    private CheckBox leftHandedCheckBox;

    private Animation rotateAnim;
    private final int LOW_SCORE = 10;
    private Handler animHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        // initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            signInAnonymously();
        } else {
            Toast.makeText(MainActivity.this, "already signed in", Toast.LENGTH_LONG)
                    .show();
        }

        setContentView(R.layout.main_activity);

        showChooseNameDialog();

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        tutorialButton = findViewById(R.id.tutorialButton);
        tutorialButton.setOnClickListener(this);
        shopButton = findViewById(R.id.shopButton);
        shopButton.setOnClickListener(this);
        aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(this);
        soundCheckBox = findViewById(R.id.soundCheckBox);
        soundCheckBox.setChecked(App.getSound()); // set volume in app accordingly
        soundCheckBox.setOnClickListener(this);
        leftHandedCheckBox = findViewById(R.id.leftHandedCheckBox);
        leftHandedCheckBox.setChecked(App.getLeftHanded()); // set leftHanded in app accordingly
        leftHandedCheckBox.setOnClickListener(this);

        if (App.getHighscore() < LOW_SCORE) {
            rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        }
    }

    private void signInAnonymously() {
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("SignInAnonymously", "Sign in successfull!");
                    Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Log.w("SignInAnonymously", "Sign in failed");
                    Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void addData() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "test");
        data.put("score", 1234);
        db.collection("scores").document(auth.getUid())
                .set(data);
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
        App.startSlowFadeinAnim(leftHandedCheckBox, 2000);
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
            /*
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
            */
            addData();
        }
        if (v.getId() == R.id.shopButton) {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.soundCheckBox) {
            App.setSound(soundCheckBox.isChecked());
        }
        if (v.getId() == R.id.aboutButton) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.leftHandedCheckBox) {
            App.setLeftHanded(leftHandedCheckBox.isChecked());
        }

    }


    private void showHighscore() {
        int highscore = App.getHighscore();
        String name = App.getHighscoreName();

        if (highscore != 0) {
            LinearLayout highscoreLayout = findViewById(R.id.highscoreLayout);
            TextView currentHighscoreTextView = findViewById(R.id.currentHighscoreTextView);
            if (!name.equals("")) {
                currentHighscoreTextView.setText(name + ": " + Integer.toString(highscore));
            } else {
                currentHighscoreTextView.setText(Integer.toString(highscore));
            }
            highscoreLayout.setVisibility(View.VISIBLE);
            App.startSlowFadeinAnim(highscoreLayout, 3000);
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

    private void showChooseNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.choosename_dialog, null);
        builder.setView(dialogView);

        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.chooseNameDialogTitle);

        Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
