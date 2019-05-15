package de.androidmika.pommesmann.Firebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    private Context context;


    public interface DataInterface {
        void deleteSuccess();
        void deleteFailure();
        void updateSuccess();
        void updateFailure();
    }
    private DataInterface dataInterface;

    public interface UIInterface{
        void hideButton();
        void setHint(String name);
        void chooseDifferentName();
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
        this.context = context;
    }

    public boolean userExists() {
        return auth.getCurrentUser() != null;
    }

    public void signIn(String name) {
        name = FireInput.analyzeString(name); // input validation
        signInAnonymously(name);
    }

    private void signInAnonymously(final String name) {
        auth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            validateName(name);
                        } else {
                            FireUserInterface.failure(context);
                        }
                    }
                });
    }

    public void validateName(final String name) {
        // check if the name already exists
        if (auth.getUid() != null) {
            db.collection(FireContract.userCollection)
                    .whereEqualTo(FireContract.name, name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && task.getResult().size() <= 0) {
                                    // the name does not exist
                                    setFirstData(name);
                                } else {
                                    // name does already exist, choose new name
                                    uiInterface.chooseDifferentName();
                                }
                            }
                        }
                    });
        } else {
            FireUserInterface.failure(context);
        }
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



    public void updateName(final Context context, final String name) {
        if (auth.getUid() != null) {
            // First check if the new name is the old name
            db.collection(FireContract.userCollection)
                    .document(auth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();

                                if (snapshot != null && snapshot.exists()) {
                                    Object oldName = snapshot.get(FireContract.name);

                                    // update name only if it is a different name
                                    if (oldName != null && !oldName.toString().equals(name)) {
                                        //analyzeName(context, name, false);
                                    } else {
                                        dataInterface.updateFailure();
                                    }
                                }
                            }
                        }
                    });
        }

    }



    public void updateScore(int points) {
        // update Data
        Map<String, Object> data = new HashMap<>();
        data.put(FireContract.score, points);
        data.put(FireContract.level, level);

        if (auth.getUid() != null)
            db.collection(FireContract.userCollection).document(auth.getUid())
                    .update(data);

    }


    // listen for real time updates for username
    public void getUserName() {
        if (auth.getUid() != null) {
            db.collection(FireContract.userCollection)
                    .document(auth.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) return;

                            if (snapshot != null && snapshot.exists()) {

                                Object name = snapshot.get(FireContract.name);
                                if (name != null) {
                                    uiInterface.setHint(name.toString());
                                }

                            }

                        }
                    });
        }
    }




    public void showHighscoreDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.highscore_dialog, null);
        builder.setView(dialogView);

        final Dialog dialog = builder.create();
        dialog.setTitle(R.string.highscoreDialogTitle);


        TextView rankingTextView = dialogView.findViewById(R.id.rankingTextView);
        TextView scoresTextView = dialogView.findViewById(R.id.scoresTextView);
        TextView namesTextView = dialogView.findViewById(R.id.namesTextView);
        getTopTen(rankingTextView, scoresTextView, namesTextView);

        dialog.show();
    }



    private void fillHighscoreDialog(ArrayList<String> scores, ArrayList<String> names,
                                     TextView rankingTextView,
                                     TextView scoresTextView,
                                     TextView namesTextView
                                     ) {
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




    private void getTopTen(final TextView rankingTextView,
                           final TextView scoresTextView,
                           final TextView namesTextView) {
        db.collection(FireContract.userCollection)
                .orderBy(FireContract.score, Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            ArrayList<String> scores = new ArrayList<>();
                            ArrayList<String> names = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Object score = document.get(FireContract.score);
                                Object name = document.get(FireContract.name);
                                if (score != null && name != null) {
                                    scores.add(score.toString());
                                    names.add(name.toString());
                                }
                            }

                            fillHighscoreDialog(scores, names,
                                    rankingTextView,
                                    scoresTextView,
                                    namesTextView);
                        }
                    }
                });
    }


    public void deleteUserData() {
        if (auth.getUid() != null) {
            db.collection(FireContract.userCollection).document(auth.getUid())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataInterface.deleteSuccess();
                            auth.signOut();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dataInterface.deleteFailure();
                        }
                    });
        } else {
            dataInterface.deleteFailure();
        }
    }

}
