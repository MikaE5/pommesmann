package de.androidmika.pommesmann.GameParts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.GameParts.Powerups.HealthPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.LaserPowerup;
import de.androidmika.pommesmann.GameParts.Powerups.Powerup;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.ShopHelper;
import de.androidmika.pommesmann.Vec;

public class Game {

    public interface SoundManager {
        void laserSound();
        void hitSound();
        void boxSound();
        void powerupSound();
    }

    public interface UpdateInterface {
        void updateLasers(ArrayList<Laser> lasers);
        void updateBoxes(ArrayList<Box> boxes);
        void updatePlayer(Player player);
        void updateAnimationBoxes(ArrayList<Box> animBoxes);
        void updatePowerups(ArrayList<Powerup> powerups);
    }

    private GameHelper engineHelper;

    private float REL_W_H;
    private float playerR;
    private float playerVel;
    private float boxWidth;
    private float maxVelBox;
    private float changeVelBox;
    private float powerupR;
    private float powerupWidth;

    private boolean isRunning = false;
    private boolean started = false;
    public Player player = new Player();
    private ArrayList<Laser> lasers = new ArrayList<>();
    private ArrayList<Laser> removableLasers = new ArrayList<>();
    private final int MAX_LASERS = 3;
    private int maxLasers = 3;
    private int laserDuration = 0;
    private ArrayList<Box> boxes = new ArrayList<>();
    private ArrayList<Box> animationBoxes = new ArrayList<>();
    private ArrayList<Box> removableBoxes = new ArrayList<>();
    private final int maxBoxes = 5;
    private ArrayList<Powerup> powerups = new ArrayList<>();
    private ArrayList<Powerup> removablePowerups = new ArrayList<>();
    private float healthLoss = 0.1f;
    private float hitDamage;
    private float hitBonus = 25;
    private int round = 0;
    private int points = 0;

    private SoundManager soundCallback;
    private UpdateManager updateManager = new UpdateManager();



