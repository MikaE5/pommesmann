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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button startButton;
    private Button shopButton;
    private CheckBox soundCheckBox;
    private CheckBox leftHandedCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main_activity);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        shopButton = findViewById(R.id.shopButton);
        shopButton.setOnClickListener(this);
        soundCheckBox = findViewById(R.id.soundCheckBox);
        soundCheckBox.setChecked(App.getSound()); // set volume in app accordingly
        soundCheckBox.setOnClickListener(this);
        leftHandedCheckBox = findViewById(R.id.leftHandedCheckBox);
        leftHandedCheckBox.setChecked(App.getLeftHanded()); // set leftHanded in app accordingly
        leftHandedCheckBox.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View v = findViewById(R.id.mainLayout);
        App.startFadeinAnim(v);
        App.startSlowFadeinAnim(startButton, 1000);
        App.startSlowFadeinAnim(soundCheckBox, 2000);
        App.startSlowFadeinAnim(leftHandedCheckBox, 2000);
        showHighscore();
        showCoinsTextView();
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
        if (v.getId() == R.id.shopButton) {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.soundCheckBox) {
            App.setSound(soundCheckBox.isChecked());
        }
        if (v.getId() == R.id.leftHandedCheckBox) {
            App.setLeftHanded(leftHandedCheckBox.isChecked());
        }

    }

    private int getHighscore() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_HIGHSCORE, 0);
    }

    private String getHighscoreName() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getString(App.SP_HIGHSCORE_NAME, "");
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

    private int getCoins() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_COINS, 0);
    }

    private void showCoinsTextView() {
        int temp = getCoins();
        if (temp < 0) temp = 0;

        TextView coinsTextView = findViewById(R.id.coinsTextView);
        coinsTextView.setText(Integer.toString(temp) + "coins");
        App.startSlowFadeinAnim(coinsTextView, 3000);
    }

}
