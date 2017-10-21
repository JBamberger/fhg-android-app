package de.fhg_radolfzell.android_app.main.vplan;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.data.VPlan;
import de.fhg_radolfzell.android_app.view.DataBindingBaseAdapter;

/**
 * @author Jannik
 * @version 29.07.2016.
 */
public class VPlanAdapter extends DataBindingBaseAdapter {

    private VPlanDataWrapper vPlanDataWrapper;

    public VPlanAdapter() {
        vPlanDataWrapper = new VPlanDataWrapper();

    }

    public void setData(VPlan[] vplans) {
        vPlanDataWrapper.setData(vplans);
        notifyDataSetChanged();
    }

    @Override
    protected Object getObjForPosition(int position) {
        return vPlanDataWrapper.getItemAtPosition(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        switch (vPlanDataWrapper.getItemTypeAtPosition(position)) {
            case VPlanDataWrapper.TYPE_FOOTER: return R.layout.vplan_footer;
            case VPlanDataWrapper.TYPE_HEADER: return R.layout.vplan_header;
            case VPlanDataWrapper.TYPE_ROW: return R.layout.vplan_item;
            default:
        }
        return vPlanDataWrapper.getItemTypeAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return vPlanDataWrapper.length();
    }
}
