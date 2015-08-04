package xyz.jbapps.vplanapp.ui;

import org.json.JSONException;
import org.json.JSONObject;

public class VPlanHeader extends VPlanBaseData {

    public String title;
    public String status;

    public VPlanHeader(String title, String status) {
        super(Type.HEADER);
        this.title = title;
        this.status = status;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("title", title);
        data.put("status", status);
        return data;
    }
}
