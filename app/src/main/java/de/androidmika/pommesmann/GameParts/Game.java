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


    private UserHelper userHelper;

    private float REL_W_H;
    private float boxWidth;
    private float maxVelBox;
    private float changeVelBox;
    private float powerupR;
    private float powerupWidth;

    private boolean isRunning = false;
    private boolean started = false;

    private User user = new User();


    public Player player = new Player();
    private ArrayList<Laser> lasers = new ArrayList<>();

    private ArrayList<Box> boxes = new ArrayList<>();
    private ArrayList<Box> animationBoxes = new ArrayList<>();

    private final int maxBoxes = 5;
    private ArrayList<Powerup> powerups = new ArrayList<>();

    private int round = 0;


    private SoundManager soundCallback;
    private UpdateManager updateManager = new UpdateManager();
    private ShowManager showManager = new ShowManager();
    private CollisionManager collisionManager;



    public Game(Context context) {
        userHelper = new UserHelper(context);
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
            setParameters();

            user.setPlayerParams(REL_W_H, width, height);

            isRunning = true;
        }
    }

    private void setParameters() {
        // playerVel = 0.09f * 48 * REL_W_H
        maxVelBox = (0.2f + 0.1f * userHelper.difficulty) * 0.09f * 48 * REL_W_H;
        changeVelBox = (0.8f + 0.1f * userHelper.difficulty) * REL_W_H;
        boxWidth = 83 * REL_W_H;

        powerupR = 40 * REL_W_H;
        powerupWidth = 80 * REL_W_H;
    }

    private void levelManagement(float width, float height) {
        if (boxes.size() <= 0) {
            nextRound();
            newBoxes(width, height);
            if (Math.random() < userHelper.chanceOfPowerup) {
                newPowerup(width, height);
            }
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

    private void newPowerup(float width, float height) {
        if (userHelper.availablePowerups.size() > 0) {
            String type = userHelper.getRandomPowerup();

            if (type.equals(ShopHelper.HEALTH_POWERUP)) {
                powerups.add(new HealthPowerup(width, height, powerupWidth, userHelper.healthPowerupDuration));
            } else if (type.equals(ShopHelper.LASER_POWERUP)) {
                powerups.add(new LaserPowerup(width, height, powerupR, userHelper.laserPowerupDuration));
            }
        }
    }

    private void newBoxes(float width, float height) {
        Box tempBox;
        Vec ppos = player.getPos();
        float pr = player.getR();
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
        return player.getPoints();
    }



    public void update(float width, float height) {
        if (isRunning && started) {

            collisionDetection();
            updateManager.updateBoxes(animationBoxes);
            updateManager.updatePlayer(player);
            updateManager.updateLasers(lasers);
            updateManager.updateBoxes(boxes);
            updateManager.updatePowerups(powerups);
            levelManagement(width, height);
        } else {
            afterSurfaceCreated(width, height);
        }
    }



    public void show(Canvas canvas) {
        showText(canvas);
        showManager.showPowerups(powerups, canvas);
        showManager.showBoxes(boxes, canvas);
        showManager.showBoxes(animationBoxes, canvas);
        showManager.showLasers(lasers, canvas);
        showManager.showPlayer(player, canvas);
    }


    private void showText(Canvas canvas) {
        float tsize = player.getR() * 0.8f;
        Paint paint = new Paint();
        paint.setColor(App.getContext().getResources().getColor(R.color.canvasTextColor));
        paint.setTextSize(tsize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Points: " + Integer.toString(player.getPoints()) +
                        " Round: " + Integer.toString(round),
                0.5f * tsize, 1.1f * tsize, paint);
    }

    public void fire() {
        if (lasers.size() < player.getMaxLaser()) {
            Laser newLaser = new Laser(player);

            lasers.add(0, newLaser);
            soundCallback.laserSound();
        }
    }

    private void collisionDetection() {

        // does all the collisionDetection and adds all boxes, that are now set to animate
        animationBoxes.addAll(
                collisionManager.collisionDetection(player, boxes, lasers, powerups)
        );
    }


    public boolean gameOver() {
        return (player.getHealth() < 0);
    }


}
