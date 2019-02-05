package de.androidnewcomer.pommesmann.GameScreen;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.androidnewcomer.pommesmann.R;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private GameOperations gameCallback;


    public interface GameOperations {
        void gameUpdate();
        void gameDraw(Canvas canvas);
        void endOfGame();
        boolean gameOver();
    }

    public GameView(Context context) {
        super(context);
        if (context instanceof GameOperations) {
            gameCallback = (GameOperations) context;
        }
        additionalConstructor();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof GameOperations) {
            gameCallback = (GameOperations) context;
        }
        additionalConstructor();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (context instanceof GameOperations) {
            gameCallback = (GameOperations) context;
        }
        additionalConstructor();
    }

    private void additionalConstructor() {
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);

        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // runs repeatedly through GameThread
    public void update() {
        gameCallback.gameUpdate();

        if (gameCallback.gameOver()) {
            thread.setRunning(false);
            gameCallback.endOfGame();
        }
    }
    // runs repeatedly through GameThread
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(getResources().getColor(R.color.canvasBackground));
            gameCallback.gameDraw(canvas);
        }
    }


}
