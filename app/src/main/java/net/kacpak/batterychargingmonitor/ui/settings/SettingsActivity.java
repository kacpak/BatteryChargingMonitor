package net.kacpak.batterychargingmonitor.ui.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.kacpak.batterychargingmonitor.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standalone);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.content, new SettingsFragment())
                    .commit();
        }
    }

}
