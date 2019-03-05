package de.androidmika.pommesmann.Activities;

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


import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.Item;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;

public class ShopActivity extends Activity {

    private ShopDatabaseHelper dbHelper;
    private LinearLayout itemContainer;

    private ArrayList<View> itemViews;
    private ArrayList<String> itemNames;
    private ArrayList<String> itemDescriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        itemViews = new ArrayList<>();
        itemNames = new ArrayList<>();
        itemDescriptions = new ArrayList<>();

        dbHelper = ShopDatabaseHelper.getInstance(this);

        itemContainer = findViewById(R.id.itemContainer);
        fillShop();

    }

    private void fillShop() {
        LayoutInflater inflater = getLayoutInflater();

        View healthPowerupView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(healthPowerupView,
                ShopHelper.HEALTH_POWERUP,
                ShopHelper.HEALTH_POWERUP_DESCRIPTION);
        itemViews.add(healthPowerupView);
        itemNames.add(ShopHelper.HEALTH_POWERUP);
        itemDescriptions.add(ShopHelper.HEALTH_POWERUP_DESCRIPTION);

        View laserPowerupView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(laserPowerupView,
                ShopHelper.LASER_POWERUP,
                ShopHelper.LASER_POWERUP_DESCRIPTION);
        itemViews.add(laserPowerupView);
        itemNames.add(ShopHelper.LASER_POWERUP);
        itemDescriptions.add(ShopHelper.LASER_POWERUP_DESCRIPTION);

        View powerupChanceView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(powerupChanceView,
                ShopHelper.POWERUP_CHANCE,
                ShopHelper.POWERUP_CHANCE_DESCRIPTION);
        itemViews.add(powerupChanceView);
        itemNames.add(ShopHelper.POWERUP_CHANCE);
        itemDescriptions.add(ShopHelper.POWERUP_CHANCE_DESCRIPTION);

        View secretOfPommesmannView = inflater
                .inflate(R.layout.shop_item, itemContainer, false);
        addItemView(secretOfPommesmannView,
                ShopHelper.SECRET_OF_POMMESMANN,
                ShopHelper.SECRET_OF_POMMESMANN_DESCRIPTION);
    }

    private void updateShop() {
        for (int i = 0; i < itemViews.size(); i++) {
            setItemLayout(itemViews.get(i), itemNames.get(i), itemDescriptions.get(i));
        }
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
        CharSequence text = getResources().getString(R.string.coinsToast);
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }

    private void buyDialog(final View view, final Item item, final String name, final String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

        CharSequence message = "Buy " + item.getName() + " Level " + (item.getLevel()+1) +
                " for " + item.getPrice() + "Coins?";
        builder.setMessage(message);

        builder.setNegativeButton(getResources().getString(R.string.dialogNegative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.dialogPositive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buyItem(view, item, name, description);
                buyToast(item);
            }
        });

        final AlertDialog buyDialog = builder.create();

        buyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button negativeButton = buyDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setBackgroundResource(R.drawable.oval_selector);
                negativeButton.setTextColor(Color.WHITE);
                Button positiveButton = buyDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setBackgroundResource(R.drawable.oval_selector);
                positiveButton.setTextColor(Color.WHITE);
            }
        });

        buyDialog.show();
    }

    private void addItemView(View view, String name, String description) {
        setItemLayout(view, name, description);
        itemContainer.addView(view);
    }

    private void setItemLayout(View view, String name, String description) {
        Item item = dbHelper.getItemByName(name);
        if (App.getHighscore() < ShopHelper.getRestrictionByName(name)) {
            restrictedPowerup(view, name, description);
        } else if (item.getLevel() >= ShopHelper.MAX_LEVEL &&
                    !item.getName().equals(ShopHelper.SECRET_OF_POMMESMANN)) {
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
        buyButton.setVisibility(View.VISIBLE);
        view.setBackgroundColor(getResources().getColor(R.color.standardItem));
        view.setAlpha(1f);
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
        itemLevelTextView.setText(getResources().getString(R.string.itemMaxLevel));
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
        if (!item.getName().equals(ShopHelper.SECRET_OF_POMMESMANN)) {
            item.setPrice(item.getPrice() + ShopHelper.PRICE_INCREASE);
        }
        dbHelper.addOrUpdateItem(item);
        secretOfPommesmann(item);

        showCoinsTextView();
        setItemLayout(view, name, description);
    }

    private void secretOfPommesmann(Item item) {
        if (item.getName().equals(ShopHelper.SECRET_OF_POMMESMANN)) {
            ShopDatabaseHelper.deleteDatabase(this);
            dbHelper = null;
            dbHelper = ShopDatabaseHelper.getInstance(this);
            dbHelper.addOrUpdateItem(item);
            updateShop();
            App.setHighscore(0);
            secretDialog(item);
        }
    }

    private void secretDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

        CharSequence message = getResources().getString(R.string.app_name) +
                " has been reset! Difficulty is now on level " + item.getLevel() + "!";
        builder.setMessage(message);

        builder.setPositiveButton(getResources().getString(R.string.dialogPositive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog secretDialog = builder.create();

        secretDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = secretDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setBackgroundResource(R.drawable.oval_selector);
                positiveButton.setTextColor(Color.WHITE);
            }
        });

        secretDialog.show();
    }

    public void buyToast(Item item) {
        CharSequence text = "Bought " + item.getName() + " Level " + item.getLevel() + "!";
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }
}
