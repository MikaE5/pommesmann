# POMMESMANN

This is my first Android Application.
It is a simple 2D-Game with an in-game-shop to buy powerups and other upgrades.
It is published in the Google Play Store: https://play.google.com/store/apps/details?id=de.androidmika.pommesmann

## Activities
The Game has basically three Activities, Main-, Game- and GameoverActivity.
The other Activites can be accessed via the MainActivity.

1. MainActivity
    + AboutActivity
    + TutorialActivity
    + ShopActivity
2. GameActivity
3. GameoverActivity


## Game-Loop
The Game-Loop is realised through a custom SurfaceView.
The drawing is done by accessing the canvas of the view.
The SurfaceView also has a custom Thread, where the actual Game-Loop (updating, drawing) is managed.

## Shop
The Shop is managed by a ShopDatabase, that is realized by using the SQLiteOpenHelper-Class.
It stores all information, such as level or price, about items, that can be bought in the shop.
