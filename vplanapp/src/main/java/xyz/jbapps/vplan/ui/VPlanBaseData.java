package xyz.jbapps.vplan.ui;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class VPlanBaseData {

    public final Type type;

    public VPlanBaseData(Type type) {
        this.type = type;
    }

    public abstract JSONObject toJSON() throws JSONException;

    public enum Type {ITEM, MOTD, HEADER}
}
