package net.kacpak.batterychargingmonitor.ui.summary;

import android.os.Handler;
import android.support.annotation.NonNull;

import net.kacpak.batterychargingmonitor.App;
import net.kacpak.batterychargingmonitor.R;
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
    private final SummaryContract.View mView;

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
     * @param mView
     */
    public SummaryPresenter(@NonNull SummaryContract.View mView) {
        this.mView = mView;
        updateBatteryStatus();
        updateView();
    }

    /**
     * Aktualizuje obecny stan baterii
     */
    private void updateBatteryStatus() {
        mBatteryStatus = new BatteryDataRepository(App.getContext()).getStatus();
    }

    /**
     * Aktualizuje dane interfejsu użytkownika
     */
    @Override
    public void updateView() {
        // Obecny stan naładowania baterii w procentach
        mView.setBatteryChargeIndicator(mBatteryStatus.getChargePercentage());

        // Obecna temperatura baterii w wybranej jednostce
        if (true) // TODO check preference (cache it to not check every time)
            mView.setBatteryTemperatureInCelsius(mBatteryStatus.getTemperatureInCelsius());
        else
            mView.setBatteryTemperatureInFahrenheit(mBatteryStatus.getTemperatureInFahrenheit());

        // Napięcie na baterii
        mView.setBatteryVoltage(mBatteryStatus.getVoltage());

        // Natężenie prądu
        if (mBatteryStatus.getCurrentAverage() == 0)
            mView.setBatteryCurrent(mBatteryStatus.getCurrent());
        else
            mView.setBatteryCurrent(mBatteryStatus.getCurrent(), mBatteryStatus.getCurrentAverage());

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
        mView.setBatteryHealth(healthStringId);

        // Licznik ładowań
        mView.setBatteryChargingCounter(0);
    }

    /**
     * Aktywuje automatyczne odświeżanie interfejsu użytkownika
     */
    @Override
    public void startUpdates() {
        mUpdateData = true;

        // TODO Zastanów się czy nie zmienić koncepcji, bo wydaje mi się że obecny stop może powodować problemy z odpalaniem podwojnym
        final Handler handler = new Handler();
        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                if (mUpdateData) {
                    updateBatteryStatus();
                    updateView();
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
