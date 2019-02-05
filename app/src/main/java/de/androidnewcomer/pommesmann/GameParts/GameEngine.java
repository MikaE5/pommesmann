package de.androidnewcomer.pommesmann.GameParts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.GameParts.Box;
import de.androidnewcomer.pommesmann.GameParts.Laser;
import de.androidnewcomer.pommesmann.GameParts.Player;
import de.androidnewcomer.pommesmann.R;
import de.androidnewcomer.pommesmann.Vec;

public class GameEngine {
    private boolean isRunning;
    private boolean started;
    public Player player;
    private ArrayList<Laser> lasers;
    private ArrayList<Laser> removableLasers;
    private final int maxLasers = 3;
    private ArrayList<Box> boxes;
    private ArrayList<Box> animationBoxes;
    private ArrayList<Box> removableBoxes;
    private final float animSpeed = 2;
    private final int maxBoxes = 5;
    private float maxVelBox;
    private ArrayList<Powerup> powerups;
    private ArrayList<Powerup> removablePowerups;
    private final float powerupBonus = 100;
    private float powerupDuration = 450;
    private float healthLoss;
    private float hitDamage;
    private float hitBonus;
    private int round;
    private int points;
    private SoundManager soundCallback;

    public interface SoundManager {
        void laserSound();
        void hitSound();
        void boxSound();
    }


    public GameEngine(Context context) {
        isRunning = false;
        started = false;
        player = new Player();
        lasers = new ArrayList<>();
        removableLasers = new ArrayList<>();
        boxes = new ArrayList<>();
        animationBoxes = new ArrayList<>();
        removableBoxes = new ArrayList<>();
        powerups = new ArrayList<>();
        removablePowerups = new ArrayList<>();
        healthLoss = 0.1f;
        hitDamage = 50;
        hitBonus = 25;
        round = 0;
        points = 0;

        if (context instanceof SoundManager) {
            soundCallback = (SoundManager) context;
        }
    }

    public void afterSurfaceCreated(float width, float height) {
        player.setPos(width / 2, height / 2);
        player.setR((Math.min(width, height) / 16));
        maxVelBox = player.getMaxVel();
    }

    public void setIsRunning(boolean running) {
        isRunning = running;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setStarted(boolean start) {
        started = start;
    }


    public int getPoints() {
        return points;
    }

    public int getMaxLasers() {
        return maxLasers;
    }

    public int getCurrentLasers() {
        return lasers.size();
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
            isRunning = true;
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
        for (Laser laser : lasers) {
            laser.show(canvas);
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
        float tsize = player.getR() * 0.5f;
        Paint paint = new Paint();
        paint.setColor(App.getContext().getResources().getColor(R.color.canvasTextColor));
        paint.setTextSize(tsize);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Points: " + Integer.toString(points) + " Round: " + Integer.toString(round),
                0.5f * tsize, 1.1f * tsize, paint);
    }

    public void fire() {
        if (lasers.size() < maxLasers) {
            // normalize vel
            Vec tempVel = player.getVel();
            float velLength = (float) Math.sqrt(tempVel.x * tempVel.x + tempVel.y * tempVel.y);
            tempVel.mult(1 / velLength);
            tempVel.mult(1.75f * player.getMaxVel());

            Vec tempPos = player.getPos();
            Laser newLaser = new Laser(tempPos, tempVel, player.getR() * 0.25f);

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

        if (circleInSquare(ppos.x, ppos.y, pr, pupos.x, pupos.y, pulen)) {
            player.changeHealth(powerupBonus);
            removablePowerups.add(powerup);
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

    private void levelManagement(float width, float height) {
        if (boxes.size() <= 0) {
            nextRound();
            newBoxes(width, height);
            if (Math.random() < 0.5) {
                powerups.add(new Powerup(width, height, width / 12, (int) powerupDuration));
            }
        }
    }

    private void nextRound() {
        round++;
        if (round > 1) {
            maxVelBox += 0.5;
            healthLoss += 0.01f;
            hitDamage += 2;
            powerupDuration *= 0.9f ;
        }
        player.setHealthLoss(healthLoss);
    }

    private void newBoxes(float width, float height) {
        Box tempBox;
        Vec ppos = player.getPos();
        float pr = player.getR();
        Vec bpos;
        float blen;
        int temp = 3;
        if (round == 1) temp = 5;
        for (int i = 0; i < maxBoxes; i++) {
            do {
                tempBox = new Box(width, height, width / 10, maxVelBox);
                bpos = tempBox.getPos();
                blen = tempBox.getLen();
            } while(circleInSquare(ppos.x, ppos.y, temp * pr, bpos.x, bpos.y, blen));

            boxes.add(tempBox);
        }
    }

    public boolean gameOver() {
        return (player.getHealth() < 0);
    }


}
