package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;


public class TutorialActivity extends Activity implements View.OnClickListener {

    private ImageView screenshotView;
    private TextView tutorialTextView;
    private Button nextButton;
    private int count = 0;

    private ArrayList<Bitmap> screenshots = new ArrayList<>();
    private final List<Integer> ID = Collections.unmodifiableList(
            Arrays.asList(
                    R.drawable.tutorial_1,
                    R.drawable.tutorial_2,
                    R.drawable.tutorial_3
            )
    );
    private final List<String> TUTORIALS = Collections.unmodifiableList(
            Arrays.asList(
                    "Tap screen to start!",
                    "Use Joystick to move around! \n Don't get hit by boxes!",
                    "Use Button to shoot lasers! \n Try to hit boxes with lasers!"
            )
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity);

        addScreenshots();
        screenshotView = findViewById(R.id.screenshotImageView);
        screenshotView.setImageBitmap(screenshots.get(count));
        tutorialTextView = findViewById(R.id.tutorialTextView);
        tutorialTextView.setText(TUTORIALS.get(count));
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        View v = findViewById(R.id.mainLayout);
        App.startFadeinAnim(v);
        App.startSlowFadeinAnim(tutorialTextView, 2000);
        App.startSlowFadeinAnim(nextButton, 3000);
        super.onResume();
    }

    private void addScreenshots() {
        for (Integer id : ID) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            screenshots.add(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextButton) {
            count++;
            if (count >= screenshots.size()) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                nextScreenshot(count);
            }
        }
    }

    private void nextScreenshot(int next) {
        screenshotView.setImageBitmap(screenshots.get(next));
        tutorialTextView.setText(TUTORIALS.get(next));
    }
}
