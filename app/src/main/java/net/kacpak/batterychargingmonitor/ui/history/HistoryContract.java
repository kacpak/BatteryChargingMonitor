package net.kacpak.batterychargingmonitor.ui.history;

import net.kacpak.batterychargingmonitor.data.database.tables.Charge;

import java.util.List;

public interface HistoryContract {
    interface View {
        void swapCharges(List<Charge> charges);
        void showDeletedCountMessage(long count);
        void showMergedCountMessage(long count);
    }

    interface UserActionsListener {
        void removeIrrelevantEntries();
        void removeEntries(long... entries);
        void mergeEntries(long... entries);
    }
}
