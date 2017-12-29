package de.jbamberger.api;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jbamberger.api.data.VPlanDay;
import de.jbamberger.api.data.VPlanRow;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import timber.log.Timber;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
final class VPlanParser implements Converter<ResponseBody, VPlanDay> {

    private static final int GRADE_C = 0;
    private static final int HOUR_C = 1;
    private static final int SUBJECT_C = 2;
    private static final int ROOM_C = 3;
    private static final int KIND_C = 4;
    private static final int CONTENT_C = 5;

    @NonNull
    public VPlanDay convert(@NonNull ResponseBody body) throws IOException {
        byte[] data = body.bytes();
        Document doc = null;
        String html = null;
        MediaType type = body.contentType();
        Charset charset = type != null ? type.charset() : null;
        if (charset != null) {
            Timber.i("Using header charset '%s'", charset.name());
            html = new String(data, charset);
            doc = Jsoup.parse(html);
        } else {
            String tmpHtml = new String(data);
            Document tmpDoc = Jsoup.parse(tmpHtml);
            Element head = tmpDoc.getElementsByTag("head").first();
            if (head != null) {
                Element contentType = head.getElementsByAttributeValue("http-equiv", "Content-Type").first();
                if (contentType != null && contentType.hasAttr("content")) {
                    String contentAttr = contentType.attr("content");

                    try {
                        Matcher matcher = Pattern.compile("\\s*([^;]+)(;\\s*)?")
                                .matcher(contentAttr);
                        Pattern charsetPattern = Pattern.compile("^charset=([^\\s]*)$");

                        while (matcher.find()) {
                            Matcher m = charsetPattern.matcher(matcher.group(1));
                            if (m.find()) {
                                charset = Charset.forName(m.group(1));
                                break;
                            }
                        }
                        if (charset != null) {
                            Timber.i("Using html charset '%s'", charset.name());
                            html = new String(data, charset);
                            doc = Jsoup.parse(html);
                        }

                    } catch (IllegalCharsetNameException | UnsupportedCharsetException |
                            IllegalStateException e) {
                        //no match or illegal charset
                        Timber.w(e, "charset extraction failed.");
                    }
                }
            }
            if (doc == null) {
                doc = tmpDoc;
            }
            if (html == null) {
                html = tmpHtml;
            }
        }


        String dateAndDay = readVPlanStatus(html);
        String lastUpdated = readVPlanTitle(doc);
        String motd = readMotdTable(doc.getElementsByClass("info"));
        List<VPlanRow> entries = readVPlanTable(doc.getElementsByClass("list").select("tr"));

        return new VPlanDay(dateAndDay, lastUpdated, motd, entries);
    }


    @NonNull
    private static String readVPlanStatus(@NonNull String html) throws ParseException {
        try {
            String status = html.split("</head>")[1];
            status = status.split("<p>")[0].replace("\n", "").replace("\r", "");
            return status;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Could not parse vplan lastUpdated", e);
        }
    }

    @NonNull
    private static String readVPlanTitle(@NonNull Document vplanDoc) throws ParseException {
        try {
            return vplanDoc.getElementsByClass("mon_title").get(0).getAllElements().get(0).text();
        } catch (Exception e) {
            throw new ParseException("Could not parse vplan dateAndDay", e);
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
            Document dat = Jsoup.parse(data);
            dat.select("html").unwrap();
            dat.select("head").unwrap();
            dat.select("body").unwrap();
            dat.select("td").unwrap();
            data = dat.toString();
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
                if (cells.select("th").size() == 0) {
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
                Elements grade = cells.get(GRADE_C).getElementsByTag("span");
                Elements hour = cells.get(HOUR_C).getElementsByTag("span");
                Elements subject = cells.get(SUBJECT_C).getElementsByTag("span");
                Elements room = cells.get(ROOM_C).getElementsByTag("span");
                Elements kind = cells.get(KIND_C).getElementsByTag("span");
                Elements content = cells.get(CONTENT_C).getElementsByTag("span");

                String valGrade = grade != null && grade.first() != null
                        ? grade.first().html()
                        : cells.get(GRADE_C).text();
                String valHour = hour != null && hour.first() != null
                        ? hour.first().html()
                        : cells.get(HOUR_C).text();
                String valContent = content != null && content.first() != null
                        ? content.first().html()
                        : cells.get(CONTENT_C).text();
                String valSubject = subject != null && subject.first() != null
                        ? subject.first().html()
                        : cells.get(SUBJECT_C).text();
                String valRoom = room != null && room.first() != null
                        ? room.first().html()
                        : cells.get(ROOM_C).text();
                String valKind = kind != null && kind.first() != null
                        ? kind.first().html()
                        : cells.get(KIND_C).text();
                boolean valOmitted = cells.get(KIND_C).text().toLowerCase().contains("entfall");
                boolean valMarkedNew = cells.get(GRADE_C).attr("style").matches("background-color: #00[Ff][Ff]00");

                return new VPlanRow(valSubject, valOmitted, valHour, valRoom, valContent,
                        valGrade, valKind, valMarkedNew);
            }
            throw new ParseException("Could not parse row, invalid format.");
        } catch (Exception e) {
            throw new ParseException("Could not parse vplan row", e);
        }
    }
}
