package de.jbapps.vplan.ui;

import org.json.JSONException;
import org.json.JSONObject;

public class VPlanMotd extends VPlanBaseData {

    public String content;

    public VPlanMotd(String content) {
        super(Type.MOTD);
        this.content = content;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("content", content);
        return data;
    }
}
