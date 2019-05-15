package de.androidmika.pommesmann.Firebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.androidmika.pommesmann.R;

public class FireUserInterface {

    public interface FireConnection {
        void login(String name);
        void differentName(String name);
    }
    private FireConnection connection;
    private Context context;

    public FireUserInterface(Context activityContext) {
        this.context = activityContext;

        if (activityContext instanceof FireConnection) {
            connection = (FireConnection) activityContext;
        }

    }



    public void submitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.choosename_dialog, null);
        builder.setView(dialogView);

        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.chooseNameDialogTitle);


        final EditText editName = dialogView.findViewById(R.id.editText);

        dialogView.findViewById(R.id.confirmButton)
            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                connection.login(name);
            }
        });

        dialogView.findViewById(R.id.laterButton)
            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void differentNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.choosename_dialog, null);
        builder.setView(dialogView);
        TextView dialogText = dialogView.findViewById(R.id.dialogText);
        dialogText.setText("This name already exists! Please choose a different name");


        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.chooseNameDialogTitle);


        final EditText editName = dialogView.findViewById(R.id.editText);
        final Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                name = FireInput.analyzeString(name);
                connection.differentName(name);
            }
        });

        Button laterButton = dialogView.findViewById(R.id.laterButton);
        laterButton.setVisibility(View.GONE);

        dialog.show();
    }



    static void failure(Context context) {
        // make toast
    }

    static void deleteSuccess(Context context) {
        //
    }

}
