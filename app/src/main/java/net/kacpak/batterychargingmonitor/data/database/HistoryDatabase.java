package net.kacpak.batterychargingmonitor.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = HistoryDatabase.NAME, version = HistoryDatabase.VERSION)
public class HistoryDatabase {
    public static final String NAME = "HistoryDatabase";
    public static final int VERSION = 1;
}
