package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.FireContract;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.Item;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;

public class GameoverActivity extends Activity implements View.OnClickListener {


    // Firebase Authentication
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    // Firestore Database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    // Shopdatabase
    ShopDatabaseHelper dbHelper = ShopDatabaseHelper.getInstance(this);

    private int points;
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

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText(Integer.toString(points));

        Button mainMenuButton = findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(this);
        Button restartButton = findViewById(R.id.restartButton);
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
        if (v.getId() == R.id.submitHighscoreButton) {
            if (auth.getCurrentUser() != null) {
                // update Data
                Map<String, Object> data = new HashMap<>();
                data.put(FireContract.score, points);
                data.put(FireContract.level, dbHelper.getSecretOfPommesmannLevel());

                db.collection(FireContract.userCollection).document(auth.getUid())
                        .update(data);
            } else {
                signInAnonymously();
                showChooseNameDialog();
            }
        }
    }




    private void newHighscore() {
        if (App.getSound()) {
            mp = MediaPlayer.create(this, R.raw.highscoresound);
            mp.start();
        }
        LinearLayout highscoreLayout = findViewById(R.id.highscoreLayout);
        highscoreLayout.setVisibility(View.VISIBLE);

        Button submitHighscoreButton = findViewById(R.id.submitHighscoreButton);
        submitHighscoreButton.setOnClickListener(this);


        App.setHighscore(points);
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

    private void signInAnonymously() {
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("SignInAnonymously", "Sign in successfull!");
                    Toast.makeText(GameoverActivity.this, "Sign in successful", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Log.w("SignInAnonymously", "Sign in failed");
                    Toast.makeText(GameoverActivity.this, "Sign in failed", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void showChooseNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.choosename_dialog, null);
        builder.setView(dialogView);

        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.chooseNameDialogTitle);


        final EditText editName = dialogView.findViewById(R.id.editText);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                Map<String, Object> data = new HashMap<>();
                data.put(FireContract.userID, auth.getUid());
                data.put(FireContract.name, name);
                data.put(FireContract.score, points);

                // get SecretOfPommesmannLevel from Shopdatabase
                data.put(FireContract.level, dbHelper.getSecretOfPommesmannLevel());

                db.collection(FireContract.userCollection).document(auth.getUid())
                        .set(data);


                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
