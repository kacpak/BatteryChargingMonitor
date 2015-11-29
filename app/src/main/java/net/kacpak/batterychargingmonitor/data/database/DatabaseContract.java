package net.kacpak.batterychargingmonitor.data.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "net.kacpak.batterychargingmonitor";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String DATA_PATH = DataEntry.TABLE_NAME;
    public static final String DATA_PATH_UNFINISHED = DATA_PATH + "/" + DataEntry.PATH_UNFINISHED;

    public static final class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "batteryHistory";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_START = "start";
        public static final String COLUMN_START_PERCENTAGE = "start_percentage";
        public static final String COLUMN_START_VOLTAGE = "start_voltage";
        public static final String COLUMN_START_TEMPERATURE_C = "start_temperature_c";
        public static final String COLUMN_STOP = "stop";
        public static final String COLUMN_STOP_PERCENTAGE = "stop_percentage";
        public static final String COLUMN_STOP_VOLTAGE = "stop_voltage";
        public static final String COLUMN_STOP_TEMPERATURE_C = "stop_temperature_c";
        public static final String COLUMN_CHARGE_FINISHED = "charge_finished";
        public static final String PATH_UNFINISHED = "unfinished";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(DATA_PATH).build();

        public static final Uri CONTENT_URI_UNFINISHED =
                CONTENT_URI.buildUpon().appendPath(PATH_UNFINISHED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + DATA_PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + DATA_PATH;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
