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

    private Button buyButton;
    private ShopDatabaseHelper dbHelper;

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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buyButton) {

        }
    }


    private int getCoins() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_COINS, 0);
    }

    private void showCoinsTextView() {
        int temp = getCoins();
        TextView coinsTextView = findViewById(R.id.coinsTextView);

        if (temp < 0) temp = 0;

        coinsTextView.setText(Integer.toString(temp) + "coins");
        App.startSlowFadeinAnim(coinsTextView, 3000);
    }
}
