package de.fhg_radolfzell.android_app.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.fhg_radolfzell.android_app.data.VPlan;
import de.fhg_radolfzell.android_app.main.vplan.VPlanScope;
import timber.log.Timber;

@VPlanScope
public class VPlanHtmlParser {

    private static final String TAG = "VPlanHtmlParser";
    private final SimpleDateFormat dateParser;

    public VPlanHtmlParser() {
        this.dateParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    }

    public @NonNull VPlan filterGrades(@NonNull VPlan vplan) {
        // TODO: 24.03.2017 Implement
        return vplan;
    }

    /**
     * @param vplan VPlanObject to parse
     * @return VPlanData
     * @throws IOException if an error occurs while parsing
     */
    @NonNull
    public VPlan parseVPlan(String vplan) throws IOException {
        try {
            Document doc = Jsoup.parse(vplan);
            VPlan plan = new VPlan();
            plan.updatedAt = readVPlanStatus(vplan);
            plan.dateAt = readVPlanTitle(doc);
            plan.motd = readMotdTable(doc.getElementsByClass("info"));
            plan.entries = readVPlanTable(doc.getElementsByClass("list").select("tr"));
            return plan;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("parsing of vplan failed");
        }
    }

    @NonNull
    private String readVPlanStatus(String vplan) {
        try {
            String status = vplan.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            return status;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "VPlan file is corrupted, an error occurred...";
        }
    }

    @NonNull
    private String readVPlanTitle(Document vplanDoc) {
        return vplanDoc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text();

    }

    @NonNull
    private String readMotdTable(Elements classInfo) {
        Elements tableRows = classInfo.select("tr");
        StringBuilder motd = new StringBuilder();
        for (Element line : tableRows) {
            Elements cells = line.children().select("td");
            int size = cells.size();
            if(size == 0) {
                continue;
            }
            boolean highlightRow = size > 1 && cells.first().text().toLowerCase().contains("unterrichtsfrei");
            if(highlightRow) {
                motd.append("<font color=#FF5252>");
            }
            for (int i = 0; i < size; i++) {
                Element cell = cells.get(i);
                motd.append(cell.toString());
                if(i < size - 2) {
                    motd.append(" | ");
                }
            }
            if(highlightRow) {
                motd.append("</font>");
            }
            motd.append("<br />");
        }String data =motd.toString();
        Log.d(TAG, data);
        Document dat = Jsoup.parse(data);
        dat.select("html").unwrap();
        dat.select("head").unwrap();
        dat.select("body").unwrap();
        dat.select("td").unwrap();
        data = dat.toString();
        Log.d(TAG, data);
        return data;

    }

    private VPlan.VPlanEntry[] readVPlanTable(Elements vPlanTable) {
        List<VPlan.VPlanEntry> entries = new ArrayList<>();
        for (Element line : vPlanTable) {
            Elements cells = line.children();
            VPlan.VPlanEntry row = readVPlanTableCells(cells);
            if (row != null) {
                entries.add(row);
            }
        }
        VPlan.VPlanEntry[] entryArray = new VPlan.VPlanEntry[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            entryArray[i] = entries.get(i);
        }
        return entryArray;
    }

    @Nullable
    private VPlan.VPlanEntry readVPlanTableCells(Elements cells) {
        VPlan.VPlanEntry row = new VPlan.VPlanEntry();
        if (cells.select("th").size() == 0 && cells.size() >= 6) {
            row.subject = cells.get(3).text();
            row.hour = cells.get(1).text();
            row.grade = cells.get(0).text();
            row.message = cells.get(2).text();
            row.room = cells.get(4).text();
            row.omitted = cells.get(5).text().contains("x") ? 0 : 1;
//            row.markedNew = cells.get(0).attr("style").matches("background-color: #00[Ff][Ff]00"));
            return row;
        } else {
            Timber.i(TAG, "Given data contains table head");
            return null;
        }
    }

}
