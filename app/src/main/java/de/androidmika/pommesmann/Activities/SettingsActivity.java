package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.util.ArrayList;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.Firebase.FireInput;
import de.androidmika.pommesmann.Firebase.FireManager;
import de.androidmika.pommesmann.Firebase.FireUserInterface;
import de.androidmika.pommesmann.R;
import de.androidmika.pommesmann.ShopDatabase.ShopDatabaseHelper;

public class SettingsActivity extends Activity implements View.OnClickListener,
        FireManager.UIInterface, FireUserInterface.FireConnection {

    private FireManager manager;
    private FireUserInterface fireUserInterface;

    private CheckBox leftHandedCheckBox;
    private EditText nameEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new FireManager(this);
        fireUserInterface = new FireUserInterface(this);

        setContentView(R.layout.settings_activity);

        nameEditText = findViewById(R.id.nameEditText);
        manager.getUserName();

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        if (!manager.userExists()) {
            findViewById(R.id.changeNameLayout).setVisibility(View.GONE);
            findViewById(R.id.deleteDataLayout).setVisibility(View.GONE);
        }

        Button deleteDataButton = findViewById(R.id.deleteDataButton);
        deleteDataButton.setOnClickListener(this);

        findViewById(R.id.resetButton).setOnClickListener(this);

        leftHandedCheckBox = findViewById(R.id.leftHandedCheckBox);
        leftHandedCheckBox.setChecked(App.getLeftHanded()); // set leftHanded in app accordingly
        leftHandedCheckBox.setOnClickListener(this);

        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.submitButton) {
            updateName();
            hideKeyboard();
        }
        if (v.getId() == R.id.deleteDataButton) {
            deleteDialog();
        }
        if (v.getId() == R.id.resetButton) {
            resetDialog();
        }
        if (v.getId() == R.id.leftHandedCheckBox) {
            App.setLeftHanded(leftHandedCheckBox.isChecked());
        }
        if (v.getId() == R.id.aboutButton) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    private void updateName() {
        String newName = nameEditText.getText().toString().trim();
        newName = FireInput.analyzeString(newName);

        manager.updateName(newName);
        nameEditText.getText().clear();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();

        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        String message = "Do you really want to delete your data?";
        builder.setMessage(message);


        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog deleteDialog = builder.create();

        deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positive = deleteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positive.setBackgroundResource(R.drawable.oval_selector);
                positive.setTextColor(Color.WHITE);

                Button negative = deleteDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negative.setBackgroundResource(R.drawable.oval_selector);
                negative.setTextColor(Color.WHITE);
            }
        });

        deleteDialog.show();
    }

    private void deleteData() {
        manager.deleteUserData();
    }

    private void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        String message = "Do you really want to reset POMMESMANN? \n" +
                "Your submitted highscore will also be deleted.";
        builder.setMessage(message);


        builder.setPositiveButton("reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetPommesmann();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog deleteDialog = builder.create();

        deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positive = deleteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positive.setBackgroundResource(R.drawable.oval_selector);
                positive.setTextColor(Color.WHITE);

                Button negative = deleteDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negative.setBackgroundResource(R.drawable.oval_selector);
                negative.setTextColor(Color.WHITE);
            }
        });

        deleteDialog.show();
    }

    private void resetPommesmann() {
        ShopDatabaseHelper.deleteDatabase(this);
        App.setLevelscore(-1);
        App.setHighscore(0);
        App.setCoins(-1 * App.getCoins());
        manager.deleteUserData();
    }



    // UIInterface from FireManager
    @Override
    public void hideButton() {
    }

    @Override
    public void setHint(String name) {
        nameEditText.setHint(name);
    }



    @Override
    public void chooseDifferentName(boolean firstSignIn) {
        fireUserInterface.differentNameDialog(firstSignIn);
    }

    @Override
    public void fillHighscoreDialog(ArrayList<String> scores, ArrayList<String> names) {
    }


    // FireConnectionInterface from FireUserInterface
    @Override
    public void login(String name) {
    }

    @Override
    public void dummyLogin() {

    }

    @Override
    public void differentName(String name) {
        if (manager.userExists()) {
            manager.validateName(name, false);
        } else {
            manager.validateName(name, true);
        }
    }
}
