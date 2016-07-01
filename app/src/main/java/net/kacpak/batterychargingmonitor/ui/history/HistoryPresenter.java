package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Context;
import android.support.annotation.NonNull;
import net.kacpak.batterychargingmonitor.data.database.HistoryRepository;
import net.kacpak.batterychargingmonitor.data.events.DatabaseUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;


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
     * Tworzy Presenter dla widoku podsumowania ({@link HistoryContract.View}) z automatyczną aktualizacją
     * @param historyView   Widok
     * @param context       Kontekst widoku
     */
    public HistoryPresenter(@NonNull HistoryContract.View historyView, @NonNull Context context) {
        mView = new WeakReference<>(historyView);
        mContext = new WeakReference<>(context);
        EventBus.getDefault().register(this);
        historyView.swapCharges(new HistoryRepository(context).getCharges());
    }

    @Override
    public void removeIrrelevantEntries() {
        long deletedCount = new HistoryRepository(mContext.get()).deleteIrrelevantCharges();

        HistoryContract.View view = mView.get();
        if (null != view)
            view.showDeletedCountMessage(deletedCount);
    }

    @Override
    public void removeEntries(long... entries) {
        long deletedCount = new HistoryRepository(mContext.get()).deleteCharges(entries);

        HistoryContract.View view = mView.get();
        if (null != view)
            view.showDeletedCountMessage(deletedCount);
    }

    @Override
    public void mergeEntries(long... entries) {
        long mergedCount = new HistoryRepository(mContext.get()).mergeCharges(entries);

        HistoryContract.View view = mView.get();
        if (null != view)
            view.showMergedCountMessage(mergedCount);
    }

    @Subscribe
    public void onDatabaseUpdateEvent(DatabaseUpdateEvent event) {
        HistoryContract.View view = mView.get();
        if (null != view)
            view.swapCharges(new HistoryRepository(mContext.get()).getCharges());
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }
}
