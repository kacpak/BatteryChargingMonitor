package net.kacpak.batterychargingmonitor.data;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.util.Log;

import net.kacpak.batterychargingmonitor.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BatteryStatus {

    private Intent mBatteryStatus;

    private int mCurrent, mCurrentAvg;

    public BatteryStatus(Intent batteryStatusIntent) {
        mBatteryStatus = batteryStatusIntent;
        init();
    }

    public BatteryStatus() {
        IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryStatus = App.getContext().registerReceiver(null, mFilter);
        init();
    }

    private void init() {
        mCurrent = readCurrent();
        mCurrentAvg = readCurrentAverage();
    }

    /**
     * Procent naładowania baterii
     */
    public int getChargePercentage() {
        int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (int)(100 * level / (float)scale);
    }

    /**
     * Zwraca true jeśli telefon jest w trakcie ładowania
     */
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
     * Temperatura baterii w stopniach Celsjusza
     */
    public float getTemperatureInCelsius() {
        return mBatteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / (float)10;
    }

    /**
     * Temperatura baterii w stopniach Fahrenheit'a
     */
    public float getTemperatureInFahrenheit() {
        return getTemperatureInCelsius() * 9 / 5 + 32;
    }

    /**
     * Napięcie baterii w mV
     */
    public int getVoltage() {
        return mBatteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    }

    /**
     * Natężenie prądu w mA
     */
    public int getCurrent() {
        return mCurrent;
    }

    /**
     * Średnie natężenie prądu w mA
     */
    public int getCurrentAverage() {
        return mCurrentAvg;
    }

    /**
     * Odczytuje natężenie prądu w mA
     */
    private int readCurrent() {
        return readBatteryStatus("current_now");
    }

    /**
     * Odczytuje średnie natężenie prądu w mA
     */
    private int readCurrentAverage() {
        return readBatteryStatus("current_avg");
    }

    /**
     * Zwraca wartość z danego pliku lub 0
     * @param filename
     */
    private int readBatteryStatus(@NonNull String filename) {
        File file = new File("/sys/class/power_supply/battery/" + filename);

        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                reader.close();

                return Integer.parseInt(line) / 1000;

            } catch (Exception e) {
                Log.e("Read file: " + filename, e.toString());

            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    Log.e("Close file: " + filename, e.toString());
                }
            }
        }

        return 0;
    }
}
