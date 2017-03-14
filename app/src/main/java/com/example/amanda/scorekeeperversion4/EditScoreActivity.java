package com.example.amanda.scorekeeperversion4;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.example.amanda.scorekeeperversion4.data.PlayerContract;

/**
 * Allows the user to edit the score of a player
 */

public class EditScoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * EditText field to enter the player's score
     */
    private EditText mScoreEditText;

    /**
     * String to hold player's name
     */
    private String mPlayerName;

    //Loader number
    public static final int PLAYER_LOADER = 0;

    //Content URI for the existing player (null if it's a new player)
    private Uri mCurrentPlayerUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_score);

        //Use getIntent() and getData()to get the URI associated with the intent
        mCurrentPlayerUri = getIntent().getData();

        // Find all relevant views that we will need to read user input from
        mScoreEditText = (EditText) findViewById(R.id.edit_player_score);

        //initialize Loader
        getLoaderManager().initLoader(PLAYER_LOADER, null, this);
    }

    //Create and return a loader that queries data for a single player
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("EditScoreActivity", "onCreateLoader() called");
        //Define a projection that specifies the columns from the table we care about
        String[] projection = {
                PlayerContract.PlayerEntry._ID,
                PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME,
                PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE};

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
            int nameColumnIndex = cursor.getColumnIndex(PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME);
            int scoreColumnIndex = cursor.getColumnIndex(PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE);

            //Read attributes from the Cursor for the current player
            mPlayerName = cursor.getString(nameColumnIndex);
            String playerScore = cursor.getString(scoreColumnIndex);

            //Populate views with extracted data
            mScoreEditText.setText(playerScore);

            //Set title with extracted name
            setTitle(mPlayerName);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mPlayerName = "";
        mScoreEditText.setText("");
    }
}
