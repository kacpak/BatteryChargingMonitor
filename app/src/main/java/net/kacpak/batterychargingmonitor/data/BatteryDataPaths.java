package net.kacpak.batterychargingmonitor.data;

import android.util.Log;

import java.io.File;

public class BatteryDataPaths {

    /**
     * Znane ścieżki do folderu systemowego zawierającego dane o stanie baterii
     */
    private static final String[] mBatteryDataPaths = new String[] {
            "/sys/class/power_supply/battery/"
    };

    /**
     *
     */
    private static final String[] mCurrentNowFilenames = new String[] {
            "batt_current_ua_now",
            "current_now"
    };

    /**
     *
     */
    private static final String[] mCurrentAvgFilenames = new String[] {
            "batt_current_ua_avg",
            "current_avg"
    };

    public static final String CurrentNow;

    public static final String CurrentAvg;

    private static final String mBatteryDataPath;

    static {
        mBatteryDataPath = getBatteryDataPath();
        CurrentNow = getCurrentNowPath();
        CurrentAvg = getCurrentAvgPath();
    }

    /**
     *
     * @return
     */
    private static String getBatteryDataPath() {
        File dir;
        for (String path : mBatteryDataPaths) {
            Log.d("BatteryStatus", "PATH checked: " + path);
            dir = new File(path);
            if (dir.isDirectory()) {
                return path;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    private static String getCurrentNowPath() {
        File dir;
        for (String path : mCurrentNowFilenames) {
            dir = new File(mBatteryDataPath + path);
            if (dir.isFile()) {
                return dir.getPath();
            }
        }
        return "";
    }

    /**
     *
     * @return
     */
    private static String getCurrentAvgPath() {
        File dir;
        for (String path : mCurrentAvgFilenames) {
            dir = new File(mBatteryDataPath + path);
            if (dir.isFile()) {
                return dir.getPath();
            }
        }
        return "";
    }

    private BatteryDataPaths() {
    }
}
