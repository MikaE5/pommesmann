package de.androidnewcomer.pommesmann;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;


public class App extends Application {
    private static Context mContext;
    private static boolean sound;
    private static boolean leftHanded;

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

    public static void startFadeoutAnim(View v) {
        Animation fadeoutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        v.startAnimation(fadeoutAnim);
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

}
