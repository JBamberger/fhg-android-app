package de.jbapps.vplan.ui;

import org.json.JSONException;
import org.json.JSONObject;

public class VPlanItemData extends VPlanBaseData {

    public String grade;
    public String subject;
    public String room;
    public String hour;
    public String content;
    public boolean omitted;
    public boolean marked_new;


    public VPlanItemData(String grade, String hour, String content, String subject, String room, boolean omitted, boolean marked_new) {
        super(Type.ITEM);
        this.grade = grade;
        this.subject = subject;
        this.room = room;
        this.hour = hour;
        this.content = content;
        this.omitted = omitted;
        this.marked_new = marked_new;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("grade", grade);
        data.put("type", type);
        data.put("omitted", omitted);
        data.put("content", content);
        data.put("hour", hour);
        data.put("room", room);
        data.put("subject", subject);
        data.put("marked_new", marked_new);
        return data;
    }
}
