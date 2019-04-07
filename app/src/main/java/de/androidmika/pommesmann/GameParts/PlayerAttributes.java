package de.androidmika.pommesmann.GameParts;

import static de.androidmika.pommesmann.App.getContext;

class PlayerAttributes {

    final int MAX_LASERS = 3;
    int maxLasers = 3;
    int laserDuration = 0;

    float healthLoss = 0.1f;
    float hitBonus = 25;
    float hitDamage;
    int difficulty;

    int points = 0;


    PlayerAttributes() {
        GameHelper gameHelper = new GameHelper(getContext());
        difficulty = gameHelper.difficulty;
        hitDamage = 40 + 4 * difficulty;
    }

    void update() {
        hitDamage += 2 + 2 * difficulty;
    }
}
