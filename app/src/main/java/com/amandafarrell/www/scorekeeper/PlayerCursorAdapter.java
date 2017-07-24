package com.amandafarrell.www.scorekeeper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amandafarrell.www.scorekeeper.data.PlayerContract;

/**
 * {@link PlayerCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of player data as its data source. This adapter knows
 * how to create list items for each row of player data in the {@link Cursor}.
 */

public class PlayerCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link PlayerCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public PlayerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Return the list item view
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the player data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current player can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        //find views to populate in inflated layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView scoreTextView = (TextView) view.findViewById(R.id.score);

        //Find the columns of player attributes we are interested in
        int nameColumnIndex = cursor.getColumnIndex(PlayerContract.PlayerEntry.COLUMN_PLAYER_NAME);
        int scoreColumnIndex = cursor.getColumnIndex(PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE);

        //Read attributes from the Cursor for the current player
        String playerName = cursor.getString(nameColumnIndex);
        final String playerScore = cursor.getString(scoreColumnIndex);

        //Populate views with extracted daa
        nameTextView.setText(playerName);
        scoreTextView.setText(playerScore);

        //Set onclick listeners on plus and minus buttons
        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        Button plusButton = (Button) view.findViewById(R.id.plus_button);

        int playerIdColumnIndex = cursor.getColumnIndex(PlayerContract.PlayerEntry._ID);
        int playerId = cursor.getInt(playerIdColumnIndex);

        final Uri currentPlayerUri = ContentUris.withAppendedId(PlayerContract.PlayerEntry.CONTENT_URI, playerId);

        final int incrementAmount = 1;

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentScore = Integer.parseInt(playerScore.trim());

                // Create a new map of values, where column name is the key,
                // and player score is the value
                ContentValues values = new ContentValues();

                // Increment the score by the increment amount
                int score = currentScore - incrementAmount;

                values.put(PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE, score);

                //pass the content resolver the updated player score
                int rowsAffected = context.getContentResolver().update(currentPlayerUri, values, null, null);
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentScore = Integer.parseInt(playerScore.trim());

                // Create a new map of values, where column name is the key,
                // and player score is the value
                ContentValues values = new ContentValues();

                // Increment the score by the increment amount
                int score = currentScore + incrementAmount;


                values.put(PlayerContract.PlayerEntry.COLUMN_PLAYER_SCORE, score);

                //pass the content resolver the updated player score
                int rowsAffected = context.getContentResolver().update(currentPlayerUri, values, null, null);
            }
        });
    }
}
