package xyz.jbapps.vplan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import xyz.jbapps.vplan.MainActivity;
import xyz.jbapps.vplan.R;

/**
 * This class provides access to the applications SharedPreferences
 */
public class Property {

    private static final String TAG = "VPlanProperty";

    private static final String PROPERTY_SHOW_GRADE_PICKER = "show_grade_picker";
    private final Context appContext;
    private static final String PROPERTY_GRADES = "grades";

    public Property(Context appContext) {
        this.appContext = appContext;
    }

    /**
     * These preferences belong to the MainActivity, in this case VPlanActivity
     */
    private SharedPreferences getMainPreferences() {
        return appContext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    public String readGrade() {
        return getMainPreferences().getString(PROPERTY_GRADES, "");
    }

    public void storeGrade(String grades) {
        final SharedPreferences prefs = getMainPreferences();
        Log.i(TAG, "Saving grades");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_GRADES, grades);
        editor.apply();
    }

    public boolean getShowGradePicker() {
        return getMainPreferences().getBoolean(PROPERTY_SHOW_GRADE_PICKER, true);
    }

    public void setShowGradePicker(boolean show) {
        final SharedPreferences prefs = getMainPreferences();
        Log.i(TAG, "Saving showgradepicker: " + show);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PROPERTY_SHOW_GRADE_PICKER, show);
        editor.apply();
    }
}