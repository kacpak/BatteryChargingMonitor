package net.kacpak.batterychargingmonitor;

import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        FlowConfig flowConfig = new FlowConfig.Builder(this).build();
        FlowManager.init(flowConfig);
    }

    public static Context getContext(){
        return mContext;
    }
}