package net.kacpak.batterychargingmonitor.data;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;

import net.kacpak.batterychargingmonitor.App;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Aktualny status baterii
 */
public class BatteryStatus {

    /**
     * Battery Status Changed Intent
     */
    private Intent mBatteryStatus;

    /**
     * Obecne natężenie prądu
     */
    private int mCurrent;

    /**
     * Średnie natężenie prądu
     */
    private int mCurrentAvg;

    /**
     * Tworzy obiekt do odczytu stanu baterii
     */
    public BatteryStatus() {
        IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryStatus = App.getContext().registerReceiver(null, mFilter);
        if (BatteryDataPaths.CurrentAvg != null)
            mCurrentAvg = readBatteryStatus(BatteryDataPaths.CurrentAvg);
        if (BatteryDataPaths.CurrentNow != null)
            mCurrent = readBatteryStatus(BatteryDataPaths.CurrentNow);
    }

    /**
     * Zwraca zadaną wartość z obecnego stanu baterii lub {@see defaultValue}
     * @param name wybrana stała z {@see BatteryManager}
     */
    private int getBatteryStatusExtra(String name) {
        return getBatteryStatusExtra(name, -1);
    }

    /**
     * Zwraca zadaną wartość z obecnego stanu baterii lub {@see defaultValue}
     * @param name wybrana stała z {@see BatteryManager}
     * @param defaultValue wartoć domyślna
     */
    private int getBatteryStatusExtra(String name, int defaultValue) {
        return mBatteryStatus.getIntExtra(name, defaultValue);
    }

    /**
     * Procent naładowania baterii
     */
    public int getChargePercentage() {
        int level = getBatteryStatusExtra(BatteryManager.EXTRA_LEVEL);
        int scale = getBatteryStatusExtra(BatteryManager.EXTRA_SCALE);
        return (int)(100 * level / (float)scale);
    }

    /**
     * Zwraca true jeśli telefon jest w trakcie ładowania lub ukończył ładowanie
     */
    public boolean isCharging() {
        int status = getBatteryStatusExtra(BatteryManager.EXTRA_STATUS);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    /**
     * Zwraca stałą BATTERY_PLUGGED z {@see BatteryManager}
     * @return 1: BATTERY_PLUGGED_AC, 2: BATTERY_PLUGGED_USB, 3: BATTERY_PLUGGED_WIRELESS
     */
    public int getPluggedInformation() {
        return getBatteryStatusExtra(BatteryManager.EXTRA_PLUGGED);
    }

    /**
     * Czy bateria jest ładowana z portu USB
     */
    public boolean isPluggedUSB() {
        return getPluggedInformation() == BatteryManager.BATTERY_PLUGGED_USB;
    }

    /**
     * Czy bateria ładowana jest ładowarką przewodową
     */
    public boolean isPluggedAC() {
        return getPluggedInformation() == BatteryManager.BATTERY_PLUGGED_AC;
    }

    /**
     * Czy bateria ładowana jest bezprzewodowo
     */
    public boolean isPluggedWireless() {
        return getPluggedInformation() == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    /**
     * Zwraca stałą BATTERY_HEALTH z {@see BatteryManager}
     * @return 1: UNKNOWN, 2: GOOD, 3: OVERHEAT, 4: DEAD, 5: OVER_VOLTAGE, 6: UNSPECIFIED_FAILURE, 7: COLD
     */
    public int getHealthInformation() {
        return getBatteryStatusExtra(BatteryManager.EXTRA_HEALTH);
    }

    /**
     * Czy stan baterii nieokreślony
     */
    public boolean isHealthUnknown() {
        return getHealthInformation() == 1;
    }

    /**
     * Czy stan baterii dobry
     */
    public boolean isHealthGood() {
        return getHealthInformation() == 2;
    }

    /**
     * Czy bateria się przegrzewa
     */
    public boolean isHealthOverheat() {
        return getHealthInformation() == 3;
    }

    /**
     * Czy bateria zużyta
     */
    public boolean isHealthDead() {
        return getHealthInformation() == 4;
    }

    /**
     * Czy bateria ładowana zbyt wysokim prądem
     */
    public boolean isHealthOverVoltage() {
        return getHealthInformation() == 5;
    }

    /**
     * Czy bateria uległa niezidentyfikowanemu uszkodzeniu
     */
    public boolean isHealthUnspecifiedFailure() {
        return getHealthInformation() == 6;
    }

    /**
     * Czy bateria pracuje przy zbyt niskiej temperaturze
     */
    public boolean isHealthCold() {
        return getHealthInformation() == 7;
    }

    /**
     * Temperatura baterii w stopniach Celsjusza
     */
    public float getTemperatureInCelsius() {
        return getBatteryStatusExtra(BatteryManager.EXTRA_TEMPERATURE) / (float)10;
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
        return getBatteryStatusExtra(BatteryManager.EXTRA_VOLTAGE);
    }

    /**
     * Natężenie prądu w mA
     */
    public int getCurrent() {
        return mCurrent;
    }

    /**
     * Informuje czy dane o obecnym natężeniu prądu są dostępne
     */
    public boolean isCurrentAvailable() {
        return isBatteryStatusAvailable(BatteryDataPaths.CurrentNow);
    }

    /**
     * Średnie natężenie prądu w mA
     */
    public int getCurrentAverage() {
        return mCurrentAvg;
    }

    /**
     * Informuje czy dane o średnim natężeniu prądu są dostępne
     */
    public boolean isCurrentAverageAvailable() {
        return isBatteryStatusAvailable(BatteryDataPaths.CurrentAvg);
    }

    /**
     * Zwraca wartość z danego pliku lub 0
     * @param path
     */
    private int readBatteryStatus(@NonNull String path) {
        File file = new File(path);

        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                reader.close();

                return Integer.parseInt(line) / 1000;

            } catch (Exception e) {
                /* Do nothing */
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        return 0;
    }

    /**
     * Sprawdza czy dany stan baterii jest przechowywany w plikach systemowych
     * @param path nazwa pliku
     */
    private boolean isBatteryStatusAvailable(@NonNull String path) {
        File file = new File(path);
        return file.exists();
    }
}
