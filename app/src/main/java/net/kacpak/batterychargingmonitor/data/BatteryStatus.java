package net.kacpak.batterychargingmonitor.data;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import net.kacpak.batterychargingmonitor.App;

public class BatteryStatus {

    private Intent mBatteryStatus;

    public BatteryStatus(Intent batteryStatusIntent) {
        mBatteryStatus = batteryStatusIntent;
    }

    public BatteryStatus() {
        IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryStatus = App.getContext().registerReceiver(null, mFilter);
    }

    public int getChargePercentage() {
        int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (int)(100 * level / (float)scale);
    }

    public boolean isCharging() {
        int status = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }
    /**
     * Metoda zwracająca liczbę odpowiadającą stałej BATTERY_PLUGGED_* w BatteryManager.java
     * Standardowo:
     *  1 : BATTERY_PLUGGED_AC
     *  2 : BATTERY_PLUGGED_USB
     *  4 : BATTERY_PLUGGED_WIRELESS
     *
     * @return Stała BATTERY_PLUGGED
     */
    public int getPluggedInformation() {
        return mBatteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    }

    public boolean isPluggedUSB() {
        return getPluggedInformation() == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public boolean isPluggedAC() {
        return getPluggedInformation() == BatteryManager.BATTERY_PLUGGED_AC;
    }

    public boolean isPluggedWireless() {
        return getPluggedInformation() == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    /**
     * Metoda zwracająca liczbę odpowiadającą stałej BATTER_HEALTH_* w BatteryManager.java
     * Standardowo:
     * 1 : UNKNOWN
     * 2 : GOOD
     * 3 : OVERHEAT
     * 4 : DEAD
     * 5 : OVER_VOLTAGE
     * 6 : UNSPECIFIED_FAILURE
     * 7 : COLD
     *
     * @return Stała BATTERY_HEALTH
     */
    public int getHealthInformation() {
        return mBatteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
    }

    public boolean isHealthUnknown() {
        return getHealthInformation() == 1;
    }

    public boolean isHealthGood() {
        return getHealthInformation() == 2;
    }

    public boolean isHealthOverheat() {
        return getHealthInformation() == 3;
    }

    public boolean isHealthDead() {
        return getHealthInformation() == 4;
    }

    public boolean isHealthOverVoltage() {
        return getHealthInformation() == 5;
    }

    public boolean isHealthUnspecifiedFailure() {
        return getHealthInformation() == 6;
    }

    public boolean isHealthCold() {
        return getHealthInformation() == 7;
    }

    /**
     * Metoda podająca napięcie baterii w mV
     *
     * @return Napięcie baterii w mV
     */
    public int getVoltage() {
        return mBatteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    }

    public float getTemperatureInCelsius() {
        return mBatteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / (float)10;
    }

    public float getTemperatureInFahrenheit() {
        return getTemperatureInCelsius() * 9 / 5 + 32;
    }
}
