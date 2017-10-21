package de.jbapps.vplan.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.R;
import de.jbapps.vplan.ui.VPlanBaseData;
import de.jbapps.vplan.ui.VPlanHeader;
import de.jbapps.vplan.ui.VPlanItemData;
import de.jbapps.vplan.ui.VPlanMotd;

public class VPlanAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<VPlanBaseData> mData;

    public VPlanAdapter(Activity context) {
        mInflater = context.getLayoutInflater();
        mData = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        switch (((VPlanBaseData) getItem(position)).type) {
            case MOTD:
                return 0;
            case ITEM:
                return 1;
            case HEADER:
                return 2;
            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    public void setData(List<VPlanBaseData> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VPlanBaseData children = (VPlanBaseData) getItem(position);

        switch (children.type) {
            case MOTD:
                MotdHolder motdholder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.vplan_motditem, null);
                    motdholder = new MotdHolder();
                    motdholder.content = convertView.findViewById(R.id.vplan_motd);
                    convertView.setTag(motdholder);
                } else {
                    motdholder = (MotdHolder) convertView.getTag();
                }
                motdholder.content.setText(((VPlanMotd) children).content);

                break;
            case ITEM:
                VPlanItemData vItemData = (VPlanItemData) children;
                VPlanHolder vplanholder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.vplan_item, null);
                    vplanholder = new VPlanHolder();
                    vplanholder.layout = convertView.findViewById(R.id.item_layout);
                    vplanholder.hour = convertView.findViewById(R.id.vplan_first);
                    vplanholder.grade = convertView.findViewById(R.id.vplan_second);
                    vplanholder.subject = convertView.findViewById(R.id.vplan_third);
                    vplanholder.room = convertView.findViewById(R.id.vplan_fourth);
                    vplanholder.content = convertView.findViewById(R.id.vplan_content);
                    convertView.setTag(vplanholder);
                } else {
                    vplanholder = (VPlanHolder) convertView.getTag();
                }

                vplanholder.layout.setBackgroundResource(vItemData.marked_new ? R.color.material_green_700 : R.drawable.abc_item_background_holo_dark);

                vplanholder.grade.setText(vItemData.grade);
                vplanholder.subject.setText(vItemData.subject);
                vplanholder.hour.setText(vItemData.hour);

                if (vItemData.omitted) {
                    vplanholder.room.setText("entfällt");
                } else {
                    vplanholder.room.setText(vItemData.room);
                }
                if (vItemData.content != null && (vItemData.content.length() > 1)) {
                    vplanholder.content.setText(vItemData.content);
                    vplanholder.content.setVisibility(View.VISIBLE);
                } else {
                    vplanholder.content.setVisibility(View.GONE);
                }

                break;

            case HEADER:
                HeaderHolder headerholder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.vplan_header, null);
                    headerholder = new HeaderHolder();
                    headerholder.title = convertView.findViewById(R.id.vplan_text);
                    headerholder.status = convertView.findViewById(R.id.vplan_status);
                    convertView.setTag(headerholder);
                } else {
                    headerholder = (HeaderHolder) convertView.getTag();
                }
                headerholder.title.setText(((VPlanHeader) children).title);
                headerholder.status.setText(((VPlanHeader) children).status);
                break;
        }
        return convertView;
    }

    private static class VPlanHolder {
        LinearLayout layout;
        TextView grade;
        TextView hour;
        TextView subject;
        TextView room;
        TextView content;
    }

    private static class MotdHolder {
        TextView content;
    }

    private static class HeaderHolder {
        TextView title;
        TextView status;
    }
}