package de.androidmika.pommesmann.Firebase;


import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;

import static de.androidmika.pommesmann.App.getContext;

public class FireManager {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private int level;
    private Context context;


    public interface UIInterface{
        void hideButton();
        void setHint(String name);
        void chooseDifferentName(boolean firstSignIn);
        void fillHighscoreDialog(ArrayList<String> scores, ArrayList<String> names);
    }
    private UIInterface uiInterface;

    public FireManager(Context context) {
        // get SecretOfPommesmannLevel from Shopdatabase
        ShopDatabaseHelper dbHelper = ShopDatabaseHelper.getInstance(getContext());
        level = dbHelper.getSecretOfPommesmannLevel();

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
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            validateName(name, true);
                        } else {
                            FireUserInterface.failure(context);
                        }
                    }
                });
    }

    public void validateName(final String name, final boolean firstSignIn) {

        if (auth.getUid() != null) {

            // if name is the dummyName, dont check if the name already exists
            if (name.equals(FireInput.dummyName)) {
                if (firstSignIn) {
                    setFirstData(name);
                    uiInterface.hideButton(); // hide the submit button
                } else {
                    setName(name);
                }
            } else {

                // check if the name already exists
                db.collection(FireContract.userCollection)
                        .whereEqualTo(FireContract.name, name)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null && task.getResult().size() <= 0) {
                                        // the name does not exist
                                        if (firstSignIn) {
                                            setFirstData(name);
                                            uiInterface.hideButton(); // hide the submit button
                                        } else {
                                            setName(name);
                                        }
                                    } else {
                                        // name does already exist, choose new name
                                        uiInterface.chooseDifferentName(firstSignIn);
                                    }
                                }
                            }
                        });
            }
        } else {
            FireUserInterface.failure(context);
        }
    }

    private void setFirstData(String name) {
        if (auth.getUid() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put(FireContract.userID, auth.getUid());
            data.put(FireContract.name, name);
            data.put(FireContract.score, App.getHighscore());
            data.put(FireContract.level, level);

            db.collection(FireContract.userCollection).document(auth.getUid())
                    .set(data);
        }
    }

    public void dummyLogin() {
        // if user did not want to choose a name at first sign in, sign in with dummy name
        setFirstData(FireInput.dummyName);
        uiInterface.hideButton(); // hide the submit button
    }

    public void updateName(String name) {
        // check if the new name is the old name
        differentName(name);
    }

    private void differentName(final String name) {
        if (auth.getUid() != null) {

            db.collection(FireContract.userCollection).document(auth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();

                                if (snapshot != null && snapshot.exists()) {
                                    Object oldName = snapshot.get(FireContract.name);

                                    if (oldName != null && !oldName.toString().equals(name)) {

                                        // check if the name does not exist yet
                                        validateName(name, false);

                                    } else {
                                        FireUserInterface.failure(context);
                                    }
                                }
                            }
                        }
                    });

        }
    }

    private void setName(String name) {
        if (auth.getUid() != null) {

            db.collection(FireContract.userCollection).document(auth.getUid())
                    .update(FireContract.name, name)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            FireUserInterface.failure(context);
                        }
                    });
        }
    }


    public void updateScore(int points) {
        if (auth.getUid() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put(FireContract.score, points);
            data.put(FireContract.level, level);

            db.collection(FireContract.userCollection).document(auth.getUid())
                    .update(data);
        }
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



    public void getTopTen() {
        db.collection(FireContract.userCollection)
                .orderBy(FireContract.score, Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
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

                            uiInterface.fillHighscoreDialog(scores, names);
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
                            FireUserInterface.deleteSuccess(context);
                            auth.signOut();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            FireUserInterface.failure(context);
                        }
                    });
        } else {
            FireUserInterface.failure(context);
        }
    }

}
