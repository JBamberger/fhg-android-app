package xyz.jbapps.vplan.data;

public class VPlanElement {

    public static final int TYPE_ROW = 0;
    public static final int TYPE_HEADER = 1;

    public int type;

    public VPlanElement(int type) {
        this.type = type;
    }
}
