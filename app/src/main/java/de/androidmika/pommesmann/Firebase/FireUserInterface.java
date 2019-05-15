package de.androidmika.pommesmann.Firebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;

public class FireUserInterface {

    public interface FireConnection {
        void login(String name);
        void dummyLogin();
        void differentName(String name);
    }
    private FireConnection connection;
    private Context context;

    private View highscoreDialogView;

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
                dialog.dismiss();
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

    public void differentNameDialog(final boolean firstSignIn) {
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
        dialogView.findViewById(R.id.confirmButton)
            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                name = FireInput.analyzeString(name);
                connection.differentName(name);
                dialog.dismiss();
            }
            });

        dialogView.findViewById(R.id.laterButton)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (firstSignIn) {
                        // no name chosen at first sign in -> sign in with dummy name
                        connection.dummyLogin();
                        dialog.dismiss();
                    } else {
                        // if user already signed in, he wanted to change his name
                        // then dialog can just be closed
                        dialog.dismiss();
                    }
                }
            });

        dialog.show();
    }



    public void highscoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        highscoreDialogView = inflater.inflate(R.layout.highscore_dialog, null);
        builder.setView(highscoreDialogView);

        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.highscoreDialogTitle);

        dialog.show();
    }

    public void fillHighscoreDialog(ArrayList<String> scores, ArrayList<String> names) {
        TextView rankingTextView = highscoreDialogView.findViewById(R.id.rankingTextView);
        TextView scoresTextView = highscoreDialogView.findViewById(R.id.scoresTextView);
        TextView namesTextView = highscoreDialogView.findViewById(R.id.namesTextView);


        int max = Math.max(names.size(), scores.size());
        String ranking = "";
        String score ="";
        String name = "";
        for (int i = 0; i < max; i++) {
            ranking = ranking.concat(i+1 + ".\n");
            score = score.concat(scores.get(i) + "\n");
            name = name.concat(names.get(i) + "\n");
        }
        rankingTextView.setText(ranking);
        scoresTextView.setText(score);
        namesTextView.setText(name);
    }

    static void failure(Context context) {
        String message = "Sorry, an error ocurred!";
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    static void deleteSuccess(Context context) {
        String message = "Data was successfully deleted!";
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
