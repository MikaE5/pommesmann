package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.androidmika.pommesmann.R;

public class AboutActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);


        TextView aboutTextView = findViewById(R.id.aboutTextView);
        InputStream aboutInput = getResources().openRawResource(R.raw.about);
        aboutTextView.setText(streamToString(aboutInput));
        TextView creditsTextView = findViewById(R.id.creditsTextView);
        InputStream creditsInput = getResources().openRawResource(R.raw.credits);
        creditsTextView.setText(streamToString(creditsInput));
    }

    private String streamToString(InputStream input) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder builder = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
