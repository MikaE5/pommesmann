package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopDatabaseHelper;

public class ShopActivity extends Activity implements View.OnClickListener {

    ShopDatabaseHelper dbHelper;
    private final String HEALTH_POWERUP = "HEALTH_POWERUP";
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(this);

        dbHelper = ShopDatabaseHelper.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        App.startFadeinAnim(mainLayout);
        showCoinsTextView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShopDatabaseHelper.deleteDatabase(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buyButton) {
            dbHelper.addOrUpdateItem(HEALTH_POWERUP, 1);
            TextView testTextView = findViewById(R.id.testTextView);
            testTextView.setText(HEALTH_POWERUP + " level: " + Integer.toString(1));
        }
    }


    private int getCoins() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_COINS, 0);
    }

    private void showCoinsTextView() {
        int temp = getCoins();

        if (temp >= 0) {
            TextView coinsTextView = findViewById(R.id.coinsTextView);
            coinsTextView.setText(Integer.toString(temp) + "coins");
            coinsTextView.setVisibility(View.VISIBLE);
            App.startSlowFadeinAnim(coinsTextView, 3000);
        }
    }
}
