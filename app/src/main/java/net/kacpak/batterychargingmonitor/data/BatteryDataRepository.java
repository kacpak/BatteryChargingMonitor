package net.kacpak.batterychargingmonitor.data;

public class BatteryDataRepository {

    static int p = 0;
    public BatteryStatus getStatus() {
        return new BatteryStatus(++p);
    }
}
