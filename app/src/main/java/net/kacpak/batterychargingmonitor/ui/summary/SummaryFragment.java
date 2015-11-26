package net.kacpak.batterychargingmonitor.ui.summary;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public void onStop() {
        super.onStop();
        mActionsListener.stopUpdates();
    }

    @Bind(R.id.battery_charge_indicator)
    DonutProgress mBatteryChargeIndicator;

    @Override
    public void setBatteryChargeIndicator(@IntRange(from = 0, to = 100) int percentage) {
        mBatteryChargeIndicator.setProgress(percentage);
    }

    @Override
    public void setBatteryHealth(@IdRes int healthId) {

    }

    @Override
    public void setBatteryVoltage(int voltage) {

    }

    @Override
    public void setBatteryAmperage(int amperage) {

    }

    @Override
    public void setBatteryTemperature(double temperature) {

    }

    @Override
    public void setBatteryChargingCounter(int counter) {

    }
}
