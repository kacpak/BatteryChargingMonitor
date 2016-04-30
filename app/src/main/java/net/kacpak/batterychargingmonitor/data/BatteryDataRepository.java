package net.kacpak.batterychargingmonitor.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.database.ChargeInformation;
import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Zarządzanie danymi o baterii i historii ładowania
 */
public class BatteryDataRepository {

    private final Context mContext;

    /**
     * Tworzy nowy obiekt do zarządzania danymi o baterii i historii ładowania
     * @param context {@see Context} aplikacji
     */
    public BatteryDataRepository(Context context) {
        mContext = context;
    }

    /**
     * Obecny stan baterii
     */
    public BatteryStatus getStatus() {
        return new BatteryStatus();
    }

    /**
     * Zwraca stan ładowania baterii
     * @param chargeId id z historii ładowania baterii
     * @return stan ładowania baterii
     */
    public ChargeInformation getChargeInformation(long chargeId) {
        Uri dataUri = DataEntry.buildUri(chargeId);
        Cursor entryInfo = mContext.getContentResolver().query(
                dataUri,
                null,
                null,
                null,
                null
        );

        if (null == entryInfo)
            return null;

        entryInfo.moveToFirst();
        return new ChargeInformation(entryInfo);
    }

    /**
     * Dodaje nowy wpis do historii ładowania
     * @param start Data i godzina rozpoczęcia ładowania
     * @param type Typ ładowarki
     * @param percentage Początkowy procent naładowania baterii
     * @param temperatureCelsius Temperatura w Celsjuszach
     * @param voltage Początkowe napięcie na baterii
     */
    public Uri add(Date start, int type, int percentage, float temperatureCelsius, int voltage) {
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_TYPE, type);
        values.put(DataEntry.COLUMN_START, start.getTime());
        values.put(DataEntry.COLUMN_START_PERCENTAGE, percentage);
        values.put(DataEntry.COLUMN_START_TEMPERATURE_C, temperatureCelsius);
        values.put(DataEntry.COLUMN_START_VOLTAGE, voltage);

