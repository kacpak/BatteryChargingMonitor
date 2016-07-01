package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Context;
import android.os.BatteryManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.database.tables.Charge;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends ArrayAdapter<Charge> {

    HistoryAdapter(Context context, List<Charge> charges) {
        super(context, 0, charges);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Charge charge = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_history, parent, false);
            convertView.setTag(new HistoryAdapterViewHolder(convertView));
        }

        String type;
        switch (charge.chargerType) {
            case 0:
                type = getContext().getString(R.string.power_state_mixed);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                type = getContext().getString(R.string.power_state_ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                type = getContext().getString(R.string.power_state_usb);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                type = getContext().getString(R.string.power_state_wireless);
                break;
            default:
                type = "?";
        }

        long seconds = charge.getDuration() / 1000 % 60;
        long minutes = charge.getDuration() / (60 * 1000) % 60;
        long hours = charge.getDuration() / (60 * 60 * 1000);
        String duration = String.format(getContext().getString(R.string.history_list_item_duration), hours, minutes, seconds);

        String percentageIncrease = charge.chargeFinished
                ? String.format(getContext().getString(R.string.history_list_item_percentage_increase), charge.startPercentage, charge.stopPercentage)
                : String.format(getContext().getString(R.string.history_list_item_percentage_while_charging), charge.startPercentage);

        HistoryAdapterViewHolder holder = (HistoryAdapterViewHolder) convertView.getTag();
        holder.type.setText(type);
        holder.duration.setText(duration);
        holder.percentage_increase.setText(percentageIncrease);

        return convertView;
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
