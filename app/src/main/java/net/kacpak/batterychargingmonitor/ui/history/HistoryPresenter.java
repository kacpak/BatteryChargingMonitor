package net.kacpak.batterychargingmonitor.ui.history;

import android.support.annotation.NonNull;


public class HistoryPresenter implements HistoryContract.UserActionsListener {

    private HistoryContract.View mView;

    /**
     * Tworzy Presenter dla widoku podsumowania ({@link HistoryContract.View}) z automatyczną aktualizacją
     * @param mHistoryView
     */
    public HistoryPresenter(@NonNull HistoryContract.View mHistoryView) {
        this.mView = mHistoryView;
    }
}
