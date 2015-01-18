package de.jbapps.vplan.util;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.data.VPlanHeader;
import de.jbapps.vplan.data.VPlanItemData;
import de.jbapps.vplan.data.VPlanMotd;

public class VPlanJSONParser extends AsyncTask<Void, Void, List<VPlanBaseData>> {


    private static final String DATE = "date";
    private static final String MOTD = "motd";
    private static final String VPLAN = "vplan";

    private static final String SUBJECT = "subject";
    private static final String OMITTED = "omitted";
    private static final String HOUR = "hour";
    private static final String ROOM = "room";
    private static final String CONTENT = "content";
    private static final String GRADE = "grade";

    IOnFinishedLoading mListener;
    List<VPlanBaseData> mData;
    JSONObject vplan1;
    JSONObject vplan2;
    private String gradePattern;

    public VPlanJSONParser(IOnFinishedLoading listener, String gradePattern, JSONObject vplan1, JSONObject vplan2) {
        this.vplan1 = vplan1;
        this.vplan2 = vplan2;
        this.mListener = listener;
        this.gradePattern = gradePattern;
        mData = new ArrayList<>();
    }

    @Override
    protected List<VPlanBaseData> doInBackground(Void... params) {
        try {
            parse(vplan1);
            parse(vplan2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mData;
    }

    private void parse(JSONObject vplan) throws JSONException {

        //get date and day name
        mData.add(new VPlanHeader(vplan.getString(DATE)));


        //get "Nachrichten zum Tag"
        StringBuilder buffer = new StringBuilder();
        JSONArray temp = vplan.getJSONArray(MOTD);
        int length = temp.length();
        for (int i = 0; i < length; i++) {
            JSONArray temp2 = temp.getJSONArray(i);
            int length2 = temp2.length();
            for(int j = 0; j < length2; j++) {
                buffer.append(temp2.getString(j));
                if(j >= 1) {
                    buffer.append(" ");
                }
            }
            buffer.append("\n");
        }
        mData.add(new VPlanMotd(buffer.toString()));

        //get vplan rows
        temp = vplan.getJSONArray(VPLAN);
        length = temp.length();
        for (int i = 0; i < length; i++) {
            JSONObject temp2 = temp.getJSONObject(i);
                VPlanItemData row;
            if(temp2.getString(GRADE).matches(gradePattern)) {
                if (gradePattern.equals(".*")) {
                    row = new VPlanItemData(temp2.getString(HOUR), temp2.getString(CONTENT), temp2.getString(GRADE) + ": " + temp2.getString(SUBJECT), temp2.getString(ROOM), temp2.getBoolean(OMITTED));
                } else {
                    row = new VPlanItemData(temp2.getString(HOUR), temp2.getString(CONTENT), temp2.getString(SUBJECT), temp2.getString(ROOM), temp2.getBoolean(OMITTED));
                }
                mData.add(row);
            }
        }
    }

    @Override
    protected void onPostExecute(final List<VPlanBaseData> list) {
        if (mListener != null) {
            if (list != null) {
                mListener.onVPlanParsed(list);
            } else {
                mListener.onVPlanParsingFailed();
            }
        }
    }

    public interface IOnFinishedLoading {
        public void onVPlanParsed(List<VPlanBaseData> dataList);

        public void onVPlanParsingFailed();
    }
}