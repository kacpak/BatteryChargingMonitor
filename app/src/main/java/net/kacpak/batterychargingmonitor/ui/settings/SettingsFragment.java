package net.kacpak.batterychargingmonitor.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.kacpak.batterychargingmonitor.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
