package xyz.jbapps.vplanapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.jbapps.jutils.ViewUtils;
import xyz.jbapps.vplanapp.R;
import xyz.jbapps.vplanapp.data.VPlanData;
import xyz.jbapps.vplanapp.data.VPlanDataWrapper;
import xyz.jbapps.vplanapp.data.VPlanElement;
import xyz.jbapps.vplanapp.data.VPlanRow;

/**
 * @author Jannik Bamberger
 * @version 1.0
 */
public class MultiVPlanAdapter extends RecyclerView.Adapter {

    private VPlanDataWrapper vPlanDataWrapper;

    public MultiVPlanAdapter() {
        vPlanDataWrapper = new VPlanDataWrapper(new VPlanData(), new VPlanData());

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
                xyz.jbapps.vplanapp.data.VPlanHeader header = (xyz.jbapps.vplanapp.data.VPlanHeader)
                        vPlanDataWrapper.getItemAtPosition(position);
                VPlanHeaderViewHolder headHolder = (VPlanHeaderViewHolder) holder;
                headHolder.title.setText(header.getTitle());
                headHolder.status.setText(header.getStatus());
                headHolder.motdHeader.setText("Nachrichten zum Tag");//TODO: auslagern
                headHolder.motdContent.setText(header.getMotd());
                break;
            case VPlanElement.TYPE_ROW:
                VPlanRow row = (VPlanRow) vPlanDataWrapper.getItemAtPosition(position);
                VPlanRowViewHolder rowHolder = (VPlanRowViewHolder) holder;
                rowHolder.grade.setText(row.getGrade());
                rowHolder.content.setText(row.getContent());
                rowHolder.hour.setText(row.getHour());
                rowHolder.subject.setText(row.getSubject());
                rowHolder.roomOmitted.setText(row.getOmitted() ? "X" : row.getRoom()); //TODO: auslagern
                rowHolder.marked_new.setBackgroundResource(row.getMarkedNew() ? R.color.material_green_700
                        : R.drawable.abc_item_background_holo_dark);//TODO: fix colors
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
        public TextView grade;
        public TextView hour;
        public TextView subject;
        public TextView roomOmitted;
        public TextView content;
        public LinearLayout marked_new;

        public VPlanRowViewHolder(View itemView) {
            super(itemView);
            grade = ViewUtils.findViewById(itemView, R.id.vplan_first);
            hour = ViewUtils.findViewById(itemView, R.id.vplan_second);
            subject = ViewUtils.findViewById(itemView, R.id.vplan_third);
            roomOmitted = ViewUtils.findViewById(itemView, R.id.vplan_fourth);
            content = ViewUtils.findViewById(itemView, R.id.vplan_content);
            marked_new = ViewUtils.findViewById(itemView, R.id.item_layout);
        }

    }

    public class VPlanHeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView status;
        public TextView title;
        public TextView motdHeader;
        public TextView motdContent;

        public VPlanHeaderViewHolder(View itemView) {
            super(itemView);
            status = ViewUtils.findViewById(itemView, R.id.vplan_status);
            title = ViewUtils.findViewById(itemView, R.id.vplan_title);
            motdHeader = ViewUtils.findViewById(itemView, R.id.vplan_motd_header);
            motdContent = ViewUtils.findViewById(itemView, R.id.vplan_motd_content);
        }
    }
}
