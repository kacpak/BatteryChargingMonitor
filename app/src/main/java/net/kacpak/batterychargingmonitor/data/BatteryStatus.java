package net.kacpak.batterychargingmonitor.data;

public class BatteryStatus {
    private final int percentage;

    public BatteryStatus(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }
}
