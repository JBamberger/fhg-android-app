package de.jbapps.vplan.data;

import org.json.JSONException;
import org.json.JSONObject;

public class VPlanHeader extends VPlanBaseData {

    public String title;

    public VPlanHeader(String title) {
        super(Type.HEADER);
        this.title = title;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("title", title);
        return data;
    }
}
