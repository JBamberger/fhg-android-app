package de.jbamberger.vplan.data;

public class VPlanElement {

    public static final int TYPE_ROW = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;

    public final int type;

    public VPlanElement(int type) {
        this.type = type;
    }
}