        return mContext.getContentResolver().insert(
                DataEntry.CONTENT_URI,
                values
        );
    }

    /**
     * Zakańcza ładowanie baterii
     * @param stop Data i godzina zakończenia ładowania
     * @param percentage Końcowy procent naładowania baterii
     * @param temperatureCelsius Temperatura w Celsjuszach
     * @param voltage Ostateczne napięcie na baterii
     * @param note Notatka do ładowania
     */
    public int finishCharging(Date stop, int percentage, float temperatureCelsius, int voltage, String note) {
        ContentValues values = new ContentValues();
        values.put(DataEntry.COLUMN_CHARGE_FINISHED, 1);
        values.put(DataEntry.COLUMN_STOP, stop.getTime());
        values.put(DataEntry.COLUMN_STOP_PERCENTAGE, percentage);
        values.put(DataEntry.COLUMN_STOP_TEMPERATURE_C, temperatureCelsius);
        values.put(DataEntry.COLUMN_STOP_VOLTAGE, voltage);
        values.put(DataEntry.COLUMN_NOTE, note);

        return mContext.getContentResolver().update(
                DataEntry.CONTENT_URI_UNFINISHED,
                values,
                null,
                null
        );
    }

    /**
     * Usuwa z historii podane wpisy
     * @param ids ID wpisów w bazie danych
     */
    public int delete(List<Long> ids) {
        String inClause = ids.toString().replace("[", "(").replace("]", ")");

        return mContext.getContentResolver().delete(
                DataEntry.CONTENT_URI,
                DataEntry._ID + " IN " + inClause,
                null
        );
    }

    /**
     * Usuwa nic nie wnoszące wpisy z historii (krótsze niż podane w preferencjach)
     */
    public int deleteIrrelevant() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int minDuration = 0;
        try {
            String irrelevantDurationPreference = prefs.getString(mContext.getString(R.string.pref_key_irrelevant_duration), "0");
            minDuration = Integer.parseInt(irrelevantDurationPreference) * 1000;
        } catch (Exception e) {
            // Do nothing
        }

        return mContext.getContentResolver().delete(
                DataEntry.CONTENT_URI,
                "CAST(ABS(" + DataEntry.COLUMN_STOP + " - " + DataEntry.COLUMN_START + ") AS INTEGER) <= " + minDuration,
                null
        );
    }

    /**
     * Łączy podane wpisy w jeden
     * @param ids ID wpisów w bazie danych
     */
    public int merge(List<Long> ids) {
        // Będziemy szukać tylko wpisów z id "(a, b, ...)"
        String inClause = ids.toString().replace("[", "(").replace("]", ")");

        Cursor entries = mContext.getContentResolver().query(
                DataEntry.CONTENT_URI,
                null,
                DataEntry._ID + " IN " + inClause,
                null,
                DataEntry.COLUMN_START + " ASC"
        );

        // Ustalam typ wpisu dla łączenia
        entries.moveToFirst();
        final int typeColumnIndex = entries.getColumnIndex(DataEntry.COLUMN_TYPE);
        int type = entries.getInt(typeColumnIndex);
        while (entries.moveToNext()) {
            if (entries.getInt(typeColumnIndex) != type) {
                type = 0;
                break;
            }
        }

        // Wartości do wpisania
        ContentValues values = new ContentValues();

        // Sczytujemy część początkową
        entries.moveToFirst();
        values.put(DataEntry.COLUMN_START, entries.getLong(entries.getColumnIndex(DataEntry.COLUMN_START)));
        values.put(DataEntry.COLUMN_START_PERCENTAGE, entries.getInt(entries.getColumnIndex(DataEntry.COLUMN_START_PERCENTAGE)));
        values.put(DataEntry.COLUMN_START_TEMPERATURE_C, entries.getFloat(entries.getColumnIndex(DataEntry.COLUMN_START_TEMPERATURE_C)));
        values.put(DataEntry.COLUMN_START_VOLTAGE, entries.getInt(entries.getColumnIndex(DataEntry.COLUMN_START_VOLTAGE)));
        values.put(DataEntry.COLUMN_TYPE, type);

        // Sczytujemy wartość końcową
        entries.moveToLast();
        values.put(DataEntry.COLUMN_STOP, entries.getLong(entries.getColumnIndex(DataEntry.COLUMN_STOP)));
        values.put(DataEntry.COLUMN_STOP_PERCENTAGE, entries.getInt(entries.getColumnIndex(DataEntry.COLUMN_STOP_PERCENTAGE)));
        values.put(DataEntry.COLUMN_STOP_TEMPERATURE_C, entries.getFloat(entries.getColumnIndex(DataEntry.COLUMN_STOP_TEMPERATURE_C)));
        values.put(DataEntry.COLUMN_STOP_VOLTAGE, entries.getInt(entries.getColumnIndex(DataEntry.COLUMN_STOP_VOLTAGE)));

        if (entries.getInt(entries.getColumnIndex(DataEntry.COLUMN_CHARGE_FINISHED)) == 1)
            values.put(DataEntry.COLUMN_CHARGE_FINISHED, 1);

        entries.close();


        mContext.getContentResolver().insert(
                DataEntry.CONTENT_URI,
                values
        );

        return delete(ids);
    }

    /**
     * Ilość ładowań z uwzględnieniem preferencji
     */
    public int getChargedCount() {
        return getChargedCount(true);
    }

    /**
     * Ilość ładowań
     * @param withPreferences true dla uwzględnienia danych od użytkownika
     */
    public int getChargedCount(boolean withPreferences) {
        Cursor countCursor = mContext.getContentResolver().query(
                DataEntry.CONTENT_URI,
                new String[] {"count(*) AS count"},
                null,
                null,
                null
        );

        int count = countCursor.moveToFirst() ? countCursor.getInt(0) : 0;
        countCursor.close();

        // Jeśli nie uwzględniamy preferencji, zwróć wartość
        if (!withPreferences)
            return count;

        // Jeśli je uwzględniamy dodaj je do wyniku
        return count + new UserPreferences(mContext).getPreviousChargesCount();
    }
}
