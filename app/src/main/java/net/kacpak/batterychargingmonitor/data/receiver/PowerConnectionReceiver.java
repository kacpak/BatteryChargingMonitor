package net.kacpak.batterychargingmonitor.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.BatteryStatus;
import net.kacpak.batterychargingmonitor.data.database.HistoryRepository;

import java.util.Date;

public class PowerConnectionReceiver extends BroadcastReceiver {

    private Context mContext;
    private BatteryStatus mBatteryStatus;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mBatteryStatus = new BatteryStatus();
        showBatteryStatus();

        if (mBatteryStatus.isCharging())
            startNewCharging();
        else
            finishCharging();
    }

    public void showBatteryStatus() {
        StringBuilder data = new StringBuilder();

        data.append(String.format(mContext.getString(R.string.battery_percent), mBatteryStatus.getChargePercentage()));
        data.append(" ");

        if (mBatteryStatus.isCharging()) {
            if (mBatteryStatus.isPluggedAC()) data.append(mContext.getString(R.string.battery_ac_charging));
            if (mBatteryStatus.isPluggedUSB()) data.append(mContext.getString(R.string.battery_usb_charging));
            if (mBatteryStatus.isPluggedWireless()) data.append(mContext.getString(R.string.battery_usb_charging));
        } else {
            data.append(mContext.getString(R.string.battery_disconnected));
        }

        data.append("\n");
        data.append(String.format(mContext.getString(R.string.battery_temperature_celsius), mBatteryStatus.getTemperatureInCelsius()));

        data.append(" ");
        data.append(String.format(mContext.getString(R.string.battery_voltage), mBatteryStatus.getVoltage()));

        Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
    }

    public void startNewCharging() {
        try {
            new HistoryRepository(mContext).startCharge(
                    new Date(),
                    mBatteryStatus.getPluggedInformation(),
                    mBatteryStatus.getChargePercentage(),
                    mBatteryStatus.getTemperatureInCelsius(),
                    mBatteryStatus.getVoltage()
            );

        } catch (Exception e) {
            Log.e("startNewCharging", e.toString());
            Toast.makeText(mContext, R.string.error_failed_to_start_charging, Toast.LENGTH_SHORT).show();
        }
    }

    public void finishCharging() {
        try {
            new HistoryRepository(mContext).finishCharge(
                    new Date(),
                    mBatteryStatus.getChargePercentage(),
                    mBatteryStatus.getTemperatureInCelsius(),
                    mBatteryStatus.getVoltage()
            );

        } catch (Exception e) {
            Log.e("finishCharging", e.toString());
            Toast.makeText(mContext, R.string.error_failed_complete_charging, Toast.LENGTH_SHORT).show();
        }
    }
}
