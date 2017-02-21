package com.example.amanda.scorekeeperversion4;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amanda.scorekeeperversion4.data.PlayerContract.PlayerEntry;

/**
 * Allows user to create a new player or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the player's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the player's score
     */
    private EditText mScoreEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_player_name);
        mScoreEditText = (EditText) findViewById(R.id.edit_player_score);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save pet to the database
                savePet();
                //Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //TODO
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save new pet in database
     */
    private void savePet() {
        //Get player data from user input
        //use trim to eliminate leading or trailing whitespace
        String nameString = mNameEditText.getText().toString().trim();
        String scoreString = mScoreEditText.getText().toString().trim();

//        //Prevent crash when saving a blank editor
//        //Check if this is supposed to be a new pet and check if all the fields are blank
//        if (mCurrentPetUri == null &&
//                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
//                TextUtils.isEmpty(weightString) && mGender == PetEntry.GENDER_UNKNOWN) {
//            // Since no fields were modified, we can return early without creating a new pet.
//            // No need to create ContentValues and no need to do any ContentProvider operations.
//            return;
//        }

        // Create a new map of values, where column names are the keys,
        // and player attributes from the editor are the values
        ContentValues values = new ContentValues();
        values.put(PlayerEntry.COLUMN_PLAYER_NAME, nameString);

        // If the score is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int score = 0;
        if (!TextUtils.isEmpty(scoreString)) {
            score = Integer.parseInt(scoreString);
        }
        values.put(PlayerEntry.COLUMN_PLAYER_SCORE, score);

        //TODO if the name is not provided by the user, provide a default value, similar to code
        //above for default score

        //For testing
        String valuesData = String.valueOf(values);
        Log.e("EditorActivity", valuesData);

//        if (mCurrentPetUri != null) {
//            //pass the content resolver the updated pet information
//            int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
//
//            // Show a toast message depending on whether or not the update was successful.
//            if (rowsAffected == 0) {
//                // If no rows were affected, then there was an error with the update.
//                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                // Otherwise, the update was successful and we can display a toast.
//                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
//                        Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            // Insert the new row using PetProvider
//            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
//
//            if (newUri != null) {
//                Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
//            }
//        }
    }

}
