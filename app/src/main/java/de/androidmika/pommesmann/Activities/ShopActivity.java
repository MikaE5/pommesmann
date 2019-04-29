package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

    private MediaPlayer mpBuy;

    private ArrayList<View> itemViews;
    private ArrayList<String> itemNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        itemViews = new ArrayList<>();
        itemNames = new ArrayList<>();

        dbHelper = ShopDatabaseHelper.getInstance(this);

        itemContainer = findViewById(R.id.itemContainer);
        fillShop();

        if (App.getSound()) {
            mpBuy = MediaPlayer.create(this, R.raw.buysound);
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
        if (mpBuy != null) {
            mpBuy.release();
            mpBuy = null;
        }
    }


    private void fillShop() {
        LayoutInflater inflater = getLayoutInflater();

        for (Item item : ShopHelper.ITEMS) {
            View view = inflater.inflate(R.layout.shop_item, itemContainer, false);
            addItemView(view, item.getName());
            itemViews.add(view);
            itemNames.add(item.getName());
        }
    }

    private void updateShop() {
        for (int i = 0; i < itemViews.size(); i++) {
            setItemLayout(itemViews.get(i), itemNames.get(i));
        }
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
        coinsTextView.setText(Integer.toString(temp) + " Coins");
        App.startSlowFadeinAnim(coinsTextView, 3000);
    }

    private void coinsToast() {
        CharSequence text = getResources().getString(R.string.coinsToast);
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }

    private void buyDialog(final View view, final Item item) {
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
                buyItem(view, item);
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

    private void addItemView(View view, String name) {
        setItemLayout(view, name);
        itemContainer.addView(view);
    }

    private void setItemLayout(View view, String name) {
        Item item = dbHelper.getItemByName(name);
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemDescriptionTextView = view.findViewById(R.id.itemDescriptionTextView);
        CheckBox itemCheckBox = view.findViewById(R.id.itemCheckBox);
        itemNameTextView.setText(item.getName());
        itemDescriptionTextView.setText(item.getDescription());
        itemCheckBox.setChecked(item.getActive());

        if (App.getLevelscore() < item.getRestriction()) {
            restrictedPowerup(view, item);
        } else if (item.getLevel() >= ShopHelper.MAX_LEVEL &&
                    !item.getName().equals(ShopHelper.SECRET_OF_POMMESMANN)) {
            maxLevelPowerup(view);
            setItemCheckBox(view, item);
        } else {
            standardItemLayout(view, item);
            setItemBuyButton(view, item);
            setItemCheckBox(view, item);
        }
        setItemImage(view, item);
    }

    private void standardItemLayout(View view, Item item) {
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);

        String text = "Level " + Integer.toString(item.getLevel() + 1) + "\n"
                    + item.getPrice() + " Coins";
        itemLevelTextView.setText(text);
        buyButton.setText(getResources().getString(R.string.itemBuyButton));
        buyButton.setVisibility(View.VISIBLE);
        view.setBackgroundColor(getResources().getColor(R.color.standardItem));
        view.setAlpha(1f);
    }

    private void maxLevelPowerup(View view) {
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        Button buyButton = view.findViewById(R.id.buyButton);
        CheckBox itemCheckBox = view.findViewById(R.id.itemCheckBox);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemCheckBox.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.itemLevelTextView);
        itemCheckBox.setLayoutParams(params);

        buyButton.setVisibility(View.GONE);
        view.setBackgroundColor(getResources().getColor(R.color.maxLevelItem));
        view.setAlpha(0.8f);
        itemLevelTextView.setText(getResources().getString(R.string.itemMaxLevel));
    }

    private void restrictedPowerup(View view, Item item) {
        int restriction = item.getRestriction();
        TextView itemLevelTextView = view.findViewById(R.id.itemLevelTextView);
        CheckBox itemCheckBox = view.findViewById(R.id.itemCheckBox);
        Button buyButton = view.findViewById(R.id.buyButton);

        buyButton.setVisibility(View.GONE);
        itemCheckBox.setVisibility(View.GONE);

        view.setBackgroundColor(getResources().getColor(R.color.restrictedItem));
        view.setAlpha(0.8f);

        itemLevelTextView.setText("score of " + restriction + " to unlock");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemLevelTextView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        itemLevelTextView.setLayoutParams(params);
    }


    private void setItemBuyButton(final View view, final Item item) {
        Button buyButton = view.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enoughCoins(item.getPrice())) {
                    buyDialog(view, item);
                } else {
                    coinsToast();
                }
            }
        });
    }

    private void setItemCheckBox(final View view, final Item item) {
        final CheckBox itemCheckBox = view.findViewById(R.id.itemCheckBox);
        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = itemCheckBox.isChecked();
                item.setActive(active);
                dbHelper.addOrUpdateItem(item);
            }
        });
    }

    private void setItemImage(View view, Item item) {
        ImageView itemImage = view.findViewById(R.id.itemImage);
        Bitmap image = BitmapFactory.decodeResource(getResources(), item.getResource());

        if (image == null) {
            itemImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            itemImage.setImageBitmap(image);
        }
    }

    public void buyItem(View view, Item item) {
        int coins = getCoins();
        setCoins(coins - item.getPrice());
        buySound();

        item.setLevel(item.getLevel() + 1);
        if (!item.getName().equals(ShopHelper.SECRET_OF_POMMESMANN)) {
            if (item.getName().equals(ShopHelper.FAST_LASER)) {
                item.setPrice(item.getPrice() + 2 * ShopHelper.PRICE_INCREASE);
            } else {
                item.setPrice(item.getPrice() + ShopHelper.PRICE_INCREASE);
            }
        }
        dbHelper.addOrUpdateItem(item);
        secretOfPommesmann(item);

        showCoinsTextView();
        setItemLayout(view, item.getName());
    }

    public void buySound() {
        if (mpBuy != null) {
            mpBuy.seekTo(0);
            mpBuy.start();
        }
    }

    private void secretOfPommesmann(Item item) {
        if (item.getName().equals(ShopHelper.SECRET_OF_POMMESMANN)) {
            ShopDatabaseHelper.deleteDatabase(this);
            dbHelper = null;
            dbHelper = ShopDatabaseHelper.getInstance(this);
            dbHelper.addOrUpdateItem(item);
            updateShop();
            App.setLevelscore(-1);
            secretDialog(item);
            updateShop();
        }
    }

    private void secretDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

        CharSequence message = "Secret #" + item.getLevel() + ": The next secret is the most interesting secret! \n" +
                getResources().getString(R.string.app_name) +
                " has been reset! Difficulty is now on level " + item.getLevel() + "!\n" +
                "Now you have a chance to get more Coins for your points!";
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
