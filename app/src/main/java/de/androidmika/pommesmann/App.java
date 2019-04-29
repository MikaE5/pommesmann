package de.androidmika.pommesmann;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


public class App extends Application {
    private static Context mContext;
    private static boolean sound;
    private static boolean leftHanded;

    // final strings for SharedPreferences
    public final static String SP_GAME = "game";

    // overall highscore
    public final static String SP_HIGHSCORE = "highscore";
    //public final static String SP_HIGHSCORE_NAME = "highscore_name";

    // highscore in current level of Secret of Pommesmann
    public final static String SP_LEVELSCORE = "levelscore";
    public final static String SP_COINS = "overallpoints";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        sound = false;
        leftHanded = false;
    }


    public static Context getContext() {
        return mContext;
    }

    public static void setSound(boolean isSound) {
        sound = isSound;
    }

    public static boolean getSound() {
        return sound;
    }

    public static boolean getLeftHanded() { return leftHanded; }

    public static void setLeftHanded(boolean left) {
        leftHanded = left;
    }

    public static void showToast() {
        Context context = getContext();
        CharSequence text = "Have Fun!";
        int duration = Toast.LENGTH_LONG;
        Toast.makeText(context, text, duration).show();
    }

    public static void startFadeinAnim(View v) {
        Animation fadeinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        v.startAnimation(fadeinAnim);
    }

    public static void startSlowFadeinAnim(View v, int millis) {
        Animation fadeinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        fadeinAnim.setDuration(millis);
        v.startAnimation(fadeinAnim);
    }

    public static int getHighscore() {
        SharedPreferences pref = getContext().getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_HIGHSCORE, 0);
    }

    public static void setHighscore(int newHighscore) {
        SharedPreferences pref = getContext().getSharedPreferences(App.SP_GAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(App.SP_HIGHSCORE, newHighscore);
        editor.apply();
    }

    public static int getLevelscore() {
        SharedPreferences pref = getContext().getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_LEVELSCORE, 0);
    }

    public static void setLevelscore(int newLevelscore) {
        SharedPreferences pref = getContext().getSharedPreferences(App.SP_GAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(App.SP_LEVELSCORE, newLevelscore);
        editor.apply();
    }


    public static int getCoins() {
        SharedPreferences pref = getContext().getSharedPreferences(App.SP_GAME, 0);
        return pref.getInt(App.SP_COINS, 0);
    }

    public static void setCoins(int points) {
        SharedPreferences pref = getContext().getSharedPreferences(App.SP_GAME, 0);
        int temp = pref.getInt(App.SP_COINS, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(App.SP_COINS, temp + points);
        editor.apply();
    }
}
