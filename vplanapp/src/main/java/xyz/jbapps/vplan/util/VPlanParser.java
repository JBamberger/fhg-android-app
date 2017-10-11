package xyz.jbapps.vplan.util;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.data.VPlanRow;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public final class VPlanParser {

    private static final String TAG = VPlanParser.class.getSimpleName();

    private static final int GRADE_C = 0;
    private static final int HOUR_C = 1;
    private static final int SUBJECT_C = 2;
    private static final int ROOM_C = 3;
    private static final int KIND_C = 4;
    private static final int CONTENT_C = 5;

    @NonNull
    public static VPlanData parse(@NonNull String html, long lastModified) throws ParseException {
        Log.d(TAG, html);

        Document doc = Jsoup.parse(html);

        VPlanData vData = new VPlanData();
        vData.setLastUpdated(lastModified);
        vData.setStatus(readVPlanStatus(html));
        vData.setTitle(readVPlanTitle(doc));
        vData.setMotd(readMotdTable(doc.getElementsByClass("info")));
        vData.setRows(readVPlanTable(doc.getElementsByClass("list").select("tr")));
        return vData;
    }


    @NonNull
    private static String readVPlanStatus(@NonNull String html) throws ParseException {
        try {
            String status = html.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            return status;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Could not parse vplan status", e);
        }
    }

    @NonNull
    private static String readVPlanTitle(@NonNull Document vplanDoc) throws ParseException {
        try {
            return vplanDoc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text();
        } catch (Exception e) {
            throw new ParseException("Could not parse vplan title", e);
        }
    }

    @NonNull
    private static String readMotdTable(@NonNull Elements classInfo) throws ParseException {
        try {
            Elements tableRows = classInfo.select("tr");
            StringBuilder motd = new StringBuilder();
            for (Element line : tableRows) {
                Elements cells = line.children().select("td");
                int size = cells.size();
                if (size == 0) {
                    continue;
                }
                boolean highlightRow = size > 1 && cells.first().text().toLowerCase().contains("unterrichtsfrei");
                if (highlightRow) {
                    motd.append("<font color=#FF5252>");
                }
                for (int i = 0; i < size; i++) {
                    Element cell = cells.get(i);
                    motd.append(cell.toString());
                    if (i < size - 2) {
                        motd.append(" | ");
                    }
                }
                if (highlightRow) {
                    motd.append("</font>");
                }
                motd.append("<br />");
            }
            String data = motd.toString();
            Log.d(TAG, data);
            Document dat = Jsoup.parse(data);
            dat.select("html").unwrap();
            dat.select("head").unwrap();
            dat.select("body").unwrap();
            dat.select("td").unwrap();
            data = dat.toString();
            Log.d(TAG, data);
            return data;
        } catch (Exception e) {
            throw new ParseException("Could not parse motd table", e);
        }
    }

    @NonNull
    private static List<VPlanRow> readVPlanTable(@NonNull Elements vPlanTable) throws ParseException {
        try {
            List<VPlanRow> rows = new ArrayList<>();
            for (Element line : vPlanTable) {
                Elements cells = line.children();
                if(cells.select("th").size() == 0) {
                    rows.add(readVPlanTableCells(cells));
                }
            }
            return rows;
        } catch (Exception e) {
            throw new ParseException("Could not parse vplan table", e);
        }
    }

    @NonNull
    private static VPlanRow readVPlanTableCells(Elements cells) throws ParseException {
        try {
            if (cells.size() >= 6) {
                VPlanRow row = new VPlanRow();

                Elements grade = cells.get(GRADE_C).getElementsByTag("span");
                Elements hour = cells.get(HOUR_C).getElementsByTag("span");
                Elements subject = cells.get(SUBJECT_C).getElementsByTag("span");
                Elements room = cells.get(ROOM_C).getElementsByTag("span");
                Elements kind = cells.get(KIND_C).getElementsByTag("span");
                Elements content = cells.get(CONTENT_C).getElementsByTag("span");

                row.setGrade(grade != null && grade.first() != null
                        ? grade.first().html()
                        : cells.get(GRADE_C).text());
                row.setHour(hour != null && hour.first() != null
                        ? hour.first().html()
                        : cells.get(HOUR_C).text());
                row.setContent(content != null && content.first() != null
                        ? content.first().html()
                        : cells.get(CONTENT_C).text());
                row.setSubject(subject != null && subject.first() != null
                        ? subject.first().html()
                        : cells.get(SUBJECT_C).text());
                row.setRoom(room != null && room.first() != null
                        ? room.first().html()
                        : cells.get(ROOM_C).text());
                row.setKind(kind != null && kind.first() != null
                        ? kind.first().html()
                        : cells.get(KIND_C).text());
                row.setOmitted(cells.get(KIND_C).text().toLowerCase().contains("entfall"));
                row.setMarkedNew(cells.get(GRADE_C).attr("style").matches("background-color: #00[Ff][Ff]00"));

                return row;
            }
            throw new ParseException("Could not parse row, invalid format.");
        } catch (Exception e) {
            throw new ParseException("Could not parse vplan row", e);
        }
    }
}
