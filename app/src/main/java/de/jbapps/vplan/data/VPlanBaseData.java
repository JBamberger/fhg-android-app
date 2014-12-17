package de.jbapps.vplan.data;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class VPlanBaseData {

    public Type type;

    public VPlanBaseData(Type type) {
        this.type = type;
    }

    public abstract JSONObject toJSON() throws JSONException;

    public enum Type {ITEM, MOTD, HEADER}
}
