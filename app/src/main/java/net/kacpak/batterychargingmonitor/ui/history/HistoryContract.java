package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

public interface HistoryContract {
    interface View {
        void swapCursor(Cursor cursor);
    }

    interface UserActionsListener {
        void onClick(long id);
        Loader<Cursor> onCreateLoader(int id, Bundle args);
        void onLoadFinished(Cursor data);
        void onLoaderReset();
    }
}
