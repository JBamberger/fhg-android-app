package xyz.jbapps.vplanapp.data;

/**
 * @author Jannik Bamberger
 * @version 1.0
 *          The VPlanDataWrapper contains two VPlanData objects and presents them as a List, so they can be
 *          used easily inside an ListAdapter
 */
public class VPlanDataWrapper {

    private VPlanData vPlanData1;
    private VPlanData vPlanData2;

    public VPlanDataWrapper(VPlanData vPlanData1, VPlanData vPlanData2) {
        this.vPlanData1 = vPlanData1;
        this.vPlanData2 = vPlanData2;
    }

    public VPlanElement getItemAtPosition(int position) throws ArrayIndexOutOfBoundsException {
        if (position < 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (position == 0) {
            return vPlanData1.getvPlanHeader();
        } else if (position < vPlanData1.getVPlanRowCount()) {
            return vPlanData1.getVPlanRowAtPosition(position - 1);
        } else if (position == vPlanData1.getVPlanRowCount() + 1) {
            return vPlanData2.getvPlanHeader();
        } else if (position < length()) {
            return vPlanData2.getVPlanRowAtPosition(position - (vPlanData1.getVPlanRowCount() + 2));
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public int length() {
        return 2 + vPlanData1.getVPlanRowCount() + vPlanData2.getVPlanRowCount();
    }
}
