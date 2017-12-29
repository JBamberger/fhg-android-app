package de.jbamberger.fhgapp.ui.vplan;

import android.support.annotation.NonNull;

import java.util.List;

import de.jbamberger.api.data.VPlan;
import de.jbamberger.api.data.VPlanDay;
import de.jbamberger.api.data.VPlanRow;
import de.jbamberger.fhgapp.R;
import de.jbamberger.fhgapp.ui.components.DataBindingBaseAdapter;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class VPlanAdapter extends DataBindingBaseAdapter {

    private final List<VPlanRow> rows1;
    private final List<VPlanRow> rows2;
    private final VPlanHeader header1;
    private final VPlanHeader header2;
    private final int bound1;
    private final int bound2;


    VPlanAdapter(@NonNull VPlan vPlan) {
        VPlanDay day1 = vPlan.getDay1();
        VPlanDay day2 = vPlan.getDay2();

        rows1 = day1.getVPlanRows();
        rows2 = day2.getVPlanRows();
        bound1 = rows1.size() + 1;
        bound2 = bound1 + rows2.size() + 1;
        header1 = new VPlanHeader(day1.getDateAndDay(), day1.getLastUpdated(), day1.getMotd());
        header2 = new VPlanHeader(day2.getDateAndDay(), day2.getLastUpdated(), day2.getMotd());
    }

    @Override
    protected Object getObjForPosition(int position) {
        if (position == 0) {
            return header1;
        } else if (0 < position && position < bound1) {
            return rows1.get(position - 1);
        } else if (position == bound1) {
            return header2;
        } else if (bound1 < position && position < bound2) {
            return rows2.get(position - bound1 - 1);
        } else if (position == bound2) {
            return null; // footer
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    protected Object getListenerForPosition(int position) {
        return null;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if (position == 0 || position == bound1) {
            return R.layout.vplan_header;
        } else if ((0 < position && position < bound1)
                || (bound1 < position && position < bound2)) {
            return R.layout.vplan_item;
        } else if (position == bound2) {
            return R.layout.vplan_footer;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    public int getItemCount() {
        return bound2;
    }
}
