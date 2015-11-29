package net.kacpak.batterychargingmonitor.ui.history;

import android.app.Fragment;
import android.os.Bundle;

import net.kacpak.batterychargingmonitor.R;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private HistoryContract.UserActionsListener mActionsListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        mActionsListener = new HistoryPresenter(this);
        getActivity().setTitle(R.string.title_history);
    }

    // TODO zrób na RecyclerView koniecznie!
}
