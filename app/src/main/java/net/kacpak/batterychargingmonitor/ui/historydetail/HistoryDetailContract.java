package net.kacpak.batterychargingmonitor.ui.historydetail;

import java.util.Date;

public interface HistoryDetailContract {

    String CHARGE_ID = "id";

    interface View {
        void setChargerType(int chargerType);
        void setChargingStartDate(Date chargingStartDate);
        void setChargingDuration(long hours, long minutes, long seconds);
        void setChargeBump(int startingPercentage);
        void setChargeBump(int startingPercentage, int finishedPercentage);
        void setStartingTemperature(float startingTemperature);
        void setFinishedTemperature(float finishedTemperature);
        void setStartingVoltage(int startingVoltage);
        void setFinishedVoltage(int finishedVoltage);
    }

    interface UserActionsListener {
        void updateDetails();
    }
}
