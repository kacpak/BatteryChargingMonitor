package net.kacpak.batterychargingmonitor.ui.historydetail;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import net.kacpak.batterychargingmonitor.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.kacpak.batterychargingmonitor.ui.historydetail.HistoryDetailContract.*;

/**
 * Detale danego wpisu z historii
 */
public class HistoryDetailDialog extends DialogFragment implements HistoryDetailContract.View {

//    public static final SimpleDateFormat sDateFormat = new SimpleDateFormat("d MMM yyyy H:mm");
    public static final DateFormat sDateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

    @BindView(R.id.duration)
    TextView mDurationTextView;

    @BindView(R.id.charger_type)
    TextView mChargerTypeTextView;

    @BindView(R.id.start_date)
    TextView mChargingStartTextView;

    @BindView(R.id.start_temp)
    TextView mTemperatureStartTextView;

    @BindView(R.id.stop_temp)
    TextView mTemperatureStopTextView;

    @BindView(R.id.start_voltage)
    TextView mVoltageStartTextView;

    @BindView(R.id.stop_voltage)
    TextView mVoltageStopTextView;

    @BindView(R.id.charge_bump)
    TextView mPercentageTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_history_detail, container, false);
        ButterKnife.bind(this, root);

        Bundle args = getArguments();
        if (null != args && args.containsKey(CHARGE_ID)) {
            long id = args.getLong(CHARGE_ID);

            UserActionsListener mActionsListener = new HistoryDetailPresenter(this, getActivity(), id);
            mActionsListener.updateDetails();
        }

        return root;
    }

    @Override
    public void setChargerType(int chargerType) {
        String type;
        switch (chargerType) {
            case 0:
                type = getString(R.string.power_state_mixed);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                type = getString(R.string.power_state_ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                type = getString(R.string.power_state_usb);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                type = getString(R.string.power_state_wireless);
                break;
            default:
                type = getString(R.string.power_state_unknown);
        }
        mChargerTypeTextView.setText(type);
    }

    @Override
    public void setChargingStartDate(Date chargingStartDate) {
        mChargingStartTextView.setText(
                sDateFormat.format(chargingStartDate)
        );
    }

    @Override
    public void setChargingDuration(long hours, long minutes, long seconds) {
        mDurationTextView.setText(String.format(
                getString(R.string.history_list_item_duration),
                hours, minutes, seconds
        ));
    }

    @Override
    public void setChargeBump(int startingPercentage) {
        mPercentageTextView.setText(String.format(
                getString(R.string.history_list_item_percentage_while_charging),
                startingPercentage
        ));
    }

    @Override
    public void setChargeBump(int startingPercentage, int finishedPercentage) {
        mPercentageTextView.setText(String.format(
                getString(R.string.history_list_item_percentage_increase),
                startingPercentage,
                finishedPercentage
        ));
    }

    @Override
    public void setStartingTemperature(float startingTemperature) {
        mTemperatureStartTextView.setText(String.format(
                getString(R.string.temperature_celsius),
                startingTemperature
        ));
    }

    @Override
    public void setFinishedTemperature(float finishedTemperature) {
        mTemperatureStopTextView.setText(String.format(
                getString(R.string.temperature_celsius),
                finishedTemperature
        ));
    }

    @Override
    public void setStartingVoltage(int startingVoltage) {
        mVoltageStartTextView.setText(String.format(
                getString(R.string.voltage),
                startingVoltage
        ));
    }

    @Override
    public void setFinishedVoltage(int finishedVoltage) {
        mVoltageStopTextView.setText(String.format(
                getString(R.string.voltage),
                finishedVoltage
        ));
    }
}
