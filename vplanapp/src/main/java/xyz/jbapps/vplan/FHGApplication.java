package xyz.jbapps.vplan;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import xyz.jbapps.vplan.util.Property;

public class FHGApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Property p = new Property(this);
        String theme = p.getThemeSetting();
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "auto":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;

        }

    }
}
