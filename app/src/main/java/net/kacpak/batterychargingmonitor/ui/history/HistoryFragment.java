package net.kacpak.batterychargingmonitor.ui.history;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.data.BatteryStatus;
import net.kacpak.batterychargingmonitor.ui.NavigationDrawerManipulation;
import net.kacpak.batterychargingmonitor.ui.historydetail.HistoryDetailContract;
import net.kacpak.batterychargingmonitor.ui.historydetail.HistoryDetailDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryFragment extends Fragment implements
        HistoryContract.View, LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {

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
    @BindView(R.id.listview_history)
    ListView mListView;

    /**
     * Adapter obsługujący wyświetlanie poszczególnych wpisów z bazy
     */
    private HistoryAdapter mHistoryAdapter;

    /**
     * Initializer
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
        setHasOptionsMenu(true);

        mHistoryAdapter = new HistoryAdapter(getActivity(), null);

        mListView.setAdapter(mHistoryAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(this);
//        mListView.setEmptyView(null);

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
    public void swapCursor(Cursor cursor) {
        mHistoryAdapter.swapCursor(cursor);
    }

    @Override
    public void showDeletedCountMessage(int count) {
        Toast.makeText(getActivity(), getString(R.string.history_list_counted_entries_deleted, count), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMergedCountMessage(int count) {
        Toast.makeText(getActivity(), getString(R.string.history_list_counted_entries_merged, count), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            Bundle args = new Bundle();
            args.putLong(HistoryDetailContract.CHARGE_ID, cursor.getLong(0));

            DialogFragment fragment = new HistoryDetailDialog();
            fragment.setArguments(args);
            fragment.show(getFragmentManager(), "dialog");
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Jeśli jest zaznaczony ostatni wpis w trakcie ładowania, dezaktywuj usuwanie
        mode.getMenu().findItem(R.id.action_delete).setEnabled(
                !(mListView.getCheckedItemPositions().get(0) && new BatteryStatus().isCharging()));

        // Jeśli zaznaczeń są conajmniej 2, to aktywuj łączenie
        mode.getMenu().findItem(R.id.action_merge).setEnabled(mListView.getCheckedItemCount() > 1);

        String title = String.format(getString(R.string.history_list_selected_count), mListView.getCheckedItemCount());
        mode.setTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history, menu);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate menu for CAB (Contextual Action Bar)
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_history_contextual, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {switch (item.getItemId()) {
        case R.id.action_clear_entries:
            mActionsListener.removeIrrelevantEntries();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getActivity().getWindow().setStatusBarColor(
                    getResources().getColor(R.color.colorPrimaryDark)
            );
        }

        ((NavigationDrawerManipulation) getActivity()).disableNavigationDrawer();
        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        final int id = item.getItemId();

        DialogInterface.OnClickListener positiveAnswer = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (id == R.id.action_delete)
                    mActionsListener.removeEntries(getSelectedEntriesIds());
                else
                    mActionsListener.mergeEntries(getSelectedEntriesIds());
                mode.finish();
            }
        };

        final int message = (id == R.id.action_delete) ? R.string.history_list_confirm_delete : R.string.history_list_confirm_merge;

        new AlertDialog.Builder(getActivity())
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, positiveAnswer)
                .setNegativeButton(android.R.string.no, null)
                .show();

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ((NavigationDrawerManipulation) getActivity()).enableNavigationDrawer();
    }

    /**
     * Zwraca id zaznaczonych elementów
     * @return lista z id zaznaczonych elementów
     */
    private List<Long> getSelectedEntriesIds() {
        List<Long> ids = new ArrayList<>();
        SparseBooleanArray positions = mListView.getCheckedItemPositions();

        for (int i = 0; i < positions.size(); i++) {
            if (positions.valueAt(i)) {
                Cursor item = (Cursor) mHistoryAdapter.getItem(positions.keyAt(i));
                ids.add(item.getLong(0));
            }
        }

        return ids;
    }
}
