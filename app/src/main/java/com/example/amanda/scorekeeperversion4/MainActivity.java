package com.example.amanda.scorekeeperversion4;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import com.example.amanda.scorekeeperversion4.data.PlayerContract.PlayerEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PLAYER_LOADER = 0;

    private ActionMode mActionMode;

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

                // Create a new map of values, where column names are the keys,
                // and player attributes from the editor are the values
                ContentValues values = new ContentValues();
                values.put(PlayerEntry.COLUMN_PLAYER_NAME, "New Player");
                values.put(PlayerEntry.COLUMN_PLAYER_SCORE, 0);

                // Insert the new row using PlayerProvider
                Uri newUri = getContentResolver().insert(PlayerEntry.CONTENT_URI, values);

/*                if (newUri != null) {
                    Toast.makeText(getApplicationContext(), "new player saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.editor_insert_player_failed), Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        //Find the ListView which will be populated with the player data
        ListView playerListView = (ListView) findViewById(R.id.list_view_player);

        //Find and set empty view on the ListView so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        playerListView.setEmptyView(emptyView);

        //Set up an Adapter to create a list item for each row of pet data in the Cursor
        //There is no pet data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new PlayerCursorAdapter(this, null);
        playerListView.setAdapter(mCursorAdapter);

        //set an empty footer view at the end of the list to avoid the fab covering information
        TextView empty = new TextView(this);
        empty.setHeight(150);
        //The footer view cannot be selected
        playerListView.addFooterView(empty, 0, false);

        //set click listeners on each list item
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditScoreActivity.class);

                //Append the id of the current pet to the content URI
                Uri currentPlayerUri = ContentUris.withAppendedId(PlayerEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentPlayerUri);

                startActivity(intent);
            }
        });

        //contextual action mode creates contextual action bar for selected list items
        playerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("MAin", "LongClickListener called");
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
                mActionMode.setTag(id);
                view.setSelected(true);
                return true;
            }
        });

        playerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //Initialize Loader
        getLoaderManager().initLoader(PLAYER_LOADER, null, this);
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


            long id = (long) mode.getTag();

            //Append the id of the current pet to the content URI
            Uri currentPlayerUri = ContentUris.withAppendedId(PlayerEntry.CONTENT_URI, id);

            switch (item.getItemId()) {
                case R.id.cab_edit:

                    Intent intent = new Intent(MainActivity.this, EditNameActivity.class);

                    //Set the URI on the data field of the intent
                    intent.setData(currentPlayerUri);

                    startActivity(intent);

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.cab_delete:
                    // Call the ContentResolver to delete the player at the given content URI.
                    // Pass in null for the selection and selection args because the mCurrentPlayerUri
                    // content URI already identifies the player that we want.
                    int rowsDeleted = getContentResolver().delete(currentPlayerUri, null, null);


                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_delete_all_entries:
                deleteAllPlayers();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePlayer(Uri uri) {



    }

    public void deleteAllPlayers() {
        // Call the ContentResolver to delete the player at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentPlayerUri
        // content URI already identifies the player that we want.
        int rowsDeleted = getContentResolver().delete(PlayerEntry.CONTENT_URI, null, null);

/*        if (rowsDeleted == 0) {
            // If no rows were affected, then there was an error with deleting the row
            Toast.makeText(this, "Error with deleting all players",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "All players deleted",
                    Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies the columns from the table we care about
        String[] projection = new String[] {
                PlayerEntry._ID,
                PlayerEntry.COLUMN_PLAYER_NAME,
                PlayerEntry.COLUMN_PLAYER_SCORE
        };

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                PlayerEntry.CONTENT_URI,
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
