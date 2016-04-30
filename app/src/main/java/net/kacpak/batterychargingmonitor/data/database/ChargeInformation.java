package net.kacpak.batterychargingmonitor.data.database;

import android.database.Cursor;

import java.util.Date;

import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;

public class ChargeInformation {

    private int mType;
    private boolean mChargeFinished;
    private long mStart;
    private float mStartTemperature;
    private int mStartPercentage;
    private int mStartVoltage;
    private long mStop;
    private float mStopTemperature;
    private int mStopPercentage;
    private int mStopVoltage;
    private String mNote;

    public ChargeInformation(Cursor cursor) {
        String[] columns = cursor.getColumnNames();

        for (int i = 0; i < columns.length; i++)
            switch (columns[i]) {
                case DataEntry.COLUMN_TYPE:
                    mType = cursor.getInt(i);
                    break;
                case DataEntry.COLUMN_START:
                    mStart = cursor.getLong(i);
                    break;
                case DataEntry.COLUMN_START_TEMPERATURE_C:
                    mStartTemperature = cursor.getFloat(i);
                    break;
                case DataEntry.COLUMN_START_PERCENTAGE:
                    mStartPercentage = cursor.getInt(i);
                    break;
                case DataEntry.COLUMN_START_VOLTAGE:
                    mStartVoltage = cursor.getInt(i);
                    break;
                case DataEntry.COLUMN_STOP:
                    mStop = cursor.getLong(i);
                    break;
                case DataEntry.COLUMN_STOP_TEMPERATURE_C:
                    mStopTemperature = cursor.getFloat(i);
                    break;
                case DataEntry.COLUMN_STOP_PERCENTAGE:
                    mStopPercentage = cursor.getInt(i);
                    break;
                case DataEntry.COLUMN_STOP_VOLTAGE:
                    mStopVoltage = cursor.getInt(i);
                    break;
                case DataEntry.COLUMN_NOTE:
                    mNote = cursor.getString(i);
                    break;
                case DataEntry.COLUMN_CHARGE_FINISHED:
                    mChargeFinished = cursor.getInt(i) != 0;
                    break;
            }
    }

    public boolean isFinished() {
        return mChargeFinished;
    }

    public int getType() {
        return mType;
    }

    public String getNote() {
        return mNote;
    }

    public Date getStartDate() {
        return new Date(mStart);
    }

    public float getStartTemperature() {
        return mStartTemperature;
    }

    public int getStartPercentage() {
        return mStartPercentage;
    }

    public int getStartVoltage() {
        return mStartVoltage;
    }

    public Date getStopDate() {
        return new Date(mStop);
    }

    public float getStopTemperature() {
        return mStopTemperature;
    }

    public int getStopPercentage() {
        return mStopPercentage;
    }

    public int getStopVoltage() {
        return mStopVoltage;
    }

    public long getDuration() {
        if (mStop != 0)
            return mStop - mStart;
        return new Date().getTime() - new Date(mStart).getTime();
    }

}
