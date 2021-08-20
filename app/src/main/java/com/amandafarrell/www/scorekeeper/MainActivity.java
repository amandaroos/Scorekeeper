package com.amandafarrell.www.scorekeeper;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amandafarrell.www.scorekeeper.data.PlayerContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PLAYER_LOADER = 0;

    private ActionMode mActionMode;

    public ListView mPlayerListView;

    //used for tracking the number of players in the list
    private int mPlayerNumber = 0;

    //The adapter that knows how to create list item views given a cursor
    private PlayerCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditScoreActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] projection = {PlayerContract.PlayerEntry._ID};

                Cursor cursor = getContentResolver().query(
                        PlayerContract.PlayerEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

                //create player name string
                try {
                    if (cursor.moveToFirst()) {
                        mPlayerNumber = cursor.getCount() + 1;
                    } else {
                        mPlayerNumber = 1;
                    }
                } catch (NullPointerException e) {
                    Log.e("MainActivity", "moveToFirst: ", e);
                }

                String defaultName = getString(R.string.main_default_name);
                defaultName += " " + mPlayerNumber;


                // Create a new map of values, where column names are the keys,
                // and player attributes from the editor are the values
                ContentValues values = new ContentValues();
                values.put(PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME, defaultName);
                values.put(PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE, 0);

                // Insert the new row using PlayerProvider
                Uri newUri = getContentResolver().insert(PlayerContract.PlayerEntry.CONTENT_URI, values);

                //Finish contextual action bar when an item has been selected
                if (mActionMode != null) {
                    mActionMode.finish();
                }

                mPlayerListView.setSelection(mPlayerListView.getCount() - 1);
                mPlayerListView.requestFocus();

                //Calls onCreateOptionsMenu()
                invalidateOptionsMenu();

                try {
                    cursor.close();
                } catch (NullPointerException e) {
                    Log.e("MainActivity", "cursor.close(): ", e);
                }
            }
        });

        //Find the ListView which will be populated with the player data
        mPlayerListView = (ListView)

                findViewById(R.id.list_view_player);


        //Find and set empty view on the ListView so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        mPlayerListView.setEmptyView(emptyView);

        //set the choice mode for the contextual action bar
        mPlayerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //Set up an Adapter to create a list item for each row of pet data in the Cursor
        //There is no pet data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new

                PlayerCursorAdapter(this, null);
        mPlayerListView.setAdapter(mCursorAdapter);

        //set an empty footer view at the end of the list to avoid the fab covering information
        TextView empty = new TextView(this);
        empty.setHeight(220);
        //The footer view cannot be selected
        mPlayerListView.addFooterView(empty, 0, false);

        //set click listeners on each list item
        mPlayerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditScoreActivity.class);

                //Append the id of the current pet to the content URI
                Uri currentPlayerUri = ContentUris.withAppendedId(PlayerContract.PlayerEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentPlayerUri);

                startActivity(intent);

                //Finish contextual action bar when an item has been selected
                if (mActionMode != null) {
                    mActionMode.finish();
                }

                //remove highlight from the selected player
                mPlayerListView.setItemChecked(position, false);
            }
        });

        //Contextual action mode creates contextual action bar for list items selected with
        //a long press
        mPlayerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {

                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
                if (mActionMode != null) {
                    mActionMode.setTag(id);
                }

                //highlight the selected player by setting as checked
                mPlayerListView.setItemChecked(position, true);

                return true;
            }
        });

        //Initialize Loader
        getLoaderManager().

                initLoader(PLAYER_LOADER, null, this);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //get the id from the action mode tag
            long id = (long) mode.getTag();

            //Append the id of the current pet to the content URI
            Uri currentPlayerUri = ContentUris.withAppendedId(PlayerContract.PlayerEntry.CONTENT_URI, id);

            switch (item.getItemId()) {
                case R.id.cab_edit:
                    Intent intent = new Intent(MainActivity.this, EditNameActivity.class);
                    //Set the URI on the data field of the intent
                    intent.setData(currentPlayerUri);
                    startActivity(intent);
                    // Action picked, so close the CAB
                    mode.finish();
                    return true;
                case R.id.cab_delete:
                    // Call the ContentResolver to delete the player at the given content URI.
                    // Pass in null for the selection and selection args because the mCurrentPlayerUri
                    // content URI already identifies the player that we want.
                    int rowsDeleted = getContentResolver().delete(currentPlayerUri, null, null);
                    // Action picked, so close the CAB
                    mode.finish();
                    return true;
                default:
                    mode.finish();
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Un-check all items to remove highlight color
            mPlayerListView.clearChoices();
            for (int i = 0; i < mPlayerListView.getCount(); i++) {
                mPlayerListView.setItemChecked(i, false);
            }
            mActionMode = null;
        }
    };

    @Override
    protected void onPause() {
        //Finish contextual action bar when an item has been selected
        if (mActionMode != null) {
            mActionMode.finish();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                deleteAllPlayers();
                invalidateOptionsMenu();
                return true;
            case R.id.action_reset_scores:
                resetAllScores();
                return true;
            case R.id.action_donate:
                Intent intent = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_upgrade:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.play_store_direct_link_upgrade))));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.play_store_browser_link_upgrade))));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteAllPlayers() {

        // Call the ContentResolver to delete the player at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentPlayerUri
        // content URI already identifies the player that we want.
        int rowsDeleted = getContentResolver().delete(PlayerContract.PlayerEntry.CONTENT_URI, null, null);
    }

    public void resetAllScores() {
        // Create a new map of values, where column name is the key,
        // and the reset score is the value
        ContentValues values = new ContentValues();
        int scoreReset = 0;
        values.put(PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE, scoreReset);

        //Pass the content resolver the updated player information
        int rowsAffected = getContentResolver().update(PlayerContract.PlayerEntry.CONTENT_URI, values, null, null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies the columns from the table we care about
        String[] projection = new String[]{
                PlayerContract.PlayerEntry._ID,
                PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME,
                PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE
        };

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                PlayerContract.PlayerEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mCursorAdapter.swapCursor(null);
    }
}
