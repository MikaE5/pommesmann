package de.androidmika.pommesmann.GameParts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.GameParts.Powerups.HealthPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.LaserPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;
import de.androidmika.pommesmann.GameParts.User.User;
import de.androidmika.pommesmann.GameParts.User.UserHelper;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;
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
    private UpdateManager updateManager = new UpdateManager();
    private ShowManager showManager = new ShowManager();
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
            if (!updateManager.isSizeAssigned()) {
                updateManager.assignSize(width, height);
            }

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
            user.updateAttributes();
        }
    }


    private void newBoxes(float width, float height) {
        Box tempBox;
        Vec ppos = user.getPlayerPos();
        float pr = user.getPlayerR();
        Vec bpos;
        float blen;

        for (int i = 0; i < maxBoxes; i++) {
            do {
                tempBox = new Box(width, height, boxWidth, maxVelBox);
                bpos = tempBox.getPos();
                blen = tempBox.getLen();
            } while(collisionManager.circleInSquare(ppos.x, ppos.y, 6 * pr, bpos.x, bpos.y, blen));
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
            updateManager.updateBoxes(animationBoxes);
            updateManager.updateBoxes(boxes);
            user.update(width, height);
            levelManagement(width, height);
        } else {
            afterSurfaceCreated(width, height);
        }
    }



    public void show(Canvas canvas) {
        showText(canvas);
        user.showPowerups(canvas);
        showManager.showBoxes(boxes, canvas);
        showManager.showBoxes(animationBoxes, canvas);
        user.showLasers(canvas);
        user.showPlayer(canvas);
    }


    private void showText(Canvas canvas) {
        float tsize = 40 * REL_W_H;
        Paint paint = new Paint();
        paint.setColor(App.getContext().getResources().getColor(R.color.canvasTextColor));
        paint.setTextSize(tsize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Points: " + Integer.toString(user.getPoints()) +
                        " Round: " + Integer.toString(round),
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
