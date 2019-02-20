package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;
import de.androidnewcomer.pommesmann.ShopDatabase.Item;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidnewcomer.pommesmann.ShopDatabase.ShopHelper;

public class ShopActivity extends Activity {

    private ShopDatabaseHelper dbHelper;
    private LinearLayout itemContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        dbHelper = ShopDatabaseHelper.getInstance(this);

        itemContainer = findViewById(R.id.itemContainer);
        LayoutInflater inflater = getLayoutInflater();

        View healthPowerupView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(healthPowerupView, ShopHelper.HEALTH_POWERUP,
                ShopHelper.HEALTH_POWERUP_DESCRIPTION);

        View powerupChanceView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(powerupChanceView, ShopHelper.POWERUP_CHANCE,
                ShopHelper.POWERUP_CHANCE_DESCRIPTION);
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


    private int getCoins() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_COINS, 0);
    }

    private void setCoins(int coins) {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(App.SP_COINS, coins);
        editor.apply();
    }

    private void showCoinsTextView() {
        int temp = getCoins();
        if (temp < 0) temp = 0;

        TextView coinsTextView = findViewById(R.id.coinsTextView);
        coinsTextView.setText(Integer.toString(temp) + "coins");
        App.startSlowFadeinAnim(coinsTextView, 3000);
    }

    private void addItemView(View newItem, String name, String description) {
        setItemLayout(newItem, name, description);
        itemContainer.addView(newItem);
    }

    private void setItemLayout(View newItem, String name, String description) {
        TextView itemNameTextView = newItem.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = newItem.findViewById(R.id.itemDescriptionTextView);
        TextView itemLevelTextView = newItem.findViewById(R.id.itemLevelTextView);
        Button buyButton = newItem.findViewById(R.id.buyButton);

        Item item = dbHelper.getItemByName(name);
        // dummy variables for onClick-method
        final View copyView = newItem;
        final Item copyItem = item;
        final String copyName = name;
        final String copyDescription = description;

        itemNameTextView.setText(name);
        itemDescriptionTextView.setText(description);
        itemLevelTextView.setText("Level " + Integer.toString(item.getLevel()+1));
        buyButton.setText(Integer.toString(item.getPrice()) + "coins");
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyItem(copyView, copyItem, copyName, copyDescription);
            }
        });
    }

    public void buyItem(View newItem, Item item, String name, String description) {
        int coins = getCoins();
        int price = item.getPrice();

        if (coins >= price) {
            setCoins(coins - price);

            item.setLevel(item.getLevel()+1);
            item.setPrice(item.getPrice()+50);
            dbHelper.addOrUpdateItem(item);

            showCoinsTextView();
            setItemLayout(newItem, name, description);

            CharSequence text = "You bought " + item.getName() + " Level " + item.getLevel() + "!";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(this, text, duration).show();
        } else {
            CharSequence text = "Not enough Coins!";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(this, text, duration).show();
        }
    }
}
