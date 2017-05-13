package com.amandafarrell.www.scorekeeper;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.amandafarrell.www.scorekeeper.data.PlayerContract.PlayerEntry;

/**
 * Allows user to edit the player's score
 */
public class EditScoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mScoreEditText;
    private TextView mName;
    private TextView mScore;

    private final String LOG_TAG = EditScoreActivity.class.getSimpleName();

    //Content URI for the player
    private Uri mCurrentPlayerUri;

    //Loader number
    public static final int PLAYER_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_score);

        //Use getIntent() and getData()to get the URI associated with the intent
        mCurrentPlayerUri = getIntent().getData();

        getLoaderManager().initLoader(PLAYER_LOADER, null, this);

        // Find all relevant views
        mName = (TextView) findViewById(R.id.player_name);
        mScore = (TextView) findViewById(R.id.edit_player_score);
        mScoreEditText = (EditText) findViewById(R.id.score_input);
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
                //Save player to the database
                saveScore();
                //Exit activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save player score in database
     */
    private void saveScore() {
        //Get player data from user input
        String scoreString = mScoreEditText.getText().toString().trim();

        //Get player's current score
        int currentScore = Integer.parseInt(mScore.getText().toString().trim());

        // Create a new map of values, where column name is the key,
        // and player score from the editor is the value
        ContentValues values = new ContentValues();

        // If the score is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int score = currentScore;

/*        if (Integer.parseInt(scoreString)) {
            Toast.makeText(this, "Score is too high to save", Toast.LENGTH_SHORT).show();
        } else {*/
            if (!TextUtils.isEmpty(scoreString)) {

                score += Integer.parseInt(scoreString);

            } else {
                return;
            }

        values.put(PlayerEntry.COLUMN_PLAYER_SCORE, score);

        //pass the content resolver the updated player information
        int rowsAffected = getContentResolver().update(mCurrentPlayerUri, values, null, null);
    }

    //Create and return a loader that queries data for a single player
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
        if (cursor.getCount() > 0) {
            //need to set Cursor to 0th position
            cursor.moveToFirst();

            //Find the columns of player attributes we are interested in
            int nameColumnIndex = cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_NAME);
            int scoreColumnIndex = cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_SCORE);

            //Read attributes from the Cursor for the current player
            String playerName = cursor.getString(nameColumnIndex);
            String playerScore = cursor.getString(scoreColumnIndex);

            //Populate views with extracted data
            mName.setText(playerName);
            mScore.setText(playerScore);

            //Save and finish EditScore activity when "done" is pressed on keyboard
            mScoreEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        saveScore();
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
        mScoreEditText.setText("");
    }
}
