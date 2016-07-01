package net.kacpak.batterychargingmonitor.ui.historydetail;

import android.content.Context;

import net.kacpak.batterychargingmonitor.data.database.HistoryRepository;
import net.kacpak.batterychargingmonitor.data.database.tables.Charge;

import java.lang.ref.WeakReference;

public class HistoryDetailPresenter implements HistoryDetailContract.UserActionsListener {

    private WeakReference<HistoryDetailContract.View> mHistoryDetailView;

    private Charge mChargeInformation;

    public HistoryDetailPresenter(HistoryDetailContract.View historyDetailView, Context context, long chargeId) {
        mHistoryDetailView = new WeakReference<>(historyDetailView);
        mChargeInformation = new HistoryRepository(context).getChargeInformation(chargeId);
    }

    @Override
    public void updateDetails() {
        HistoryDetailContract.View view = mHistoryDetailView.get();

        if (null == view)
            return;

        // Initial battery charge
        view.setChargeBump(mChargeInformation.startPercentage);

        // Charger Type
        view.setChargerType(mChargeInformation.chargerType);

        // Duration
        long seconds = mChargeInformation.getDuration() / 1000 % 60;
        long minutes = mChargeInformation.getDuration() / (60 * 1000) % 60;
        long hours = mChargeInformation.getDuration() / (60 * 60 * 1000);
        view.setChargingDuration(hours, minutes, seconds);

        // Starting Date
        view.setChargingStartDate(mChargeInformation.start);

        // Starting Temperature
        view.setStartingTemperature(mChargeInformation.startTemperature);

        // Starting Voltage
        view.setStartingVoltage(mChargeInformation.startVoltage);

        // If charging finished
        if (!mChargeInformation.chargeFinished)
            return;

        // Charge Bump
        view.setChargeBump(mChargeInformation.startPercentage, mChargeInformation.stopPercentage);

        // Finished Temperature
        view.setFinishedTemperature(mChargeInformation.stopTemperature);

        // Finished Voltage
        view.setFinishedVoltage(mChargeInformation.stopVoltage);
    }
}
