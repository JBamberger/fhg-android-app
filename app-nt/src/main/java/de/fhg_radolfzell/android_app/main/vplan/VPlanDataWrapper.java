package de.fhg_radolfzell.android_app.main.vplan;

import de.fhg_radolfzell.android_app.data.VPlan;

/**
 * @author Jannik
 * @version 30.07.2016.
 */
public class VPlanDataWrapper {

    public static final int TYPE_ROW = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;

    private VPlanElement[] elements;

    public void setData(VPlan[] vplans) {
        if (vplans == null) {
            elements = new VPlanElement[1];
            elements[0] = new VPlanFooter();
            return;
        }
        int length = 1; // footer
        for (VPlan vplan : vplans) {
            length += 1 + (vplan.entries == null ? 0 : vplan.entries.length); //header and items
        }
        elements = new VPlanElement[length];
        int index = 0;
        for (VPlan vplan : vplans) {
            VPlanHeader h = new VPlanHeader();
            h.motd = vplan.motd;
            h.status = vplan.updatedAt;
            h.title = vplan.dateAt;
            h.lastUpdated = vplan.updatedAt;
            elements[index] = h;
            index++;
            if (vplan.entries != null) {
                for (VPlan.VPlanEntry entry : vplan.entries) {
                    VPlanRow r = new VPlanRow();
                    r.grade = entry.grade;
                    r.hour = entry.hour;
                    r.message = entry.message;
                    r.omitted = entry.omitted;
                    r.subject = entry.subject;
                    r.room = entry.room;
                    elements[index] = r;
                    index++;
                }
            }
            elements[index] = new VPlanFooter();
        }
    }

    public int getItemTypeAtPosition(int position) {
        return elements[position].type;
    }

    public VPlanElement getItemAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        return elements[position];
    }

    public int length() {
        return elements == null ? 0 : elements.length;
    }

    public class VPlanElement {
        public static final int TYPE_ROW = 0;
        public static final int TYPE_HEADER = 1;
        public static final int TYPE_FOOTER = 2;

        public final int type;

        public VPlanElement(int type) {
            this.type = type;
        }
    }

    public class VPlanFooter extends VPlanElement {

        public VPlanFooter() {
            super(VPlanElement.TYPE_FOOTER);
        }
    }

    public class VPlanHeader extends VPlanElement {

        public String lastUpdated;
        public String title;
        public String status;
        public String motd;

        public VPlanHeader() {
            super(VPlanElement.TYPE_HEADER);
        }
    }

    public class VPlanRow extends VPlanElement {

        public String subject;
        public int omitted;
        public String hour;
        public String room;
        public String message;
        public String grade;
        //public boolean marked_new;

        public VPlanRow() {
            super(VPlanElement.TYPE_ROW);
        }
    }


}
