package xyz.jbapps.vplan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.ui.activity.MainActivity;

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

    private SharedPreferences getSettingsPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public boolean readShowAll() {
        return getSettingsPreferences().getBoolean(appContext.getString(R.string.preference_grade_show_all_key), true);
    }

    public String readGrades() {
        Set<String> grades = getSettingsPreferences().getStringSet(appContext.getString(R.string.preference_grade_key), new HashSet<String>());
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(String grade : grades) {
            if (first) {
                builder.append(grade);
                first = false;
            } else {
                builder.append(",");
                builder.append(grade);
            }
        }

        return builder.toString();
    }

    public String readCourse() {
        return getSettingsPreferences().getString(appContext.getString(R.string.preference_course_key), "");
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