package de.jbapps.vplan.util;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.jbapps.vplan.R;
import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.data.VPlanHeader;
import de.jbapps.vplan.data.VPlanItemData;
import de.jbapps.vplan.data.VPlanMotd;

public class VPlanAdapter extends BaseAdapter {

    public LayoutInflater mInflater;
    private List<VPlanBaseData> mData;

    public VPlanAdapter(Activity context) {
        mInflater = context.getLayoutInflater();
        mData = new ArrayList<VPlanBaseData>();
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
                    motdholder.content = (TextView) convertView.findViewById(R.id.vplan_motd);
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
                    vplanholder.subject = (TextView) convertView.findViewById(R.id.vplan_subject);
                    vplanholder.room = (TextView) convertView.findViewById(R.id.vplan_room);
                    vplanholder.hour = (TextView) convertView.findViewById(R.id.vplan_hour);
                    vplanholder.content = (TextView) convertView.findViewById(R.id.vplan_content);
                    convertView.setTag(vplanholder);
                } else {
                    vplanholder = (VPlanHolder) convertView.getTag();
                }

                vplanholder.subject.setText(vItemData.subject);
                vplanholder.hour.setText(vItemData.hour);

                if (vItemData.omitted) {
                    vplanholder.room.setText("entfällt");
                } else {
                    vplanholder.room.setText(vItemData.room);
                }
                Log.d("", "#" + vItemData.content + "#");
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
                    headerholder.title = (TextView) convertView.findViewById(R.id.vplan_text);
                    convertView.setTag(headerholder);
                } else {
                    headerholder = (HeaderHolder) convertView.getTag();
                }
                headerholder.title.setText(((VPlanHeader) children).title);
                break;
        }
        return convertView;
    }

    private static class VPlanHolder {
        TextView subject;
        TextView room;
        TextView hour;
        TextView content;
    }

    private static class MotdHolder {
        TextView content;
    }

    private static class HeaderHolder {
        TextView title;
    }
}