package xyz.jbapps.vplan.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplan.R;
import xyz.jbapps.vplan.data.VPlanData;
import xyz.jbapps.vplan.data.VPlanDataWrapper;
import xyz.jbapps.vplan.data.VPlanElement;
import xyz.jbapps.vplan.data.VPlanRow;

/**
 * @author Jannik Bamberger
 * @version 1.0
 */
public class MultiVPlanAdapter extends RecyclerView.Adapter {

    private VPlanDataWrapper vPlanDataWrapper;
    private final Context context;

    public MultiVPlanAdapter(Context context) {
        vPlanDataWrapper = new VPlanDataWrapper(new VPlanData(), new VPlanData());
        this.context = context;

    }

    public void setData(VPlanData vPlanData1, VPlanData vPlanData2) {
        vPlanDataWrapper = new VPlanDataWrapper(vPlanData1, vPlanData2);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VPlanElement.TYPE_HEADER:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.vplan_header, parent, false);
                return new VPlanHeaderViewHolder(v0);
            case VPlanElement.TYPE_ROW:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.vplan_item, parent, false);
                return new VPlanRowViewHolder(v1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (vPlanDataWrapper.getItemAtPosition(position).type) {
            case VPlanElement.TYPE_HEADER:
                xyz.jbapps.vplan.data.VPlanHeader header = (xyz.jbapps.vplan.data.VPlanHeader)
                        vPlanDataWrapper.getItemAtPosition(position);
                VPlanHeaderViewHolder headHolder = (VPlanHeaderViewHolder) holder;
                headHolder.headlayout.setBackgroundResource(R.color.vplan_header);
                headHolder.title.setText(header.getTitle());
                headHolder.status.setText(header.getStatus());
                if (!header.getMotd().isEmpty()) {
                    headHolder.motdHeader.setText(context.getString(R.string.text_vplan_motd));
                }
                headHolder.motdContent.setText(header.getMotd());
                break;
            case VPlanElement.TYPE_ROW:
                VPlanRow row = (VPlanRow) vPlanDataWrapper.getItemAtPosition(position);
                VPlanRowViewHolder rowHolder = (VPlanRowViewHolder) holder;
                rowHolder.grade.setText(row.getGrade());
                rowHolder.content.setText(row.getContent());
                rowHolder.hour.setText(row.getHour());
                rowHolder.subject.setText(row.getSubject());
                rowHolder.background.setBackgroundResource(((position % 2) == 1) ? R.color.vplan_even : R.color.vplan_odd);
                if (row.getOmitted()) {
                    rowHolder.roomOmitted.setText(context.getString(R.string.text_vplan_omitted));
                    rowHolder.background.setBackgroundResource(R.color.vplan_omitted);
                } else {
                    rowHolder.roomOmitted.setText(row.getRoom());
                }
                if (row.getMarkedNew()) {
                    rowHolder.background.setBackgroundResource(R.color.vplan_new);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return vPlanDataWrapper.getItemAtPosition(position).type;
    }

    @Override
    public int getItemCount() {
        return vPlanDataWrapper.length();
    }

    public static class VPlanRowViewHolder extends RecyclerView.ViewHolder {
        public final TextView grade;
        public final TextView hour;
        public final TextView subject;
        public final TextView roomOmitted;
        public final TextView content;
        public final LinearLayout background;

        public VPlanRowViewHolder(View itemView) {
            super(itemView);
            grade = ViewUtils.findViewById(itemView, R.id.vplan_first);
            hour = ViewUtils.findViewById(itemView, R.id.vplan_second);
            subject = ViewUtils.findViewById(itemView, R.id.vplan_third);
            roomOmitted = ViewUtils.findViewById(itemView, R.id.vplan_fourth);
            content = ViewUtils.findViewById(itemView, R.id.vplan_content);
            background = ViewUtils.findViewById(itemView, R.id.item_layout);
        }

    }

    public class VPlanHeaderViewHolder extends RecyclerView.ViewHolder {

        public final TextView status;
        public final TextView title;
        public final TextView motdHeader;
        public final TextView motdContent;
        public final RelativeLayout headlayout;

        public VPlanHeaderViewHolder(View itemView) {
            super(itemView);
            headlayout = ViewUtils.findViewById(itemView, R.id.header_layout);
            status = ViewUtils.findViewById(itemView, R.id.vplan_status);
            title = ViewUtils.findViewById(itemView, R.id.vplan_title);
            motdHeader = ViewUtils.findViewById(itemView, R.id.vplan_motd_header);
            motdContent = ViewUtils.findViewById(itemView, R.id.vplan_motd_content);
        }
    }
}
