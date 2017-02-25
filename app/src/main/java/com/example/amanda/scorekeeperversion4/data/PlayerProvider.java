package com.example.amanda.scorekeeperversion4.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.amanda.scorekeeperversion4.data.PlayerContract.PlayerEntry;

/**
 * This ContentProvider manages access to the data stored by the app
 * and provides mechanisms for sharing data (within the app or with other apps)
 * and for defining data security.
 */

public class PlayerProvider extends ContentProvider {

    /** URI matcher code for the content URI for the players table */
    private static final int PLAYERS = 100;

    /** URI matcher code for the content URI for a single player in the players table */
    private static final int PLAYER_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(PlayerContract.CONTENT_AUTHORITY, PlayerContract.PATH_PLAYERS, PLAYERS);
        sUriMatcher.addURI(PlayerContract.CONTENT_AUTHORITY, PlayerContract.PATH_PLAYERS
                + "/#", PLAYER_ID);
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PlayerProvider.class.getSimpleName();


    //Database helper object
    private PlayerDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new PlayerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match){
            case PLAYERS:
                // For the PLAYERS code, query the players table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the players table.
                cursor = database.query(PlayerEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PLAYER_ID:
                // For the PLAYER_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.scorekeeper/players/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PlayerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the players table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PlayerEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set notification URI on the Cursor
        //so we know what content URI the Cursor was created for.
        //If the the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAYERS:
                return PlayerEntry.CONTENT_LIST_TYPE;
            case PLAYER_ID:
                return PlayerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAYERS:
                return insertPlayer(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a player into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPlayer(Uri uri, ContentValues values) {

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId = database.insert(PlayerEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the player content URI
        //uri: content://com.example.android.scorekeeper/players
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAYERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PlayerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PLAYER_ID:
                // Delete a single row given by the ID in the URI
                selection = PlayerEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PlayerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAYERS:
                return updatePlayer(uri, contentValues, selection, selectionArgs);
            case PLAYER_ID:
                // For the PLAYER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PlayerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePlayer(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update players in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more players).
     * Return the number of rows that were successfully updated.
     */
    private int updatePlayer(Uri uri, ContentValues values, String selection,
                             String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        int rowsUpdated = database.update(PlayerEntry.TABLE_NAME, values, selection, selectionArgs);

        //Notify all listeners that the data has changed for the player content URI
        //given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.e("PlayerProvider", ".notifyChange called");
        }

        return rowsUpdated;
    }
}
