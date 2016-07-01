package net.kacpak.batterychargingmonitor.data.database.tables;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import net.kacpak.batterychargingmonitor.data.database.HistoryDatabase;

import java.util.Date;

@Table(database = HistoryDatabase.class)
public class Charge extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public long id;

    /**
     * true if charge has finished
     */
    @Column
    public boolean chargeFinished = false;

    /**
     * 0: Mixed charge
     * 1: BATTERY_PLUGGED_AC,
     * 2: BATTERY_PLUGGED_USB,
     * 3: BATTERY_PLUGGED_WIRELESS
     */
    @Column
    public int chargerType;

    /**
     * Date on start
     */
    @Column
    public Date start;

    /**
     * Date on finish
     */
    @Column
    public Date stop;

    /**
     * Battery percentage on start
     */
    @Column
    public int startPercentage;

    /**
     * Battery percentage on finish
     */
    @Column
    public int stopPercentage;

    /**
     * Temperature on start in Celsius
     */
    @Column
    public double startTemperature;

    /**
     * Temperature on finish in Celsius
     */
    @Column
    public double stopTemperature;

    /**
     * Voltage on start in mV
     */
    @Column
    public int startVoltage;

    /**
     * Voltage on finish in mV
     */
    @Column
    public int stopVoltage;

    /**
     * @return Charge duration in miliseconds
     */
    public long getDuration() {
        Date finish = stop == null ? new Date() : stop;
        return finish.getTime() - start.getTime();
    }

}
