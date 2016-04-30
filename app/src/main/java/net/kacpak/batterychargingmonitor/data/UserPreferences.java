package net.kacpak.batterychargingmonitor.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.kacpak.batterychargingmonitor.R;

public class UserPreferences {

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public UserPreferences(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Czy temperatura baterii powinna być wyświetlana w stopniach Celsjusza
     */
    public boolean isTemperatureInCelsius() {
        try {
            return mPreferences.getBoolean(
                    mContext.getString(R.string.pref_key_temperature_celsius),
                    Boolean.parseBoolean(mContext.getString(R.string.pref_key_temperature_celsius_default))
            );

        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Ilość podanych ładowań baterii przed instalacją aplikacji
     */
    public int getPreviousChargesCount() {
        try {
            return Integer.parseInt(
                    mPreferences.getString(
                            mContext.getString(R.string.pref_key_add_to_count),
                            mContext.getString(R.string.pref_key_add_to_count_default)
            ));

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Zwraca minimalny czas w sekundach, po którym wpisy nie będą automatycznie usuwane
     */
    public int getIrrelevantChargeDuration() {
        try {
            return Integer.parseInt(mPreferences.getString(
                    mContext.getString(R.string.pref_key_irrelevant_duration),
                    mContext.getString(R.string.pref_key_irrelevant_duration_default)
            ));

        } catch (Exception e) {
            return 0;
        }
    }
}
