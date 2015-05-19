package de.jbapps.vplan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import de.jbapps.vplan.VPlanActivity;

/**
 * This class provides access to the applications SharedPreferences
 */
public class Property {

    private static final String TAG = "Property";
    private static final String PROPERTY_GRADES = "grades";
    private static final String PROPERTY_SHOW_GRADE_PICKER = "show_grade_picker";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_CLIENT_ID = "client_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private Context appContext;

    public Property(Context appContext) {
        this.appContext = appContext;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * These preferences belong to the MainActivity, in this case VPlanActivity
     */
    private SharedPreferences getMainPreferences() {
        return appContext.getSharedPreferences(VPlanActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * These preferences belong to the Application and represent the values stored via Settings-API
     */
    private SharedPreferences getSettingsPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getMainPreferences();
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getMainPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId == null || registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public String getClientId() {
        final SharedPreferences prefs = getMainPreferences();
        String clientId = prefs.getString(PROPERTY_CLIENT_ID, "");
        if (clientId == null || clientId.isEmpty()) {
            Log.i(TAG, "VPlanID not found.");
            return "";
        } else {
            return clientId;
        }
    }

    public void storeClientId(String clientId) {
        final SharedPreferences prefs = getMainPreferences();
        Log.i(TAG, "Saving vplanId");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_CLIENT_ID, clientId);
        editor.apply();
    }

    /**
     * This method retrieves the 'grades' value stored in the SettingsActivity
     */
    public String readGrade() {
        return getSettingsPreferences().getString(PROPERTY_GRADES, "");
    }

    public void storeGrade(String grades) {
        final SharedPreferences prefs = getSettingsPreferences();
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