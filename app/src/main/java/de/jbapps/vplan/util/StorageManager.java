package de.jbapps.vplan.util;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageManager {

    private static final String PREFERENCES = "vplan_preferences";

    private static final String UI_GRADE_INDEX = "ui_grade_index";

    private static final String DATA_HEADER_1 = "header_1";
    private static final String DATA_HEADER_2 = "header_2";
    private static final String DATA_VPLAN_1 = "vplan_1";
    private static final String DATA_VPLAN_2 = "vplan_2";

    private Context mContext;
    private SharedPreferences mPreferences;


    public StorageManager(Context context) {
        this.mContext = context;
        mPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public String[] readHeaders() {
        String[] headers = new String[2];
        headers[0] = mPreferences.getString(DATA_HEADER_1, "empty");
        headers[1] = mPreferences.getString(DATA_HEADER_2, "empty");
        return headers;
    }

    public void writeHeaders(String[] headers) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(DATA_HEADER_1, headers[0]);
        editor.putString(DATA_HEADER_2, headers[1]);
        editor.apply();
    }

    public String[] readVPlan() {
        String[] plans = new String[2];
        plans[0] = mPreferences.getString(DATA_VPLAN_1, "");
        plans[1] = mPreferences.getString(DATA_VPLAN_2, "");
        return plans;
    }

    public void writeVPlan(String[] plans) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(DATA_VPLAN_1, plans[0]);
        editor.putString(DATA_VPLAN_2, plans[1]);
        editor.apply();
    }

    private int readGradeIndex() {
        return mPreferences.getInt(UI_GRADE_INDEX, 0);
    }

    private void writeGradeIndex(int index) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(UI_GRADE_INDEX, index);
        editor.apply();
    }

}
