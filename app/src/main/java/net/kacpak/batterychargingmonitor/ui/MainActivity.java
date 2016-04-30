package net.kacpak.batterychargingmonitor.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.kacpak.batterychargingmonitor.R;
import net.kacpak.batterychargingmonitor.ui.history.HistoryFragment;
import net.kacpak.batterychargingmonitor.ui.settings.SettingsActivity;
import net.kacpak.batterychargingmonitor.ui.summary.SummaryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        // ButterKnife
        ButterKnife.bind(this);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize fragment
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, new SummaryFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_summary);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int id = item.getItemId();

        // Ustawienia
        if (id == R.id.nav_settings) {
            mDrawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        // Zmiana fragmentu
        Fragment fragment = null;
        if (id == R.id.nav_summary)
            fragment = new SummaryFragment();
        else if (id == R.id.nav_history)
            fragment = new HistoryFragment();

        if (fragment != null)
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
