package de.androidmika.pommesmann.GameParts;

import static de.androidmika.pommesmann.App.getContext;

class PlayerAttributes {

    final int MAX_LASERS = 3;
    int maxLasers = 3;
    int laserDuration = 0;

    float healthLoss = 0.1f;
    float hitBonus = 25;
    float hitDamage;

    int points = 0;

    private GameHelper gameHelper = new GameHelper(getContext());

    PlayerAttributes() {
        hitDamage = 40 + 4 * gameHelper.difficulty;
    }
}
