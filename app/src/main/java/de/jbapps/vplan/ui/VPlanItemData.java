package de.jbapps.vplan.ui;

import org.json.JSONException;
import org.json.JSONObject;

public class VPlanItemData extends VPlanBaseData {

    public String subject;
    public String room;
    public String hour;
    public String content;
    public boolean omitted;


    public VPlanItemData(String hour, String content, String subject, String room, boolean omitted) {
        super(Type.ITEM);
        this.subject = subject;
        this.room = room;
        this.hour = hour;
        this.content = content;
        this.omitted = omitted;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("omitted", omitted);
        data.put("content", content);
        data.put("hour", hour);
        data.put("room", room);
        data.put("subject", subject);
        return data;
    }
}
