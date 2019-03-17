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

public class GameEngine {

    private GameEngineHelper engineHelper;

    private float REL_W_H;

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
    private final float animSpeed = 2;
    private final int maxBoxes = 5;
    private float maxVelBox;
    private ArrayList<Powerup> powerups = new ArrayList<>();
    private ArrayList<Powerup> removablePowerups = new ArrayList<>();
    private float healthLoss = 0.1f;
    private float hitDamage;
    private float hitBonus = 25;
    private int round = 0;
    private int points = 0;
    private SoundManager soundCallback;



    public interface SoundManager {
        void laserSound();
        void hitSound();
        void boxSound();
        void powerupSound();
    }


    public GameEngine(Context context) {
        engineHelper = new GameEngineHelper(context);
        hitDamage = 50 + 5 * engineHelper.difficulty;

        if (context instanceof SoundManager) {
            soundCallback = (SoundManager) context;
        }
    }

    private void afterSurfaceCreated(float width, float height) {
        if (!isRunning) {
            REL_W_H = (float)Math.sqrt(width * height) / 1000;
            // round to two decimal places
            REL_W_H *= 100;
            REL_W_H = Math.round(REL_W_H);
            REL_W_H /= 100;

            player.setPos(width / 2, height / 2);
            player.setR(55 * REL_W_H);
            player.setHealthLoss(healthLoss);

            // addition of difficulty is not relative
            maxVelBox = player.getMaxVel();
            isRunning = true;
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
            updateAnimationBoxes();
            player.update(width, height);
            updateLasers(width, height);
            updateBoxes(width, height);
            updatePowerups();
            levelManagement(width, height);
        } else {
            afterSurfaceCreated(width, height);
        }
    }

    private void updateLasers(float width, float height) {
        for (Laser laser : lasers) {
            laser.update(width, height);
            if (laser.removeLaser()) {
                removableLasers.add(laser);
            }
        }
        lasers.removeAll(removableLasers);
        removableLasers.clear();
    }

    private void updateBoxes(float width, float height) {
        for (Box box : boxes) {
            box.update(width, height);
        }
    }

    private void updateAnimationBoxes() {
        for (Box box : animationBoxes) {
            box.removeAnimation(animSpeed);
            if (box.animationFinished()) {
                removableBoxes.add(box);
            }
        }
        animationBoxes.removeAll(removableBoxes);
        removableBoxes.clear();
    }

    private void updatePowerups() {
        for (Powerup powerup : powerups) {
            powerup.update();
            if (powerup.isRemovable()) {
                removablePowerups.add(powerup);
            }
        }
        powerups.removeAll(removablePowerups);
        removablePowerups.clear();

        updateLaserDuration();
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

    private void laserHitsPlayer(Laser laser) {
        if (laser.getWallCount() != 0) {
            Vec lpos = laser.getPos();
            Vec ppos = player.getPos();

            float dx = lpos.x - ppos.x;
            float dy = lpos.y - ppos.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            float minDist = laser.getR() + player.getR();

            if (dist < minDist) {
                removableLasers.add(laser);
                player.changeHealth(-0.5f * hitDamage);
            }
        }
    }

    private void laserHitsBox(Laser laser, Box box) {
        Vec lpos = laser.getPos();
        Vec bpos = box.getPos();
        float lr = laser.getR();
        float blen = box.getLen();

        if (circleInSquare(lpos.x, lpos.y, lr, bpos.x, bpos.y, blen)) {
            player.changeHealth(hitBonus);
            removableLasers.add(laser);
            animationBoxes.add(box);
            removableBoxes.add(box);
            points += 1;
            soundCallback.hitSound();
        }
    }

    private void playerHitsBox(Box box) {
        Vec ppos = player.getPos();
        Vec bpos = box.getPos();
        float pr = player.getR();
        float blen = box.getLen();

        if (circleInSquare(ppos.x, ppos.y, pr, bpos.x, bpos.y, blen)) {
            player.changeHealth(-1f * hitDamage);
            animationBoxes.add(box);
            removableBoxes.add(box);
            soundCallback.boxSound();
        }
    }

    private void playerHitsPowerup(Powerup powerup) {
        Vec ppos = player.getPos();
        float pr = player.getR();
        Vec pupos = powerup.getPos();
        float pulen = powerup.getLen();

        if (powerup instanceof HealthPowerup) {
            if (circleInSquare(ppos.x, ppos.y, pr, pupos.x, pupos.y, pulen)) {
                player.changeHealth(engineHelper.healthPowerupHealing);

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        } else if (powerup instanceof LaserPowerup) {
            if (circleInCircle(ppos.x, ppos.y, pr, pupos.x, pupos.y, pulen)) {
                maxLasers++;
                laserDuration = engineHelper.laserPowerupDuration;

                removablePowerups.add(powerup);
                soundCallback.powerupSound();
            }
        }
    }

    private void collisionDetection() {
        for (Laser laser : lasers) {
            laserHitsPlayer(laser);
            for (Box box : boxes) {
                laserHitsBox(laser, box);
            }
        }
        for (Box box : boxes) {
            playerHitsBox(box);
        }
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

    private void levelManagement(float width, float height) {
        if (boxes.size() <= 0) {
            nextRound();
            newBoxes(width, height);
            if (Math.random() < engineHelper.chanceOfPowerup) {
                newPowerup(width, height);
            }
        }
    }

    private void newPowerup(float width, float height) {
        if (engineHelper.availablePowerups.size() > 0) {
            String type = engineHelper.getRandomPowerup();

            if (type.equals(ShopHelper.HEALTH_POWERUP)) {
                powerups.add(new HealthPowerup(width, height, 75 * REL_W_H, engineHelper.healthPowerupDuration));
            } else if (type.equals(ShopHelper.LASER_POWERUP)) {
                powerups.add(new LaserPowerup(width, height, 38 * REL_W_H, engineHelper.laserPowerupDuration));
            }
        }
    }

    private void nextRound() {
        round++;
        // nextRound is called when the game starts, but I don't want to increase the values
        // when starting
        if (round > 1) {
            maxVelBox += (0.5 + 0.1 * engineHelper.difficulty) * REL_W_H;
            hitDamage += 2 + 2 * engineHelper.difficulty;
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
                tempBox = new Box(width, height, 88 * REL_W_H, maxVelBox);
                bpos = tempBox.getPos();
                blen = tempBox.getLen();
            } while(circleInSquare(ppos.x, ppos.y, 6 * pr, bpos.x, bpos.y, blen));
            boxes.add(tempBox);
        }
    }

    public boolean gameOver() {
        return (player.getHealth() < 0);
    }


}
