package net.kacpak.batterychargingmonitor.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;

import java.util.Arrays;
import java.util.Date;

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
    public int delete(int... ids) {
        String inClause = Arrays.asList(ids).toString().replace("[", "(").replace("]", ")");

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
        // TODO deleteIrrelevant
        return 0;
    }

    /**
     * Łączy podane wpisy w jeden
     * @param ids ID wpisów w bazie danych
     */
    public int merge(int... ids) {
        // TODO merge
        return 0;
    }
}
