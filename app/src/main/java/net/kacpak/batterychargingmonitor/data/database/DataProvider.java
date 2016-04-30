package net.kacpak.batterychargingmonitor.data.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.kacpak.batterychargingmonitor.data.database.DatabaseContract;
import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;
import net.kacpak.batterychargingmonitor.data.database.DatabaseHelper;

import java.util.ArrayList;

public class DataProvider extends ContentProvider {

    private SQLiteOpenHelper mDatabase;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int DATA = 100;
    private static final int DATA_ID = 101;
    private static final int DATA_UNFINISHED = 102;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.DATA_PATH, DATA);
        matcher.addURI(authority, DatabaseContract.DATA_PATH + "/#", DATA_ID);
        matcher.addURI(authority, DatabaseContract.DATA_PATH_UNFINISHED, DATA_UNFINISHED);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDatabase = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;

        switch (sUriMatcher.match(uri)) {
            case DATA: {
                returnCursor = mDatabase.getReadableDatabase().query(
                        DataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case DATA_ID: {
                long id = DataEntry.getIdFromUri(uri);
                returnCursor = mDatabase.getReadableDatabase().query(
                        DataEntry.TABLE_NAME,
                        projection,
                        DataEntry._ID + " = " + id,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case DATA_UNFINISHED: {
                returnCursor = mDatabase.getReadableDatabase().query(
                        DataEntry.TABLE_NAME,
                        projection,
                        DataEntry.COLUMN_CHARGE_FINISHED + " = 0",
                        null,
                        null,
                        null,
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try {
            returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        } catch (NullPointerException e) {
            Log.e("No content resolver", e.toString());
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DATA:
                return DataEntry.CONTENT_TYPE;
            case DATA_ID:
                return DataEntry.CONTENT_ITEM_TYPE;
            case DATA_UNFINISHED:
                return DataEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case DATA: {
                long id = db.insert(DataEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = DataEntry.buildUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try {
            getContext().getContentResolver().notifyChange(uri, null);

        } catch (NullPointerException e) {
            Log.e("No content resolver", e.toString());
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case DATA: {
                rowsDeleted = db.delete(DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case DATA_ID: {
                long id = DataEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(DataEntry.TABLE_NAME, DataEntry._ID + " = " + id, null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try {
            if (rowsDeleted != 0)
                getContext().getContentResolver().notifyChange(uri, null);

        } catch (NullPointerException e) {
            Log.e("No content resolver", e.toString());
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case DATA: {
                rowsUpdated = db.update(DataEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case DATA_UNFINISHED: {
                Cursor cursor = query(
                        DataEntry.CONTENT_URI_UNFINISHED,
                        new String[] { DataEntry._ID },
                        null,
                        null,
                        null
                );

                ArrayList<Long> ids = new ArrayList<>();
                while (cursor.moveToNext())
                    ids.add(cursor.getLong(0));
                cursor.close();
                String inClause = ids.toString().replace("[", "(").replace("]", ")");

                rowsUpdated = db.update(
                        DataEntry.TABLE_NAME,
                        values,
                        DataEntry._ID + " IN " + inClause,
                        null
                );
                break;
            }
            case DATA_ID: {
                rowsUpdated = db.update(
                        DataEntry.TABLE_NAME,
                        values,
                        DataEntry._ID + " = " + DataEntry.getIdFromUri(uri),
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try {
            if (rowsUpdated != 0)
                getContext().getContentResolver().notifyChange(uri, null);

        } catch (NullPointerException e) {
            Log.e("No content resolver", e.toString());
        }

        return rowsUpdated;
    }
}
