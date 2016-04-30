package net.kacpak.batterychargingmonitor.ui.summary;

import android.support.annotation.IntRange;
import android.support.annotation.StringRes;

public interface SummaryContract {
    interface View {
        void setBatteryChargeIndicator(@IntRange(from=0,to=100) int percentage);
        void setBatteryHealth(@StringRes int healthId);
        void setBatteryVoltage(int voltage);
        void setBatteryCurrent(int current);
        void setBatteryCurrent(int current, int currentAvg);
        void setBatteryTemperatureInCelsius(double temperature);
        void setBatteryTemperatureInFahrenheit(double temperature);
        void setBatteryChargingCounter(int counter);
        void hideBatteryCurrentData();
    }

    interface UserActionsListener {
        void updateView();
        void startUpdates();
        void stopUpdates();
    }
}
