package com.example.amanda.scorekeeperversion4.data;

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
     * Inner class that defines constant values for the players database table.
     * Each entry in the table represents a single player.
     */
    public static class PlayerEntry implements BaseColumns{

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
