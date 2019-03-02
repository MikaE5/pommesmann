package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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

        View secretOfPommesmannView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(secretOfPommesmannView,
                ShopHelper.SECRET_OF_POMMESMANN,
                ShopHelper.SECRET_OF_POMMESMANN_DESCRIPTION);
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
        setItemLayout(view, name, description);
        itemContainer.addView(view);
    }

    private void setItemLayout(View view, String name, String description) {
        if (getHighscore() < ShopHelper.getRestrictionByName(name)) {
            restrictedPowerup(view, name, description);
        } else if (dbHelper.getItemByName(name).getLevel() > ShopHelper.MAX_LEVEL) {
            maxLevelPowerup(view, name, description);
        } else {
            standardItemLayout(view, name, description);
            setItemBuyButton(view, name, description);
        }
        setItemImage(view, name);
    }

    private void standardItemLayout(View view, String name, String description) {
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);

        Item item = dbHelper.getItemByName(name);

        itemNameTextView.setText(name);
        itemDescriptionTextView.setText(description);
        itemLevelTextView.setText("Level " + Integer.toString(item.getLevel() + 1));
        buyButton.setText(Integer.toString(item.getPrice()) + "coins");
    }

    private void maxLevelPowerup(View view, String name, String description) {
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);

        itemNameTextView.setText(name);
        itemDescriptionTextView.setText(description);

        buyButton.setVisibility(View.GONE);
        view.setBackgroundColor(getResources().getColor(R.color.maxLevelItem));
        view.setAlpha(0.8f);
        itemLevelTextView.setText("MAX LEVEL");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemLevelTextView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        itemLevelTextView.setLayoutParams(params);
    }

    private void restrictedPowerup(View view, String name, String description) {
        int restriction = ShopHelper.getRestrictionByName(name);
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);

        itemNameTextView.setText(name);
        itemDescriptionTextView.setText(description);
        buyButton.setVisibility(View.GONE);
        view.setBackgroundColor(getResources().getColor(R.color.restrictedItem));
        view.setAlpha(0.8f);

        itemLevelTextView.setText("Highscore of " + restriction + " to unlock");
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

    private void setItemImage(View view, String name) {
        ImageView itemImage = view.findViewById(R.id.itemImage);
        Bitmap image = ShopHelper.getImageByName(name);

        if (image == null) {
            itemImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            itemImage.setImageBitmap(image);
        }
    }

    public void buyItem(View view, Item item, String name, String description) {
        int coins = getCoins();
        setCoins(coins - item.getPrice());

        item.setLevel(item.getLevel() + 1);
        item.setPrice(item.getPrice() + ShopHelper.PRICE_INCREASE);
        dbHelper.addOrUpdateItem(item);
        secretOfPommesmann(name);

        showCoinsTextView();
        setItemLayout(view, name, description);
    }

    private void secretOfPommesmann(String name) {
        if (name == ShopHelper.SECRET_OF_POMMESMANN) {
            ShopDatabaseHelper.deleteDatabase(this);
            dbHelper = null;
            dbHelper = ShopDatabaseHelper.getInstance(this);
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
