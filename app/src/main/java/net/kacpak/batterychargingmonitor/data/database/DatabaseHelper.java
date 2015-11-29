package net.kacpak.batterychargingmonitor.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "battery_history.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DATA_TABLE = "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                DataEntry._ID + " INTEGER PRIMARY KEY, " +
                DataEntry.COLUMN_CHARGE_FINISHED + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_TYPE + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_START + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_START_PERCENTAGE + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_START_VOLTAGE + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_START_TEMPERATURE_C + " REAL NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_STOP + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_STOP_PERCENTAGE + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_STOP_VOLTAGE + " INTEGER NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_STOP_TEMPERATURE_C + " REAL NOT NULL DEFAULT 0, " +
                DataEntry.COLUMN_NOTE + " TEXT DEFAULT NULL " +
                " );";

        db.execSQL(SQL_CREATE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        onCreate(db);
    }
}
