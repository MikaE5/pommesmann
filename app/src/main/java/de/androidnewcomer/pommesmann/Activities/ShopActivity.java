package de.androidnewcomer.pommesmann.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import de.androidnewcomer.pommesmann.App;
import de.androidnewcomer.pommesmann.R;

public class ShopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        App.startFadeinAnim(mainLayout);
    }
}
