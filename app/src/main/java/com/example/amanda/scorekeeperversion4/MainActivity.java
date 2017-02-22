package com.example.amanda.scorekeeperversion4;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

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
                Log.e("Main Activity OnCreate", "intent created");
                startActivity(intent);
            }
        });

        //Find the ListView which will be populated with the player data
        ListView playerListView = (ListView) findViewById(R.id.list_view_player);

        //TODO set empty view
//        //Find and set empty view on the ListView so that it only shows when the list has 0 items.
//        View emptyView = findViewById(R.id.empty_view);
//        playerListView.setEmptyView(emptyView);

        //Set up an Adapter to create a list item for each row of pet data in the Cursor
        //There is no pet data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new PlayerCursorAdapter(this, null);
        playerListView.setAdapter(mCursorAdapter);
    }
}
