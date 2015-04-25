package de.jbapps.vplan.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.data.VPlanHeader;
import de.jbapps.vplan.data.VPlanItemData;
import de.jbapps.vplan.data.VPlanMotd;
import de.jbapps.vplan.data.VPlanSet;

/**
 * parses a VPlanSet asynchronously to a List of VPlanBaseData
 */
public class JSONParser extends AsyncTask<Void, Void, List<VPlanBaseData>> {


    private static final String DATE = "date";
    private static final String MOTD = "motd";
    private static final String VPLAN = "vplan";

    private static final String SUBJECT = "subject";
    private static final String OMITTED = "omitted";
    private static final String HOUR = "hour";
    private static final String ROOM = "room";
    private static final String CONTENT = "content";
    private static final String GRADE = "grade";

    private static final String[] PATTERNS = {
            "(.*5[^0-9]*[aA].*)",
            "(.*5[^0-9]*[bB].*)",
            "(.*5[^0-9]*[cC].*)",
            "(.*5[^0-9]*[dD].*)",
            "(.*5[^0-9]*[eE].*)",
            "(.*6[^0-9]*[aA].*)",
            "(.*6[^0-9]*[bB].*)",
            "(.*6[^0-9]*[cC].*)",
            "(.*6[^0-9]*[dD].*)",
            "(.*6[^0-9]*[eE].*)",
            "(.*7[^0-9]*[aA].*)",
            "(.*7[^0-9]*[bB].*)",
            "(.*7[^0-9]*[cC].*)",
            "(.*7[^0-9]*[dD].*)",
            "(.*7[^0-9]*[eE].*)",
            "(.*8[^0-9]*[aA].*)",
            "(.*8[^0-9]*[bB].*)",
            "(.*8[^0-9]*[cC].*)",
            "(.*8[^0-9]*[dD].*)",
            "(.*8[^0-9]*[eE].*)",
            "(.*9[^0-9]*[aA].*)",
            "(.*9[^0-9]*[bB].*)",
            "(.*9[^0-9]*[cC].*)",
            "(.*9[^0-9]*[dD].*)",
            "(.*9[^0-9]*[eE].*)",
            "(.*10[^0-9]*[aA].*)",
            "(.*10[^0-9]*[bB].*)",
            "(.*10[^0-9]*[cC].*)",
            "(.*10[^0-9]*[dD].*)",
            "(.*10[^0-9]*[eE].*)",
            "(.*[kK].*1.*)",
            "(.*[kK].*2.*)"};

    IItemsParsed mListener;
    List<VPlanBaseData> mData;
    JSONObject vplan1;
    JSONObject vplan2;
    private String gradePattern;
    private boolean multipleGrades = true;

    public JSONParser(IItemsParsed listener, String grades, VPlanSet vPlanSet) {
        this.vplan1 = vPlanSet.getVPlan1();
        this.vplan2 = vPlanSet.getVPlan2();
        this.mListener = listener;
        this.gradePattern = generatePattern(grades);
        Log.e("PATTERN: ", gradePattern);
        mData = new ArrayList<>();
    }

    private String generatePattern(String grades) {
        StringBuilder patternBuilder = new StringBuilder();
        boolean first = true;
        if (grades.equals("")) {
            Log.e("matcher", "matched... fuu");
            return ".*";
        }
        for (String pattern : PATTERNS) {
            if (grades.matches(pattern)) {
                if (first) {
                    patternBuilder.append(pattern);
                    first = false;
                } else {
                    patternBuilder.append("|");
                    patternBuilder.append(pattern);
                }
            }
        }
        String result = patternBuilder.toString();
        if (!result.contains("|") && !result.equals("")) {
            multipleGrades = false;
        }

        if (result.equals("")) {
            return ".*";
        } else {
            return result;
        }
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
        JSONObject header = vplan.getJSONObject(VPlanLoader.HEADER);
        mData.add(new VPlanHeader(header.getString(VPlanLoader.HEADER_TITLE), header.getString(VPlanLoader.HEADER_STATUS)));


        //get "Nachrichten zum Tag"
        StringBuilder buffer = new StringBuilder();
        JSONArray temp = vplan.getJSONArray(MOTD);
        int length = temp.length();
        for (int i = 0; i < length; i++) {
            JSONArray temp2 = temp.getJSONArray(i);
            int length2 = temp2.length();
            for (int j = 0; j < length2; j++) {
                buffer.append(temp2.getString(j));
                if (j >= 1) {
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
            if (temp2.getString(GRADE).matches(gradePattern)) {
                if (multipleGrades) {
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
                mListener.onItemsParsed(list);
            } else {
                mListener.onItemsParsed(null);
            }
        }
    }

    public interface IItemsParsed {
        void onItemsParsed(List<VPlanBaseData> dataList);
    }
}