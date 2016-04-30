package net.kacpak.batterychargingmonitor.ui.historydetail;

import android.content.Context;

import net.kacpak.batterychargingmonitor.data.BatteryDataRepository;
import net.kacpak.batterychargingmonitor.data.database.ChargeInformation;

import java.lang.ref.WeakReference;

public class HistoryDetailPresenter implements HistoryDetailContract.UserActionsListener {

    private WeakReference<HistoryDetailContract.View> mHistoryDetailView;

    private ChargeInformation mChargeInformation;

    public HistoryDetailPresenter(HistoryDetailContract.View historyDetailView, Context context, long chargeId) {
        mHistoryDetailView = new WeakReference<>(historyDetailView);
        mChargeInformation = new BatteryDataRepository(context).getChargeInformation(chargeId);
    }

    @Override
    public void updateDetails() {
        HistoryDetailContract.View view = mHistoryDetailView.get();

        if (null == view)
            return;

        // Initial battery charge
        view.setChargeBump(mChargeInformation.getStartPercentage());

        // Charger Type
        view.setChargerType(mChargeInformation.getType());

        // Duration
        long seconds = mChargeInformation.getDuration() / 1000 % 60;
        long minutes = mChargeInformation.getDuration() / (60 * 1000) % 60;
        long hours = mChargeInformation.getDuration() / (60 * 60 * 1000);
        view.setChargingDuration(hours, minutes, seconds);

        // Starting Date
        view.setChargingStartDate(mChargeInformation.getStartDate());

        // Starting Temperature
        view.setStartingTemperature(mChargeInformation.getStartTemperature());

        // Starting Voltage
        view.setStartingVoltage(mChargeInformation.getStartVoltage());

        // If charging finished
        if (!mChargeInformation.isFinished())
            return;

        // Charge Bump
        view.setChargeBump(mChargeInformation.getStartPercentage(), mChargeInformation.getStopPercentage());

        // Finished Temperature
        view.setFinishedTemperature(mChargeInformation.getStopTemperature());

        // Finished Voltage
        view.setFinishedVoltage(mChargeInformation.getStopVoltage());
    }
}
