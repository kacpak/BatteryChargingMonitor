package net.kacpak.batterychargingmonitor.ui.history;

import android.content.Context;
import android.database.Cursor;
import android.os.BatteryManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.database.ChargeInformation;
import net.kacpak.batterychargingmonitor.data.database.DatabaseContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder> {

    final private Context mContext;

    private Cursor mCursor;
    private final HistoryAdapterOnClickHandler mClickHandler;
    private final View mEmptyView;

    HistoryAdapter(Context context, HistoryAdapterOnClickHandler onClickHandler, View emptyView) {
        mContext = context;
        mClickHandler = onClickHandler;
        mEmptyView = emptyView;
    }

    @Override
    public HistoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!(parent instanceof RecyclerView))
            throw new RuntimeException("Not bound to RecyclerViewSelection");

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_history, parent, false);
        view.setFocusable(true);
        return new HistoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        ChargeInformation data = new ChargeInformation(mCursor);

        String type;
        switch (data.getType()) {
            case 0:
                type = mContext.getString(R.string.power_state_mixed);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                type = mContext.getString(R.string.power_state_ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                type = mContext.getString(R.string.power_state_usb);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                type = mContext.getString(R.string.power_state_wireless);
                break;
            default:
                type = "?";
        }

        long seconds = data.getDuration() / 1000 % 60;
        long minutes = data.getDuration() / (60 * 1000) % 60;
        long hours = data.getDuration() / (60 * 60 * 1000);
        String duration = String.format(mContext.getString(R.string.history_list_item_duration), hours, minutes, seconds);

        String percentageIncrease = data.isFinished()
                ? String.format(mContext.getString(R.string.history_list_item_percentage_increase), data.getStartPercentage(), data.getStopPercentage())
                : String.format(mContext.getString(R.string.history_list_item_percentage_while_charging), data.getStartPercentage());

        holder.type.setText(type);
        holder.duration.setText(duration);
        holder.percentage_increase.setText(percentageIncrease);
    }

    @Override
    public int getItemCount() {
        return (null == mCursor) ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        if (null != mEmptyView)
            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public class HistoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.type)
        public TextView type;

        @BindView(R.id.duration)
        public TextView duration;

        @BindView(R.id.percentage_increase)
        public TextView percentage_increase;

        public HistoryAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            int idColumnIndex = mCursor.getColumnIndex(DatabaseContract.DataEntry._ID);
            mClickHandler.onClick(mCursor.getInt(idColumnIndex), this);
        }
    }

    public interface HistoryAdapterOnClickHandler {
        void onClick(long id, HistoryAdapterViewHolder holder);
    }
}
