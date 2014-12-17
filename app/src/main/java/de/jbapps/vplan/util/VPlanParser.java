package de.jbapps.vplan.util;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.data.VPlanHeader;
import de.jbapps.vplan.data.VPlanItemData;
import de.jbapps.vplan.data.VPlanMotd;

public class VPlanParser extends AsyncTask<Void, Void, List<VPlanBaseData>> {


    IOnFinishedLoading mListener;
    List<VPlanBaseData> mData;
    String vplan1;
    String vplan2;
    private String gradePattern;

    public VPlanParser(IOnFinishedLoading listener, String gradePattern, String vplan1, String vplan2) {
        this.vplan1 = vplan1;
        this.vplan2 = vplan2;
        this.mListener = listener;
        this.gradePattern = gradePattern;
        mData = new ArrayList<VPlanBaseData>();
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

    private void parse(String vplan) {
        Document doc = Jsoup.parse(vplan);

        //get date and day name
        mData.add(new VPlanHeader(doc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text()));

        //get "Nachrichten zum Tag"
        Elements infoRows = doc.getElementsByClass("info").select("tr");
        StringBuilder buffer = new StringBuilder();
        for (Element line : infoRows) {
            Elements cells = line.children().select("td");
            for (Element cell : cells) {
                buffer.append(cell.text());
                buffer.append("\n");
            }
        }
        mData.add(new VPlanMotd(buffer.toString()));

        //get vplan rows
        Elements listRows = doc.getElementsByClass("list").select("tr");
        for (Element line : listRows) {
            Elements cells = line.children();
            if (cells.get(0).text().matches(gradePattern) && cells.select("th").size() == 0) {
                VPlanItemData row;
                if (gradePattern.equals(".*")) {
                    row = new VPlanItemData(cells.get(1).text(), cells.get(2).text(), cells.get(0).text() + ": " + cells.get(3).text(), cells.get(4).text(), cells.get(5).text().contains("x"));
                } else {
                    row = new VPlanItemData(cells.get(1).text(), cells.get(2).text(), cells.get(3).text(), cells.get(4).text(), cells.get(5).text().contains("x"));
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