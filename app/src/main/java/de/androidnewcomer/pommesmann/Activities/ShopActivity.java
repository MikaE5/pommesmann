package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        addItemView(healthPowerupView,
                    ShopHelper.HEALTH_POWERUP,
                    ShopHelper.HEALTH_POWERUP_DESCRIPTION);

        View laserPowerupView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(laserPowerupView,
                    ShopHelper.LASER_POWERUP,
                    ShopHelper.LASER_POWERUP_DESCRIPTION);

        View powerupChanceView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(powerupChanceView,
                    ShopHelper.POWERUP_CHANCE,
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

    private boolean enoughCoins(int price) {
        int coins = getCoins();
        return coins >= price;
    }

    private void showCoinsTextView() {
        int temp = getCoins();
        if (temp < 0) temp = 0;

        TextView coinsTextView = findViewById(R.id.coinsTextView);
        coinsTextView.setText(Integer.toString(temp) + "Coins");
        App.startSlowFadeinAnim(coinsTextView, 3000);
    }

    private void coinsToast() {
        CharSequence text = "Not enough Coins!";
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }

    private void buyDialog(final View view, final Item item, final String name, final String description) {
        DialogInterface.OnClickListener positiveListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buyItem(view, item, name, description);
                        buyToast(item);
                    }
                };

        DialogInterface.OnClickListener negativeListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence message = "Buy " + item.getName() + " Level " + (item.getLevel()+1) +
                " for " + item.getPrice() + "Coins?";
        builder.setMessage(message);
        builder.setPositiveButton("BUY", positiveListener);
        builder.setNegativeButton("CANCEL", negativeListener);
        builder.show();
    }

    private void addItemView(View view, String name, String description) {
        if (name == ShopHelper.LASER_POWERUP && getHighscore() < ShopHelper.LASER_POWERUP_RESTRICTION) {
            restrictedLaserPowerup(view, name, description);
        } else {
            setItemLayout(view, name, description);
            setItemBuyButton(view, name, description);
        }
        itemContainer.addView(view);
    }

    private void setItemLayout(View view, String name, String description) {
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);

        Item item = dbHelper.getItemByName(name);

        itemNameTextView.setText(name);
        itemDescriptionTextView.setText(description);
        if (item.getLevel() < 5) {
            itemLevelTextView.setText("Level " + Integer.toString(item.getLevel() + 1));
            buyButton.setText(Integer.toString(item.getPrice()) + "coins");
        } else {
            buyButton.setVisibility(View.GONE);
            ImageView hiddenImageView = view.findViewById(R.id.hiddenImageView);
            hiddenImageView.setVisibility(View.VISIBLE);
            itemLevelTextView.setText("MAX LEVEL");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemLevelTextView.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            itemLevelTextView.setLayoutParams(params);
        }
    }

    private void restrictedLaserPowerup(View view, String name, String description) {
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);
        ImageView hiddenImageView = view.findViewById(R.id.hiddenImageView);

        itemNameTextView.setText(name);
        itemDescriptionTextView.setText(description);
        buyButton.setVisibility(View.GONE);
        hiddenImageView.setVisibility(View.VISIBLE);

        itemLevelTextView.setText("Highscore of " + ShopHelper.LASER_POWERUP_RESTRICTION + " to unlock");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemLevelTextView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        itemLevelTextView.setLayoutParams(params);
    }


    private void setItemBuyButton(final View view, final String name, final String description) {
        final Item item = dbHelper.getItemByName(name);
        Button buyButton = view.findViewById(R.id.buyButton);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enoughCoins(item.getPrice())) {
                    buyDialog(view, item, name, description);
                } else {
                    coinsToast();
                }
            }
        });
    }

    public void buyItem(View view, Item item, String name, String description) {
        int coins = getCoins();
        setCoins(coins - item.getPrice());

        item.setLevel(item.getLevel() + 1);
        item.setPrice(item.getPrice() + ShopHelper.PRICE_INCREASE);
        dbHelper.addOrUpdateItem(item);

        showCoinsTextView();
        setItemLayout(view, name, description);
        if (item.getLevel() >= 5) {
            setItemBuyButton(view, name, description);
        }
    }

    public void buyToast(Item item) {
        CharSequence text = "Bought " + item.getName() + " Level " + item.getLevel() + "!";
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }

    private int getHighscore() {
        SharedPreferences pref = getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_HIGHSCORE, 0);
    }
}
