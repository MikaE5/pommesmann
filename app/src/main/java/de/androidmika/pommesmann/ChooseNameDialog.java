package de.androidmika.pommesmann;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class ChooseNameDialog extends Dialog implements View.OnClickListener {

    private Context context;

    public ChooseNameDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        
    }
}
