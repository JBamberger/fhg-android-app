package de.jbapps.vplan.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.data.VPlanBaseData;

public class VPlanHTJParser extends AsyncTask<Void, Void, Void> {

    private static final String DATE = "date";
    private static final String MOTD = "motd";
    private static final String MOTD_ROW = "motd_row";
    private static final String VPLAN = "vplan";

    private static final String SUBJECT = "subject";
    private static final String OMITTED = "omitted";
    private static final String HOUR = "hour";
    private static final String ROOM = "room";
    private static final String CONTENT = "content";
    private static final String GRADE = "grade";

    IOnFinishedLoading mListener;
    List<VPlanBaseData> mData;
    private String vplan1Html;
    private String vplan2Html;
    private JSONObject vplan1Json;
    private JSONObject vplan2Json;

    public VPlanHTJParser(/*IOnFinishedLoading listener,*/  String vplan1Html, String vplan2Html) {

        this.vplan1Html = vplan1Html;
        this.vplan2Html = vplan2Html;
        this.vplan1Json = new JSONObject();
        this.vplan2Json = new JSONObject();
        //this.mListener = listener;
        mData = new ArrayList<VPlanBaseData>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            parse(vplan1Html, vplan1Json);
            parse(vplan2Html, vplan2Json);

            Log.d("", "#########################  VPLAN 1  ##########################################");
            Log.d("", vplan1Json.toString());
            Log.d("", "##############################################################################");
            Log.d("", vplan2Json.toString());
            Log.d("", "#########################  VPLAN 2  ##########################################");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parse(String vplan, JSONObject jPlan) throws JSONException {
        Document doc = Jsoup.parse(vplan);
        JSONArray temp = new JSONArray();

        //get date and day name
        jPlan.put(DATE, doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text());

        //get "Nachrichten zum Tag"
        Elements infoRows = doc.getElementsByClass("info").select("tr");

        for (Element line : infoRows) {
            Elements cells = line.children().select("td");

            JSONArray temp2 = new JSONArray();
            for (Element cell : cells) {
                temp2.put(cell.text());
            }
            try {
                temp2.get(0);
                temp.put(temp2);
            } catch (JSONException e) {
                Log.d("HTML to JSON","Unknown empty element discarded...");
            }

        }
        jPlan.put(MOTD, temp);
        temp = new JSONArray();

        //get vplan rows
        Elements listRows = doc.getElementsByClass("list").select("tr");
        for (Element line : listRows) {
            Elements cells = line.children();
            if (cells.select("th").size() == 0) {
                JSONObject row = new JSONObject();
                row.put(SUBJECT, cells.get(3).text());
                row.put(HOUR, cells.get(1).text());
                row.put(GRADE, cells.get(0).text());
                row.put(CONTENT, cells.get(2).text());
                row.put(ROOM, cells.get(4).text());
                row.put(OMITTED, Boolean.toString(cells.get(5).text().contains("x")));
                temp.put(row);
            }
        }
        jPlan.put(VPLAN, temp);

    }

    @Override
    protected void onPostExecute(Void v) {
        if (mListener != null) {
            mListener.onVPlanParsed();
            mListener.onVPlanParsingFailed();
        }
    }

    public interface IOnFinishedLoading {
        public void onVPlanParsed();

        public void onVPlanParsingFailed();
    }
}