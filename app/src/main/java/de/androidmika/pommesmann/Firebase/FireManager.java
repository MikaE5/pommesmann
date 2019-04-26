package de.androidmika.pommesmann.Firebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;

import static de.androidmika.pommesmann.App.getContext;

public class FireManager {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private int level;

    public interface DataInterface {
        void receivedHighscore(double score);
        void updateHighscoreList(ArrayList<String> names, ArrayList<Long> scores);
    }
    private DataInterface dataInterface;

    public interface UIInterface{
        void hideButton();
    }
    private UIInterface uiInterface;

    public FireManager(Context context) {
        // get SecretOfPommesmannLevel from Shopdatabase
        ShopDatabaseHelper dbHelper = ShopDatabaseHelper.getInstance(getContext());
        level = dbHelper.getSecretOfPommesmannLevel();

        if (context instanceof DataInterface) {
            dataInterface = (DataInterface) context;
        }
        if (context instanceof UIInterface) {
            uiInterface = (UIInterface) context;
        }
    }

    public boolean userExists() {
        return auth.getCurrentUser() != null;
    }

    private void signInAnonymously(final Context context, final String name) {
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Sign in successful", Toast.LENGTH_LONG)
                            .show();
                    setFirstData(name);
                } else {
                    Toast.makeText(context, "Sign in failed", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    // returns true if a name was chosen and a user created
    // returns false if the dialog was just dismissed
    public void showChooseNameDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.choosename_dialog, null);
        builder.setView(dialogView);

        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.chooseNameDialogTitle);


        final EditText editName = dialogView.findViewById(R.id.editText);
        final Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                signInAnonymously(context, name);

                dialog.dismiss();
                uiInterface.hideButton();
            }
        });

        Button laterButton = dialogView.findViewById(R.id.laterButton);
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setFirstData(String name) {
        Map<String, Object> data = new HashMap<>();
        if (auth.getUid() != null) {
            data.put(FireContract.userID, auth.getUid());
            data.put(FireContract.name, name);
            data.put(FireContract.score, App.getHighscore());
            data.put(FireContract.level, level);

            db.collection(FireContract.userCollection).document(auth.getUid())
                    .set(data);
        }
    }

    public void updateData(int points) {
        // update Data
        Map<String, Object> data = new HashMap<>();
        data.put(FireContract.score, points);
        data.put(FireContract.level, level);

        if (auth.getUid() != null)
            db.collection(FireContract.userCollection).document(auth.getUid())
                    .update(data);

    }


    public void isHighscoreUpdated() {
        if (auth.getUid() != null) {
            db.collection(FireContract.userCollection).document(auth.getUid()).
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        final DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            dataInterface.receivedHighscore(document.getDouble(FireContract.score));
                        }
                    }
                }
            });
        }
    }

    public void getHighscores() {
        db.collection(FireContract.userCollection)
                .whereGreaterThan(FireContract.score, 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // get name and score
                            ArrayList<String> names = new ArrayList<>();
                            ArrayList<Long> scores = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                names.add((String) document.get(FireContract.name));
                                scores.add((Long) document.get(FireContract.score));
                            }
                            dataInterface.updateHighscoreList(names, scores);
                        }
                    }
                });
    }
}
