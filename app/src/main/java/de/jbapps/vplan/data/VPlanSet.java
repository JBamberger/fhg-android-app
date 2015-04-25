package de.jbapps.vplan.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class contains all information about a given VPlan set, including headers and content for two days.
 * It also manages persisting this data.
 */
public class VPlanSet {

    private static final String TAG = "VPlanSet";

    private static final String PROPERTY = "vplan_data";
    private static final String PROPERTY_VPLAN_1 = "vplan1";
    private static final String PROPERTY_VPLAN_2 = "vplan2";
    private static final String PROPERTY_HEADER_1 = "header1";
    private static final String PROPERTY_HEADER_2 = "header2";

    private String mHeader1;
    private String mHeader2;
    private JSONObject mVPlan1;
    private JSONObject mVPlan2;
    private SharedPreferences mPreferences;

    public VPlanSet(Context context) {
        mPreferences = context.getSharedPreferences(PROPERTY, Context.MODE_PRIVATE);
    }

    public String getHeader1() {
        return mHeader1;
    }

    public void setHeader1(String header) {
        mHeader1 = header;
    }

    public String getHeader2() {
        return mHeader2;
    }

    public void setHeader2(String header) {
        mHeader2 = header;
    }

    public JSONObject getVPlan1() {
        return mVPlan1;
    }

    public void setVPlan1(JSONObject vplan) {
        mVPlan1 = vplan;
    }

    public JSONObject getVPlan2() {
        return mVPlan2;
    }

    public void setVPlan2(JSONObject vplan) {
        mVPlan2 = vplan;
    }

    /**
     * reads the headers from persistent storage
     * */
    public boolean readHeader() {
        mHeader1 = mPreferences.getString(PROPERTY_HEADER_1, null);
        mHeader2 = mPreferences.getString(PROPERTY_HEADER_2, null);
        return headersSet();
    }

    /**
     * reads the whole data set from persistent storage returns true if all fields are available
     * */
    public boolean readVPlan() {
        mHeader1 = mPreferences.getString(PROPERTY_HEADER_1, null);
        mHeader2 = mPreferences.getString(PROPERTY_HEADER_2, null);
        try {
            mVPlan1 = new JSONObject(mPreferences.getString(PROPERTY_VPLAN_1, null));
            mVPlan2 = new JSONObject(mPreferences.getString(PROPERTY_VPLAN_2, null));
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return allFieldsSet();
    }

    /**
     * writes the whole data set to persistent storage if all members are initialized
     * */
    public void writeAll() {
        if(allFieldsSet()) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(PROPERTY_HEADER_1, mHeader1);
            editor.putString(PROPERTY_HEADER_2, mHeader2);
            editor.putString(PROPERTY_VPLAN_1, mVPlan1.toString());
            editor.putString(PROPERTY_VPLAN_2, mVPlan2.toString());
            editor.apply();
            Log.i(TAG, "Wrote VPlanSet successfully");
        } else {
            Log.w(TAG, "Not all fields are initialized. Writing aborted!");
        }
    }

    private boolean allFieldsSet() {
        return headersSet() && vPlanSet();
    }

    private boolean vPlanSet() {
        return mVPlan1 != null && mVPlan2 != null;
    }

    private boolean headersSet() {
        return mHeader1 != null && mHeader2 != null;
    }
}
