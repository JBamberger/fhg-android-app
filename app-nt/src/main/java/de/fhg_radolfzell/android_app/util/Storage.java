package de.fhg_radolfzell.android_app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.event.VPlanLoadedEvent;
import de.fhg_radolfzell.android_app.data.VPlan;
import timber.log.Timber;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Singleton
public class Storage {

    private Context context;
    private SharedPreferences sharedPreferences;

    @Inject
    public Storage(@NonNull Context context, @NonNull SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public void setLastSeenEtagVPlan1(String value) {
        sharedPreferences.edit().putString(context.getString(R.string.pref_last_seen_etag_vplan_1), value).apply();
    }

    public
    @Nullable
    String getLastSeenEtagVPlan1() {
        return sharedPreferences.getString(context.getString(R.string.pref_last_seen_etag_vplan_1), null);
    }

    public void setLastSeenEtagVPlan2(String value) {
        sharedPreferences.edit().putString(context.getString(R.string.pref_last_seen_etag_vplan_2), value).apply();
    }

    public
    @Nullable
    String getLastSeenEtagVPlan2() {
        return sharedPreferences.getString(context.getString(R.string.pref_last_seen_etag_vplan_2), null);
    }

    public void setShowInitialSettings(boolean value) {
        sharedPreferences.edit().putBoolean(context.getString(R.string.pref_show_settings_key), value).apply();
    }

    public boolean getShowInitialSettings() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_show_settings_key), false);
    }

    public
    @IdRes
    int getLastSelectedFragment() {
        return sharedPreferences.getInt(context.getString(R.string.pref_last_seen_fragment), R.id.drawer_settings);
    }

    public void setLastSelectedFragment(@IdRes int fragment) {
        sharedPreferences.edit().putInt(context.getString(R.string.pref_last_seen_fragment), fragment).apply();
    }

    public boolean getShowDrawer() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_show_drawer_key), true);
    }

    public void setShowDrawer(boolean state) {
        sharedPreferences.edit().putBoolean(context.getString(R.string.pref_show_drawer_key), state).apply();
    }

    public String getFcmToken() {
        return sharedPreferences.getString(context.getString(R.string.pref_fcm_token), null);
    }

    public void setFcmToken(String token) {
        sharedPreferences.edit().putString(context.getString(R.string.pref_fcm_token), token).apply();
    }

    public boolean getFcmSubscribed() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_fcm_subscribed), true);
    }

    public void setFcmSubscribed(boolean state) {
        sharedPreferences.edit().putBoolean(context.getString(R.string.pref_fcm_subscribed), state).apply();
    }

    public boolean getSettingsAnalyticsEnabled() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_enable_analytics_key), true);
    }

    public boolean getSettingsNotificationEnabled() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_notification_enabled_key), true);
    }

    public String getSettingsNotificationRingtone() {
        return sharedPreferences.getString(context.getString(R.string.pref_notification_ringtone_key), null);
    }

    public boolean getSettingsNotificationVibrate() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_notification_vibrate_key), true);
    }

    public boolean getSettingsVPlanShowAllGrades() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_grade_show_all_key), true);
    }

    public Set<String> getSettingsVPlanSelectedGrades() {
        return sharedPreferences.getStringSet(context.getString(R.string.pref_grade_key), null);
    }

    public String getSettingsVPlanSelectedCourses() {
        return sharedPreferences.getString(context.getString(R.string.pref_course_key), null);
    }

    public @Nullable String[] getGrades() {
        boolean all = getSettingsVPlanShowAllGrades();
        if (!all) {
            Set<String> gradeSet = getSettingsVPlanSelectedGrades();
            if (gradeSet != null) {
                String[] grades = new String[gradeSet.size()];
                return gradeSet.toArray(grades);
            }
        }
        return null;
    }

    public String getGradeString() {
        StringBuilder builder = new StringBuilder();
        String[] grades = getGrades();
        if (grades != null && grades.length > 0) {
            for (String grade : grades) {
                builder.append(grade);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length() - 1);
        } else {
            builder.append(context.getString(R.string.vplan_showing_all));
        }


        return builder.toString();
    }

    @Subscribe
    public void onVPlanUpdated(VPlanLoadedEvent event) {
        VPlan[] vplans = event.vPlans;
        if(vplans != null) {
            if(vplans.length  >= 2) {
                setLastSeenEtagVPlan1(vplans[0].etag);
                setLastSeenEtagVPlan2(vplans[1].etag);
            } else {
                Timber.d("VPlan Update with less than 2 vplans");
            }
        }
    }
}
