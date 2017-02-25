package com.example.amanda.scorekeeperversion4;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amanda.scorekeeperversion4.data.PlayerContract.PlayerEntry;

/**
 * Allows user to create a new player or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * EditText field to enter the player's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the player's score
     */
    private EditText mScoreEditText;

    //Content URI for the existing player (null if it's a new player)
    private Uri mCurrentPlayerUri;

    //Loader number
    public static final int PLAYER_LOADER = 0;

    //boolean for checking if user changed any parts of the form
    private boolean mPlayerHasChanged = false;

    //check if changes were made
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPlayerHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Use getIntent() and getData()to get the URI associated with the intent
        mCurrentPlayerUri = getIntent().getData();

        //Set title of the EditorActivity.
        //Set title to "Edit Player" if ListView item was selected
        //Otherwise, change app bar to say "Add a Player"
        if (mCurrentPlayerUri != null) {
            //This is an existing player that is being edited
            setTitle(R.string.editor_activity_title_edit_player);

            //initialize Loader
            getLoaderManager().initLoader(PLAYER_LOADER, null, this);
        } else {
            //This is a new player that is being added
            setTitle(getString(R.string.editor_activity_title_new_player));
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_player_name);
        mScoreEditText = (EditText) findViewById(R.id.edit_player_score);

        //Set listeners that will track if the edit player form has changed
        mNameEditText.setOnTouchListener(mTouchListener);
        mScoreEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        //Hide "Delete" item in options menu
        MenuItem item = menu.findItem(R.id.action_delete);
        item.setVisible(false);

        if (mCurrentPlayerUri != null){
            //Unhide "Delete" in options menu since this means we are editing
            //an existing player that can be deleted
            item.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save player to the database
                savePlayer();
                //Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deletePlayer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save new player in database
     */
    private void savePlayer() {
        //Get player data from user input
        //use trim to eliminate leading or trailing whitespace
        String nameString = mNameEditText.getText().toString().trim();
        String scoreString = mScoreEditText.getText().toString().trim();


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

        if (mCurrentPlayerUri != null) {
            //pass the content resolver the updated player information
            int rowsAffected = getContentResolver().update(mCurrentPlayerUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_player_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_player_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insert the new row using PlayerProvider
            Uri newUri = getContentResolver().insert(PlayerEntry.CONTENT_URI, values);

            Log.e("Saving a Player", String.valueOf(newUri));

            if (newUri != null) {
                Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_player_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.editor_insert_player_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Perform the deletion of the player in the database.
     */
    private void deletePlayer() {

        // Only perform the delete if this is an existing player.
        if (mCurrentPlayerUri != null) {
            // Call the ContentResolver to delete the player at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPlayerUri
            // content URI already identifies the player that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPlayerUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were affected, then there was an error with deleting the row
                Toast.makeText(this, getString(R.string.editor_delete_player_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_player_successful),
                        Toast.LENGTH_SHORT).show();

                //Exit activity
                finish();
            }
        }
    }

    //Create and return a loader that queries data for a single player
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("EditorActivity", "onCreateLoader() called");
        //Define a projection that specifies the columns from the table we care about
        String[] projection = {
                PlayerEntry._ID,
                PlayerEntry.COLUMN_PLAYER_NAME,
                PlayerEntry.COLUMN_PLAYER_SCORE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentPlayerUri,         // Query the content URI for the current player
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    //Update the editor fields with the data for the current player
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Check to see if cursor is empty
        if (cursor.getCount()>0) {
            //need to set Cursor to 0th position
            cursor.moveToFirst();

            //Find the columns of player attributes we are interested in
            int nameColumnIndex = cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_NAME);
            int scoreColumnIndex = cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_SCORE);

            //Read attributes from the Cursor for the current player
            String playerName = cursor.getString(nameColumnIndex);
            String playerScore = cursor.getString(scoreColumnIndex);

            //Populate views with extracted data
            mNameEditText.setText(playerName);
            mScoreEditText.setText(playerScore);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mScoreEditText.setText("");
    }
}
