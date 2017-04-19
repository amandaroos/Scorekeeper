package com.example.amanda.scorekeeperversion4.data;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.amanda.scorekeeperversion4.EditScoreActivity;
import com.example.amanda.scorekeeperversion4.R;

/**
 * Allows user to edit the player's name
 */

public class EditNameActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mScoreEditName;
    private TextView mName;

    private final String LOG_TAG = EditNameActivity.class.getSimpleName();

    //Content URI for the existing player
    private Uri mCurrentPlayerUri;

    //Loader number
    public static final int PLAYER_LOADER = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        //Use getIntent() and getData()to get the URI associated with the intent
        mCurrentPlayerUri = getIntent().getData();

        getLoaderManager().initLoader(PLAYER_LOADER, null, this);

        // Find all relevant views
        mScoreEditName = (EditText) findViewById(R.id.name_input);
    }

    /**
     * Get user input from editor and save player name in database
     */
    private void savePlayer() {
        //Get player data from user input
        String nameString = mScoreEditName.getText().toString().trim();

        // Create a new map of values, where column name is the key,
        // and player name from the editor is the value
        ContentValues values = new ContentValues();

        values.put(PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME, nameString);

/*        //pass the content resolver the updated player information
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
        }*/
    }


    //Create and return a loader that queries data for a single player
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the table we care about
        String[] projection = {
                PlayerContract.PlayerEntry._ID,
                PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME};

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
        if (cursor.getCount() > 0) {
            //need to set Cursor to 0th position
            cursor.moveToFirst();

            //Find the name column
            int nameColumnIndex = cursor.getColumnIndex(PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME);

            //Read name from the Cursor for the current player
            String playerName = cursor.getString(nameColumnIndex);

            //Populate views with extracted data
            mName.setText(playerName);

            //Save and finish EditScore activity when "done" is pressed on keyboard
            mScoreEditName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        savePlayer();
                        finish();
                        handled = true;
                    }
                    return handled;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mScoreEditName.setText("");
    }
}
