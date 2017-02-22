package com.example.amanda.scorekeeperversion4.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;


/**
 * This ContentProvider manages access to the data stored by the app
 * and provides mechanisms for sharing data (within the app or with other apps)
 * and for defining data security.
 */

public class PlayerProvider extends ContentProvider {

    /** URI matcher code for the content URI for the players table */
    private static final int PLAYERS = 100;

    /** URI matcher code for the content URI for a single player in the players table */
    private static final int PLAYERS_ID = 101;

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
        sUriMatcher.addURI(PlayerContract.CONTENT_AUTHORITY, PlayerContract.PATH_PLAYERS + "/#", PLAYERS_ID);
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
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
