package net.kacpak.batterychargingmonitor.ui.summary;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import net.kacpak.batterychargingmonitor.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SummaryFragment extends Fragment implements SummaryContract.View {

    private SummaryContract.UserActionsListener mActionsListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        mActionsListener = new SummaryPresenter(this);
        getActivity().setTitle(R.string.title_summary);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_summary, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.startUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        mActionsListener.stopUpdates();
    }

    @Bind(R.id.battery_charge_indicator)
    DonutProgress mBatteryChargeIndicator;

    @Override
    public void setBatteryChargeIndicator(@IntRange(from = 0, to = 100) int percentage) {
        mBatteryChargeIndicator.setProgress(percentage);
    }

    @Bind(R.id.health)
    TextView mBatteryHealth;

    @Override
    public void setBatteryHealth(@StringRes int healthId) {
        mBatteryHealth.setText(healthId);
    }

    @Bind(R.id.voltage)
    TextView mBatteryVoltage;

    @Override
    public void setBatteryVoltage(int voltage) {
        mBatteryVoltage.setText(String.format(getString(R.string.voltage), voltage));
    }

    @Bind(R.id.temperature)
    TextView mBatteryTemperature;

    @Override
    public void setBatteryTemperatureInCelsius(double temperature) {
        mBatteryTemperature.setText(String.format(getString(R.string.temperature_celsius), temperature));
    }

    @Override
    public void setBatteryTemperatureInFahrenheit(double temperature) {
        mBatteryTemperature.setText(String.format(getString(R.string.temperature_fahrenheit), temperature));
    }

    @Bind(R.id.counter)
    TextView mBatteryChargingCounter;

    @Override
    public void setBatteryChargingCounter(int counter) {
        mBatteryChargingCounter.setText(String.format(getString(R.string.count_times), counter));
    }
}
