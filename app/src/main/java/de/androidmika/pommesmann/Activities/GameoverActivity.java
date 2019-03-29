package de.androidmika.pommesmann.Activities;

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
import android.widget.Toast;

import java.util.List;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.Item;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;

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

        int highscore = App.getHighscore();
        points = getIntent().getIntExtra("points", 0);
        App.setCoins(points);

        if (points > highscore) {
            unlockedPowerups(points, highscore);
            newHighscore();
        }

        scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText(Integer.toString(points));

        mainMenuButton = findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(this);
        restartButton = findViewById(R.id.restartButton);
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.restartButton) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            App.showToast();
        }
        if (v.getId() == R.id.submitNameButton) {
            EditText nameEditText = findViewById(R.id.nameEditText);
            String name = nameEditText.getText().toString().trim();
            App.setHighscoreName(name);
            submitNameButton.setVisibility(View.GONE);
        }
    }




    private void newHighscore() {
        if (App.getSound()) {
            mp = MediaPlayer.create(this, R.raw.highscoresound);
            mp.start();
        }
        LinearLayout highscoreLayout = findViewById(R.id.highscoreLayout);
        highscoreLayout.setVisibility(View.VISIBLE);

        submitNameButton = findViewById(R.id.submitNameButton);
        submitNameButton.setOnClickListener(this);

        App.setHighscore(points);
        App.setHighscoreName(""); // set name to nothing in case user doesn't submit his name
    }

    private void unlockedPowerups(int points, int highscore) {
        ShopDatabaseHelper dbHelper = ShopDatabaseHelper.getInstance(this);
        List<Item> items = dbHelper.getAllItems();

        for (Item item : items) {
            if (points >= item.getRestriction()) {
                if (highscore < item.getRestriction()) {
                    CharSequence text = "You unlocked " + item.getName() + "!";
                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(this, text, duration).show();

                    // only show one toast
                    return;
                }
            }
        }
    }



    private void showCoinsTextView() {
        int temp = App.getCoins();

        if (temp > 0) {
            TextView coinsTextView = findViewById(R.id.coinsTextView);
            coinsTextView.setText(Integer.toString(temp) + " Coins");
            coinsTextView.setVisibility(View.VISIBLE);
            App.startSlowFadeinAnim(coinsTextView, 3000);
        }
    }
}
