package com.example.amanda.scorekeeperversion4;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amanda.scorekeeperversion4.data.PlayerContract.PlayerEntry;

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
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView changeScoreImageView = (ImageView) view.findViewById(R.id.change_score);
        //changeScoreImageView.setOnClickListener();


        //find views to populate in inflated layout
        TextView nameTextView = (TextView) view. findViewById(R.id.name);
        TextView scoreTextView = (TextView) view.findViewById(R.id.score);

        //Find the columns of player attributes we are interested in
        int nameColumnIndex = cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_NAME);
        int scoreColumnIndex = cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_SCORE);

        //Read attributes from the Cursor for the current player
        String playerName = cursor.getString(nameColumnIndex);
        String playerScore = cursor.getString(scoreColumnIndex);

        // If the player name is empty string or null, then use some default text
        // that says "New Player", so the TextView isn't blank.
        if (TextUtils.isEmpty(playerName)){
            playerName = context.getString(R.string.main_default_name);
        }

        //Populate views with extracted daa
        nameTextView.setText(playerName);
        scoreTextView.setText(playerScore);
    }
}
