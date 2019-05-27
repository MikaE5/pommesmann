package de.androidmika.pommesmann.GameParts.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;


import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.GameParts.Box;
import de.androidmika.pommesmann.GameParts.User.User;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.Vec;

public class Game {

    public interface SoundManager {
        void laserSound();
    }


    private boolean isRunning = false;
    private boolean started = false;
    private int round = 0;

    private float REL_W_H;
    private float boxWidth;
    private float maxVelBox;
    private float changeVelBox;

    private final int maxBoxes = 5;
    private ArrayList<Box> boxes = new ArrayList<>();
    private ArrayList<Box> animationBoxes = new ArrayList<>();

    public User user = new User();

    private SoundManager soundCallback;
    private CollisionManager collisionManager;



    public Game(Context context) {
        collisionManager = new CollisionManager(context);

        if (context instanceof SoundManager) {
            soundCallback = (SoundManager) context;
        }
    }

    private void afterSurfaceCreated(float width, float height) {
        if (!isRunning) {
            // get the screen size in updateManager

            REL_W_H = (float)Math.sqrt(width * height) / 1000;
            // round to two decimal places
            REL_W_H *= 100;
            REL_W_H = Math.round(REL_W_H);
            REL_W_H /= 100;

            setBoxParameters();
            user.setParams(REL_W_H, width, height);

            isRunning = true;
        }
    }

    private void setBoxParameters() {
        // playerVel = 0.09f * 48 * REL_W_H
        maxVelBox = (0.2f + 0.1f * user.getDifficulty()) * 0.09f * 48 * REL_W_H;
        changeVelBox = (0.8f + 0.1f * user.getDifficulty()) * REL_W_H;
        boxWidth = 83 * REL_W_H;
    }

    private void levelManagement(float width, float height) {
        if (boxes.size() <= 0) {
            nextRound();
            newBoxes(width, height);
            user.newPowerup(width, height);
        }
    }

    private void nextRound() {
        round++;
        // nextRound is called when the game starts, but I don't want to increase the values
        // when starting
        if (round > 1) {
            float factor = 1 - 0.05f * round;
            if (factor < 0.5) factor = 0.5f;
            maxVelBox += factor * changeVelBox;
            user.updateHitDamage();
        }
    }


    private void newBoxes(float width, float height) {
        Box tempBox;

        for (int i = 0; i < maxBoxes; i++) {
            do {
                tempBox = new Box(width, height, boxWidth, maxVelBox);

            } while(collisionManager.isBoxNearby(user.player, tempBox, 6));
            boxes.add(tempBox);
        }
    }


    public void setStarted(boolean start) {
        started = start;
    }

    public int getPoints() {
        return user.getPoints();
    }



    public void update(float width, float height) {
        if (isRunning && started) {

            collisionDetection();
            UpdateManager.updateBoxes(animationBoxes, width, height);
            UpdateManager.updateBoxes(boxes, width, height);
            UpdateManager.updatePlayer(user.player, width, height);
            UpdateManager.updateLasers(user.lasers, width, height);
            UpdateManager.updatePowerups(user.powerups);
            user.updateAttributes();
            levelManagement(width, height);
        } else {
            afterSurfaceCreated(width, height);
        }

    }



    public void show(Canvas canvas) {
        ShowManager.showPowerups(user.powerups, canvas);
        showText(canvas);
        ShowManager.showHealthbar(user.player.getHealth(), canvas);
        ShowManager.showBoxes(boxes, canvas);
        ShowManager.showBoxes(animationBoxes, canvas);
        ShowManager.showLasers(user.lasers, canvas);
        ShowManager.showPlayer(user.player, canvas);
    }


    private void showText(Canvas canvas) {
        float tsize = 40 * REL_W_H;
        Paint paint = new Paint();
        paint.setColor(App.getContext().getResources().getColor(R.color.canvasTextColor));
        paint.setTextSize(tsize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Points: " + user.getPoints() +
                        " Round: " + round,
                0.5f * tsize, 1.1f * tsize, paint);
    }

    public void fire() {
        if (user.fire())
            soundCallback.laserSound();
    }

    private void collisionDetection() {
        // does all the collisionDetection and adds all boxes, that are now set to animate
        animationBoxes.addAll(
                collisionManager.collisionDetection(boxes, user)
        );
    }


    public boolean gameOver() {
        return (user.getHealth() < 0);
    }
}
