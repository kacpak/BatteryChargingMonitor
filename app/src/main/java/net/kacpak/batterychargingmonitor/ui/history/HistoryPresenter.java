package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import net.kacpak.batterychargingmonitor.data.BatteryDataRepository;
import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;

import java.lang.ref.WeakReference;
import java.util.List;


public class HistoryPresenter implements HistoryContract.UserActionsListener {

    /**
     * Kontekst aplikacji
     */
    private final WeakReference<Context> mContext;

    /**
     * Widok
     */
    private WeakReference<HistoryContract.View> mView;

    /**
     * Pobierane dane z bazy danych
     */
    private static final String[] sProjection = {
            DataEntry._ID,
            DataEntry.COLUMN_START,
            DataEntry.COLUMN_STOP,
            DataEntry.COLUMN_START_PERCENTAGE,
            DataEntry.COLUMN_STOP_PERCENTAGE,
            DataEntry.COLUMN_TYPE,
            DataEntry.COLUMN_CHARGE_FINISHED
    };

    /**
     * Tworzy Presenter dla widoku podsumowania ({@link HistoryContract.View}) z automatyczną aktualizacją
     * @param mHistoryView Widok
     * @param context Kontekst widoku
     */
    public HistoryPresenter(@NonNull HistoryContract.View mHistoryView, @NonNull Context context) {
        mView = new WeakReference<>(mHistoryView);
        mContext = new WeakReference<>(context);
    }

    /**
     * Tworzy Loadera do wczytania danych o historii ładowania
     * @param id id Loadera
     * @param args dodatkowe argumenty
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext.get(),
                DataEntry.CONTENT_URI,
                sProjection,
                null,
                null,
                DataEntry.COLUMN_START + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Cursor data) {
        HistoryContract.View view = mView.get();
        if (null != view)
            view.swapCursor(data);
    }

    @Override
    public void onLoaderReset() {
        HistoryContract.View view = mView.get();
        if (null != view)
            view.swapCursor(null);
    }

    @Override
    public void removeIrrelevantEntries() {
        int deletedCount = new BatteryDataRepository(mContext.get()).deleteIrrelevant();

        HistoryContract.View view = mView.get();
        if (null != view)
            view.showDeletedCountMessage(deletedCount);
    }

    @Override
    public void removeEntries(List<Long> entries) {
        int deletedCount = new BatteryDataRepository(mContext.get()).delete(entries);

        HistoryContract.View view = mView.get();
        if (null != view)
            view.showDeletedCountMessage(deletedCount);
    }

    @Override
    public void mergeEntries(List<Long> entries) {
        int mergedCount = new BatteryDataRepository(mContext.get()).merge(entries);

        HistoryContract.View view = mView.get();
        if (null != view)
            view.showMergedCountMessage(mergedCount);
    }
}
