package de.jbamberger.vplan.data;

/**
 * @author Jannik Bamberger
 * @version 1.0
 *          The VPlanDataWrapper contains two VPlanData objects and presents them as a List, so iterating through them is easy
 */
public class VPlanDataWrapper {

    private static final int HEADERCOUNT = 2;
    private static final int FOOTERCOUNT = 1;

    private final VPlanData vPlanData1;
    private final VPlanData vPlanData2;
    private final VPlanFooter vPlanFooter;

    public VPlanDataWrapper(VPlanData vPlanData1, VPlanData vPlanData2) {
        this.vPlanData1 = vPlanData1;
        this.vPlanData2 = vPlanData2;
        vPlanFooter = new VPlanFooter();
    }

    public VPlanElement getItemAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        if (position < 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (position == 0) {
            return vPlanData1.getvPlanHeader();
        } else if (position <= vPlanData1.getVPlanRowCount()) {
            return vPlanData1.getVPlanRowAtPosition(position - 1);
        } else if (position == vPlanData1.getVPlanRowCount() + 1) {
            return vPlanData2.getvPlanHeader();
        } else if (position < length() - 1) {
            return vPlanData2.getVPlanRowAtPosition(position - (vPlanData1.getVPlanRowCount() + 2));
        } else if (position == length() - 1) {//FIXME probably fails
            return vPlanFooter;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public int length() {
        return HEADERCOUNT + vPlanData1.getVPlanRowCount() + vPlanData2.getVPlanRowCount() + FOOTERCOUNT;
    }
}
