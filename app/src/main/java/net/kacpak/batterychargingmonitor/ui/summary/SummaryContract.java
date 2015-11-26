package net.kacpak.batterychargingmonitor.ui.summary;

import android.support.annotation.IdRes;
import android.support.annotation.IntRange;

public interface SummaryContract {
    interface View {
        void setBatteryChargeIndicator(@IntRange(from=0,to=100) int percentage);
        void setBatteryHealth(@IdRes int healthId);
        void setBatteryVoltage(int voltage);
        void setBatteryAmperage(int amperage);
        void setBatteryTemperature(double temperature);
        void setBatteryChargingCounter(int counter);
    }

    interface UserActionsListener {
        void updateAllData();
        void startUpdates();
        void stopUpdates();
    }
}
