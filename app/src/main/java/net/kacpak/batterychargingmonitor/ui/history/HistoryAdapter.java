package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Context;
import android.database.Cursor;
import android.os.BatteryManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.database.ChargeInformation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends CursorAdapter {

    HistoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history, parent, false);
        view.setTag(new HistoryAdapterViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ChargeInformation data = new ChargeInformation(cursor);

        String type;
        switch (data.getType()) {
            case 0:
                type = context.getString(R.string.power_state_mixed);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                type = context.getString(R.string.power_state_ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                type = context.getString(R.string.power_state_usb);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                type = context.getString(R.string.power_state_wireless);
                break;
            default:
                type = "?";
        }

        long seconds = data.getDuration() / 1000 % 60;
        long minutes = data.getDuration() / (60 * 1000) % 60;
        long hours = data.getDuration() / (60 * 60 * 1000);
        String duration = String.format(context.getString(R.string.history_list_item_duration), hours, minutes, seconds);

        String percentageIncrease = data.isFinished()
                ? String.format(context.getString(R.string.history_list_item_percentage_increase), data.getStartPercentage(), data.getStopPercentage())
                : String.format(context.getString(R.string.history_list_item_percentage_while_charging), data.getStartPercentage());

        HistoryAdapterViewHolder holder = (HistoryAdapterViewHolder) view.getTag();
        holder.type.setText(type);
        holder.duration.setText(duration);
        holder.percentage_increase.setText(percentageIncrease);
    }

    /**
     * ViewHolder dla wiersza w historii
     */
    public class HistoryAdapterViewHolder {
        @BindView(R.id.type)
        public TextView type;

        @BindView(R.id.duration)
        public TextView duration;

        @BindView(R.id.percentage_increase)
        public TextView percentage_increase;

        public HistoryAdapterViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
