package net.kacpak.batterychargingmonitor.ui.summary;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import net.kacpak.batterychargingmonitor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryFragment extends Fragment implements SummaryContract.View {

    private SummaryContract.UserActionsListener mActionsListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        mActionsListener = new SummaryPresenter(this, getActivity());
        getActivity().setTitle(R.string.title_summary);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_summary, container, false);
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_summary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_battery_settings)
            return showPowerUsage();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Uruchamia domyślną aplikację monitorującą zużycie baterii
     */
    private boolean showPowerUsage() {
        Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        ResolveInfo resolveInfo = getActivity().getPackageManager().resolveActivity(powerUsageIntent, 0);

        // Check that the Battery app exists on this device
        if (resolveInfo != null)
            startActivity(powerUsageIntent);

        return true;
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

    @BindView(R.id.battery_charge_indicator)
    DonutProgress mBatteryChargeIndicator;

    @Override
    public void setBatteryChargeIndicator(@IntRange(from = 0, to = 100) int percentage) {
        mBatteryChargeIndicator.setProgress(percentage);
    }

    @BindView(R.id.health)
    TextView mBatteryHealth;

    @Override
    public void setBatteryHealth(@StringRes int healthId) {
        mBatteryHealth.setText(healthId);
    }

    @BindView(R.id.voltage)
    TextView mBatteryVoltage;

    @Override
    public void setBatteryVoltage(int voltage) {
        mBatteryVoltage.setText(String.format(getString(R.string.voltage), voltage));
    }

    @BindView(R.id.current)
    TextView mBatteryCurrent;

    @Override
    public void setBatteryCurrent(int current) {
        mBatteryCurrent.setText(String.format(getString(R.string.current), current));
    }

    @Override
    public void setBatteryCurrent(int current, int currentAvg) {
        String currentTxt = new StringBuilder()
                .append(String.format(getString(R.string.current), current))
                .append(" (")
                .append(String.format(getString(R.string.current), currentAvg))
                .append(')')
                .toString();

        mBatteryCurrent.setText(currentTxt);
    }

    @BindView(R.id.current_heading)
    TextView mBatteryCurrentHeading;

    @Override
    public void hideBatteryCurrentData() {
        mBatteryCurrent.setVisibility(View.GONE);
        mBatteryCurrentHeading.setVisibility(View.GONE);
    }

    @BindView(R.id.temperature)
    TextView mBatteryTemperature;

    @Override
    public void setBatteryTemperatureInCelsius(double temperature) {
        mBatteryTemperature.setText(String.format(getString(R.string.temperature_celsius), temperature));
    }

    @Override
    public void setBatteryTemperatureInFahrenheit(double temperature) {
        mBatteryTemperature.setText(String.format(getString(R.string.temperature_fahrenheit), temperature));
    }

    @BindView(R.id.counter)
    TextView mBatteryChargingCounter;

    @Override
    public void setBatteryChargingCounter(int counter) {
        mBatteryChargingCounter.setText(String.format(getString(R.string.count_times), counter));
    }
}
