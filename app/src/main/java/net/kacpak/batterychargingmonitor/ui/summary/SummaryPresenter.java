package net.kacpak.batterychargingmonitor.ui.summary;

import android.os.Handler;
import android.support.annotation.NonNull;

import net.kacpak.batterychargingmonitor.data.BatteryDataRepository;
import net.kacpak.batterychargingmonitor.data.BatteryStatus;

public class SummaryPresenter implements SummaryContract.UserActionsListener {

    /**
     * Czas między kolejnymi aktualizacjami interfejsu użytkownika
     */
    private static final int DATA_UPDATE_INTERVAL = 500;

    /**
     * Widok
     */
    private final SummaryContract.View mSummaryView;

    /**
     * Wskazuje czy należy zaktualizować widok
     */
    private boolean mUpdateData = false;

    /**
     * Stan baterii
     */
    private BatteryStatus mBatteryStatus;

    /**
     * Tworzy Presenter dla widoku podsumowania ({@link SummaryContract.View}) z automatyczną aktualizacją
     * @param mSummaryView
     */
    public SummaryPresenter(@NonNull SummaryContract.View mSummaryView) {
        this.mSummaryView = mSummaryView;
        updateBatteryStatus();
    }

    /**
     * Aktualizuje obecny stan baterii
     */
    private void updateBatteryStatus() {
        mBatteryStatus = new BatteryDataRepository().getStatus();
    }

    /**
     * Aktualizuje dane interfejsu użytkownika
     */
    @Override
    public void updateAllData() {
        mSummaryView.setBatteryChargeIndicator(mBatteryStatus.getPercentage());
    }

    /**
     * Aktywuje automatyczne odświeżanie interfejsu użytkownika
     */
    @Override
    public void startUpdates() {
        mUpdateData = true;

        final Handler handler = new Handler();
        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                if (mUpdateData) {
                    updateBatteryStatus();
                    updateAllData();
                    handler.postDelayed(this, DATA_UPDATE_INTERVAL);
                }
            }
        };
        handler.postDelayed(updateTask, DATA_UPDATE_INTERVAL);
    }

    /**
     * Zatrzymuje akutalizację interfejsu użytkownika
     */
    @Override
    public void stopUpdates() {
        mUpdateData = false;
    }

}
