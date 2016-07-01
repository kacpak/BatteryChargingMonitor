package net.kacpak.batterychargingmonitor.data.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.BatteryStatus;
import net.kacpak.batterychargingmonitor.data.UserPreferences;
import net.kacpak.batterychargingmonitor.data.database.tables.Charge;
import net.kacpak.batterychargingmonitor.data.database.tables.Charge_Table;
import net.kacpak.batterychargingmonitor.data.events.DatabaseUpdateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class HistoryRepository {

    private final Context mContext;

    public HistoryRepository(Context context) {
        mContext = context;
    }

    /**
     * @return Current battery state
     */
    public BatteryStatus getStatus() {
        return new BatteryStatus();
    }

    /**
     * Add new charge entry to history
     * @param startDate     charging start date
     * @param chargerType   charger type as specified in {@link Charge#chargerType}
     * @param percentage    starting battery percentage
     * @param temperature   starting battery temperature in Celsius {@link Charge#startTemperature}
     * @param voltage       starting battery voltage in mV {@link Charge#startVoltage}
     */
    public void startCharge(Date startDate, int chargerType, int percentage, float temperature, int voltage) {
        Charge newCharge = new Charge();
        newCharge.start = startDate;
        newCharge.chargerType = chargerType;
        newCharge.startPercentage = percentage;
        newCharge.startTemperature = temperature;
        newCharge.startVoltage = voltage;
        newCharge.save();

        EventBus.getDefault().post(new DatabaseUpdateEvent());
    }

    /**
     * End all charges
     * @param stopDate      charging finish date
     * @param percentage    finish battery percentage
     * @param temperature   finish battery temperature in Celsius {@link Charge#stopTemperature}
     * @param voltage       finish battery temperature in mV {@link Charge#stopVoltage}
     */
    public void finishCharge(Date stopDate, int percentage, float temperature, int voltage) {
        List<Charge> chargeList = new Select()
                .from(Charge.class)
                .where(Charge_Table.chargeFinished.is(false))
                .queryList();

        for (Charge charge : chargeList) {
            charge.chargeFinished = true;
            charge.stop = stopDate;
            charge.stopPercentage = percentage;
            charge.stopTemperature = temperature;
            charge.stopVoltage = voltage;
            charge.save();
        }

        EventBus.getDefault().post(new DatabaseUpdateEvent());
    }

    /**
     * Deletes charges from database
     * @param ids charge ids
     * @return number of affected entries
     */
    public long deleteCharges(long... ids) {
        long affectedRows = SQLite.delete(Charge.class)
                .where(Charge_Table.id.in(ids[0], ids))
                .count();

        EventBus.getDefault().post(new DatabaseUpdateEvent());

        return affectedRows;
    }

    /**
     * Deletes all charges shorter than those specified in SharedPreferences (pref_key_irrelevant_duration)
     * @return number of affected entries
     */
    public long deleteIrrelevantCharges() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int minDuration = 0;
        try {
            String irrelevantDurationPreference = prefs.getString(mContext.getString(R.string.pref_key_irrelevant_duration), "0");
            minDuration = Integer.parseInt(irrelevantDurationPreference) * 1000;
        } catch (Exception e) {
            // Do nothing
        }

        long affectedRows = SQLite.delete(Charge.class)
                .where(Charge_Table.stop.minus(Charge_Table.start).lessThanOrEq(new Date(minDuration)))
                .and(Charge_Table.chargeFinished.is(true))
                .count();

        EventBus.getDefault().post(new DatabaseUpdateEvent());

        return affectedRows;
    }

    /**
     * Merges charges into one
     * @param ids charge ids
     * @return number of affected entries
     */
    public long mergeCharges(long... ids) {
        List<Charge> charges = SQLite.select()
                .from(Charge.class)
                .where(Charge_Table.id.in(ids[0], ids))
                .orderBy(Charge_Table.id, true)
                .queryList();

        long affectedRows = SQLite.delete(Charge.class)
                .where(Charge_Table.id.in(ids[0], ids))
                .count();

        Charge firstCharge = charges.get(0);
        Charge lastCharge = charges.get(charges.size() - 1);

        int chargerType = firstCharge.chargerType;
        for (Charge charge : charges) {
            if (charge.chargerType != chargerType) {
                chargerType = 0;
                break;
            }
        }

        firstCharge.stop = lastCharge.stop;
        firstCharge.stopPercentage = lastCharge.stopPercentage;
        firstCharge.stopTemperature = lastCharge.stopTemperature;
        firstCharge.stopVoltage = lastCharge.stopVoltage;
        firstCharge.chargeFinished = lastCharge.chargeFinished;
        firstCharge.chargerType = chargerType;
        firstCharge.save();

        EventBus.getDefault().post(new DatabaseUpdateEvent());

        return affectedRows;
    }

    /**
     * @return all charges
     */
    public List<Charge> getCharges() {
        return new Select()
                .from(Charge.class)
                .orderBy(Charge_Table.start, false)
                .queryList();
    }

    /**
     * @param id charge id
     * @return charge information with specified id
     */
    public Charge getChargeInformation(long id) {
        return new Select()
                .from(Charge.class)
                .where(Charge_Table.id.is(id))
                .querySingle();
    }

    /**
     * @return number of charges accounting those defined by user
     */
    public long getChargesCount() {
        return getChargesCount(true);
    }

    /**
     * @param withPreferences true for accounting previous charges defined by user
     * @return number of charges
     */
    public long getChargesCount(boolean withPreferences) {
        long charges = SQLite.selectCountOf()
                .from(Charge.class)
                .count();

        // Jeśli nie uwzględniamy preferencji, zwróć wartość
        if (!withPreferences)
            return charges;

        // Jeśli je uwzględniamy dodaj je do wyniku
        return charges + new UserPreferences(mContext).getPreviousChargesCount();
    }

}
