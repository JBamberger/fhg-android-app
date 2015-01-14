package de.jbapps.vplan.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VPlan {

    private List<Row> mRows;
    public static final String MOTD = "Nachrichten zum Tag";
    private String motd;
    private String date;

    public VPlan(String jsonString) {

    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Row> getRows() {
        return mRows;
    }

    public void setRows(List<Row> mRows) {
        this.mRows = mRows;
    }

    public VPlan.Row getRowAtPosition(int position) {
        return mRows.get(position);
    }








//##################################################################################################

    class Row  implements IJSONable {
        public String subject;
        public String room;
        public String hour;
        public String content;
        public boolean omitted;

        public Row(String hour, String content, String subject, String room, boolean omitted) {
            this.subject = subject;
            this.room = room;
            this.hour = hour;
            this.content = content;
            this.omitted = omitted;
        }

        @Override
        public JSONObject toJSON() throws JSONException {
            JSONObject data = new JSONObject();
            data.put("omitted", omitted);
            data.put("content", content);
            data.put("hour", hour);
            data.put("room", room);
            data.put("subject", subject);
            return data;
        }
    }


    public interface IJSONable {
        public JSONObject toJSON() throws JSONException;
    }
}
