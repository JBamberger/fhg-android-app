package xyz.jbapps.vplan;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by Jannik on 25.03.2016.
 */
public class FHGApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