    public Game(Context context) {
        engineHelper = new GameHelper(context);
        hitDamage = 40 + 4 * engineHelper.difficulty;

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

            player.setPos(width / 2, height / 2);
            player.setR(playerR);
            player.setMaxVel(playerVel);
            player.setHealthLoss(healthLoss);

            isRunning = true;
        }
    }

    private void setParameters() {
        playerR = 48 * REL_W_H;
        playerVel = 0.09f * playerR;
        maxVelBox = (0.2f + 0.1f * engineHelper.difficulty) * playerVel;
        changeVelBox = (0.8f + 0.1f * engineHelper.difficulty) * REL_W_H;
        boxWidth = 83 * REL_W_H;

        powerupR = 40 * REL_W_H;
        powerupWidth = 80 * REL_W_H;
    }

    private void levelManagement(float width, float height) {
        if (boxes.size() <= 0) {
            nextRound();
            newBoxes(width, height);
            if (Math.random() < engineHelper.chanceOfPowerup) {
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
            hitDamage += 2 + 2 * engineHelper.difficulty;
        }
    }

    private void newPowerup(float width, float height) {
        if (engineHelper.availablePowerups.size() > 0) {
            String type = engineHelper.getRandomPowerup();

            if (type.equals(ShopHelper.HEALTH_POWERUP)) {
                powerups.add(new HealthPowerup(width, height, powerupWidth, engineHelper.healthPowerupDuration));
            } else if (type.equals(ShopHelper.LASER_POWERUP)) {
                powerups.add(new LaserPowerup(width, height, powerupR, engineHelper.laserPowerupDuration));
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
            } while(circleInSquare(ppos.x, ppos.y, 6 * pr, bpos.x, bpos.y, blen));
            boxes.add(tempBox);
        }
    }


    public void setStarted(boolean start) {
        started = start;
    }

    public int getPoints() {
        return points;
    }



    public void update(float width, float height) {
        if (isRunning && started) {

            collisionDetection();
            updateManager.updateAnimationBoxes(animationBoxes);
            updateManager.updatePlayer(player);
            updateManager.updateLasers(lasers);
            updateManager.updateBoxes(boxes);
            updateManager.updatePowerups(powerups);
            updateLaserDuration();
            levelManagement(width, height);
        } else {
            afterSurfaceCreated(width, height);
        }
    }


    private void updateLaserDuration() {
        if (maxLasers > MAX_LASERS) {
            laserDuration--;
            if (laserDuration < 0) {
                maxLasers--;
            }
        }
    }

    public void show(Canvas canvas) {
        showText(canvas);
        showPowerups(canvas);
        showBoxes(canvas);
        showAnimationBoxes(canvas);
        showLasers(canvas);
        player.show(canvas);
    }

    private void showLasers(Canvas canvas) {
        try {
            for (Laser laser : lasers) {
                laser.show(canvas);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    private void showBoxes(Canvas canvas) {
        for (Box box : boxes) {
            box.show(canvas);
        }
    }

    private void showAnimationBoxes(Canvas canvas) {
        for (Box box : animationBoxes) {
            box.animationShow(canvas);
        }
    }

    private void showPowerups(Canvas canvas) {
        for (Powerup powerup : powerups) {
            powerup.show(canvas);
        }
    }

    private void showText(Canvas canvas) {
        float tsize = player.getR() * 0.8f;
        Paint paint = new Paint();
        paint.setColor(App.getContext().getResources().getColor(R.color.canvasTextColor));
        paint.setTextSize(tsize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Points: " + Integer.toString(points) + " Round: " + Integer.toString(round),
                0.5f * tsize, 1.1f * tsize, paint);
    }

    public void fire() {
        if (lasers.size() < maxLasers) {
            Laser newLaser = new Laser(player);

            lasers.add(0, newLaser);
            soundCallback.laserSound();
        }
    }

    private void collisionDetection() {
        for (Box box : boxes) {
            // one laser hits box
            for (Laser laser : lasers) {
                laserHitsBox(laser, box);
            }
            // player hits box
            playerHitsBox(box);
        }
        // laser hits player
        for (Laser laser : lasers) {
            laserHitsPlayer(laser);
        }
        // player hits powerup
        for (Powerup powerup : powerups) {
            playerHitsPowerup(powerup);
        }
        lasers.removeAll(removableLasers);
        boxes.removeAll(removableBoxes);
        powerups.removeAll(removablePowerups);
        removableLasers.clear();
        removableBoxes.clear();
        removablePowerups.clear();
    }

    private void laserHitsPlayer(Laser laser) {
        if (laser.getWallCount() != 0) {

            float dx = laser.getPos().x - player.getPos().x;
            float dy = laser.getPos().y - player.getPos().y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            float minDist = laser.getR() + player.getR();

            if (dist < minDist) {
                removableLasers.add(laser);
                player.changeHealth(-0.5f * hitDamage);
            }
        }
    }

    private void laserHitsBox(Laser laser, Box box) {
        if (circleInSquare(laser.getPos().x, laser.getPos().y, laser.getR(),
                box.getPos().x, box.getPos().y, box.getLen())) {
            player.changeHealth(hitBonus);
            removableLasers.add(laser);
            animationBoxes.add(box);
            removableBoxes.add(box);
            points += 1;
            soundCallback.hitSound();
        }
    }

    private void playerHitsBox(Box box) {
        if (circleInSquare(player.getPos().x, player.getPos().y, player.getR(),
                box.getPos().x, box.getPos().y, box.getLen())) {
            player.changeHealth(-1f * hitDamage);
            animationBoxes.add(box);
            removableBoxes.add(box);
            soundCallback.boxSound();
        }
    }

    private void playerHitsPowerup(Powerup powerup) {
        if (powerup instanceof HealthPowerup) {
            if (circleInSquare(player.getPos().x, player.getPos().y, player.getR(),
                    powerup.getPos().x, powerup.getPos().y, powerup.getLen())) {
                player.changeHealth(engineHelper.healthPowerupHealing);

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        } else if (powerup instanceof LaserPowerup) {
            if (circleInCircle(player.getPos().x, player.getPos().y, player.getR(),
                    powerup.getPos().x, powerup.getPos().y, powerup.getLen())) {
                maxLasers++;
                laserDuration = engineHelper.laserPowerupDuration;

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        }
    }


    private boolean circleInSquare(float cx, float cy, float cr, float sx, float sy, float slen) {
        float dx = cx - Math.max(sx, Math.min(cx, sx + slen));
        float dy = cy - Math.max(sy, Math.min(cy, sy + slen));

        return (dx * dx + dy * dy < cr * cr);
    }

    private boolean circleInCircle(float ax, float ay, float ar, float bx, float by, float br) {
        float dx = ax - bx;
        float dy = ay - by;
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < ar + br;
    }



    public boolean gameOver() {
        return (player.getHealth() < 0);
    }


}
