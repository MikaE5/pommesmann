package de.androidmika.pommesmann.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import de.androidmika.pommesmann.App;
import de.androidmika.pommesmann.Firebase.FireManager;
import de.androidmika.pommesmann.R;

public class SettingsActivity extends Activity implements View.OnClickListener, FireManager.DataInterface, FireManager.UIInterface {

    private FireManager manager;

    private CheckBox leftHandedCheckBox;
    private EditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new FireManager(this);

        setContentView(R.layout.settings_activity);

        nameEditText = findViewById(R.id.nameEditText);
        manager.getUserName();

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        if (!manager.userExists())
            submitButton.setClickable(false);

        Button deleteDataButton = findViewById(R.id.deleteDataButton);
        deleteDataButton.setOnClickListener(this);

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
        }
        if (v.getId() == R.id.deleteDataButton) {
            deleteData();
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

        if (newName.length() < 20) {
            manager.updateName(newName);
            nameEditText.setHint(newName);
        }
    }

    private void deleteData() {
        manager.deleteUserData();
    }

    @Override
    public void deleteSuccess() {
        CharSequence text = "Data successfully deleted!";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void deleteFailure() {
        CharSequence text = "Sorry! An error occurred. Data was not deleted!";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateSuccess() {
        CharSequence text = "Name successfully updated!";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateFailure() {
        CharSequence text = "Sorry! An error occurred!";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void hideButton() {
    }

    @Override
    public void setHint(String name) {
        nameEditText.setHint(name);
    }
}
