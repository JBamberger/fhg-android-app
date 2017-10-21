package de.fhg_radolfzell.android_app.main.calendar;

import de.fhg_radolfzell.android_app.data.VPlan;

/**
 * @author Jannik
 * @version 30.07.2016.
 */
public class CalendarDataWrapper {

    public static final int TYPE_ROW = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;

    private VPlanElement[] elements;

    public void setData(VPlan[] vplans) {
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

        public long lastUpdated;
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
