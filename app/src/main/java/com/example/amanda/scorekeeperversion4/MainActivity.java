package com.example.amanda.scorekeeperversion4;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.amanda.scorekeeperversion4.data.PlayerContract.PlayerEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PLAYER_LOADER = 0;

    //The adapter that knows how to create list item views given a cursor
    PlayerCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
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

        Log.e("MainActivity", "mCursorAdapter set on playerListView");

        //set click listeners on each list item
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                //Append the id of the current pet to the content URI
                Uri currentPlayerUri = ContentUris.withAppendedId(PlayerEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentPlayerUri);

                startActivity(intent);
            }
        });

        //Initialize Loader
        getLoaderManager().initLoader(PLAYER_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Define a projection that specifies the columns from the table we care about
        String[] projection = new String[] {
                PlayerEntry._ID,
                PlayerEntry.COLUMN_PLAYER_NAME,
                PlayerEntry.COLUMN_PLAYER_SCORE
        };
        Log.e("onCreateLoader", "is this thing loading?");

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
