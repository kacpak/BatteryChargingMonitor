package net.kacpak.batterychargingmonitor.ui.summary;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.BatteryDataRepository;
import net.kacpak.batterychargingmonitor.data.BatteryStatus;
import net.kacpak.batterychargingmonitor.data.UserPreferences;

import java.lang.ref.WeakReference;

public class SummaryPresenter implements SummaryContract.UserActionsListener {

    /**
     * Czas między kolejnymi aktualizacjami interfejsu użytkownika
     */
    private static final int DATA_UPDATE_INTERVAL = 500;

    /**
     * Widok
     */
    private final WeakReference<SummaryContract.View> mView;

    /**
     * Kontekst aplikacji
     */
    private final Context mContext;

    /**
     * Wskazuje czy należy zaktualizować widok
     */
    private boolean mUpdateData = false;

    /**
     * Stan baterii
     */
    private BatteryStatus mBatteryStatus;

    private Handler mRunnableHandler;
    private Runnable mUpdater;

    /**
     * Tworzy Presenter dla widoku podsumowania ({@link SummaryContract.View}) z automatyczną aktualizacją
     * @param view
     */
    public SummaryPresenter(@NonNull SummaryContract.View view, Context context) {
        mView = new WeakReference<>(view);
        mContext = context;
        updateBatteryStatus();
        updateView();
    }

    /**
     * Aktualizuje obecny stan baterii
     */
    private void updateBatteryStatus() {
        mBatteryStatus = new BatteryDataRepository(mContext).getStatus();
    }

    /**
     * Aktualizuje dane interfejsu użytkownika
     */
    @Override
    public void updateView() {
        SummaryContract.View view = mView.get();

        if (null == view)
            return;

        // Obecny stan naładowania baterii w procentach
        view.setBatteryChargeIndicator(mBatteryStatus.getChargePercentage());

        // Obecna temperatura baterii w wybranej jednostce
        if (new UserPreferences(mContext).isTemperatureInCelsius())
            view.setBatteryTemperatureInCelsius(mBatteryStatus.getTemperatureInCelsius());
        else
            view.setBatteryTemperatureInFahrenheit(mBatteryStatus.getTemperatureInFahrenheit());

        // Napięcie na baterii
        view.setBatteryVoltage(mBatteryStatus.getVoltage());

        // Natężenie prądu
        if (mBatteryStatus.isCurrentAvailable()) {
            if (mBatteryStatus.isCurrentAverageAvailable())
                view.setBatteryCurrent(mBatteryStatus.getCurrent(), mBatteryStatus.getCurrentAverage());
            else
                view.setBatteryCurrent(mBatteryStatus.getCurrent());
        } else
            view.hideBatteryCurrentData();

        // Stan zdrowia baterii
        int healthStringId;
        switch (mBatteryStatus.getHealthInformation()) {
            case 2: healthStringId = R.string.health_good; break;
            case 3: healthStringId = R.string.health_overheat; break;
            case 4: healthStringId = R.string.health_dead; break;
            case 5: healthStringId = R.string.health_over_voltage; break;
            case 6: healthStringId = R.string.health_unspecified_failure; break;
            case 7: healthStringId = R.string.health_cold; break;
            default: healthStringId = R.string.health_unknown;
        }
        view.setBatteryHealth(healthStringId);

        // Licznik ładowań
        view.setBatteryChargingCounter(
                new BatteryDataRepository(mContext).getChargedCount()
        );
    }

    /**
     * Aktywuje automatyczne odświeżanie interfejsu użytkownika
     */
    @Override
    public void startUpdates() {
        mUpdateData = true;

        if (mRunnableHandler != null)
            mRunnableHandler.removeCallbacks(mUpdater);

        mRunnableHandler = new Handler();
        mUpdater = new Runnable() {
            @Override
            public void run() {
                if (mUpdateData) {
                    updateBatteryStatus();
                    updateView();
                    mRunnableHandler.postDelayed(mUpdater, DATA_UPDATE_INTERVAL);
                }
            }
        };
        mRunnableHandler.postDelayed(mUpdater, DATA_UPDATE_INTERVAL);
    }

    /**
     * Zatrzymuje akutalizację interfejsu użytkownika
     */
    @Override
    public void stopUpdates() {
        mUpdateData = false;
        if (mRunnableHandler != null)
            mRunnableHandler.removeCallbacks(mUpdater);
    }

}
