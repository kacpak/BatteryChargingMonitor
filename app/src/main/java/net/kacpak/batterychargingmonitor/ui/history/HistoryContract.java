package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import java.util.List;

public interface HistoryContract {
    interface View {
        void swapCursor(Cursor cursor);
        void showDeletedCountMessage(int count);
        void showMergedCountMessage(int count);
    }

    interface UserActionsListener {
        Loader<Cursor> onCreateLoader(int id, Bundle args);
        void onLoadFinished(Cursor data);
        void onLoaderReset();
        void removeIrrelevantEntries();
        void removeEntries(List<Long> entries);
        void mergeEntries(List<Long> entries);
    }
}
