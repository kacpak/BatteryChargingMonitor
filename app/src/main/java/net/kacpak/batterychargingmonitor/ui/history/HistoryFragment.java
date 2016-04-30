package net.kacpak.batterychargingmonitor.ui.history;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.kacpak.batterychargingmonitor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import net.kacpak.batterychargingmonitor.data.database.DatabaseContract.DataEntry;

public class HistoryFragment extends Fragment implements
        HistoryContract.View, LoaderManager.LoaderCallbacks<Cursor>,
        HistoryAdapter.HistoryAdapterOnClickHandler {

    /**
     * ID dla nowo-tworzonego Loadera
     */
    private static final int HISTORY_LOADER = 0;

    /**
     * Interaktor
     */
    private HistoryContract.UserActionsListener mActionsListener;

    /**
     * RecyclerView do wyświetlenia historii ładowań telefony
     */
    @BindView(R.id.recyclerview_history)
    RecyclerView mRecyclerView;

    /**
     * Adapter obsługujący wyświetlanie poszczególnych wpisów z bazy
     */
    private HistoryAdapter mHistoryAdapter;

    /**
     * Initializer
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        mActionsListener = new HistoryPresenter(this, getActivity());
        getActivity().setTitle(R.string.title_history);
        getLoaderManager().initLoader(HISTORY_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Tworzy widok naszego Fragmentu
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_history, container, false);
        ButterKnife.bind(this, root);

        mHistoryAdapter = new HistoryAdapter(getActivity(), this, null);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mHistoryAdapter);

        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mActionsListener.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mActionsListener.onLoadFinished(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mActionsListener.onLoaderReset();
    }

    @Override
    public void onClick(long id, HistoryAdapter.HistoryAdapterViewHolder holder) {
        mActionsListener.onClick(id);
    }

    @Override
    public void swapCursor(Cursor cursor) {
        mHistoryAdapter.swapCursor(cursor);
    }
}
