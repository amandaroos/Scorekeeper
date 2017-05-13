package com.amandafarrell.www.scorekeeper.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The PlayerContract class is a container for constants that define names for URIs,
 * tables, and columns.
 * The contract class allows you to use the same constants across all the other
 * classes in the same package. This lets you change a column name in one place and
 * have it propagate throughout your code.
 */

public class PlayerContract implements BaseColumns {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PlayerContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.amanda.scorekeeper";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.scorekeeper/players/ is a valid path for
     * looking at player data. content://com.example.android.player/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PLAYERS = "players";

    /**
     * Inner class that defines constant values for the players database table.
     * Each entry in the table represents a single player.
     */
    public static class PlayerEntry implements BaseColumns{

        /** The content URI to access the player data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLAYERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of players.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single player.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYERS;

        /** Name of database table for players */
        public final static String TABLE_NAME = "players";

        /**
         * Unique ID number for the player (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the player.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PLAYER_NAME ="name";


        /**
         * The player's current score
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PLAYER_SCORE = "score";
    }
}
