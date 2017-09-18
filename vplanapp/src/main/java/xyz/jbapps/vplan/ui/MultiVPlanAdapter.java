package xyz.jbapps.vplan.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xml.sax.XMLReader;

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
    private final StrikeTagHandler tagHandler;

    public MultiVPlanAdapter(Context context) {
        vPlanDataWrapper = new VPlanDataWrapper(new VPlanData(), new VPlanData());
        tagHandler = new StrikeTagHandler();
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
            case VPlanElement.TYPE_FOOTER:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.vplan_footer, parent, false);
                return new VPlanRowViewHolder(v2);
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
                headHolder.motdContent.setText(Html.fromHtml(header.getMotd()));
                break;
            case VPlanElement.TYPE_ROW:

                VPlanRow row = (VPlanRow) vPlanDataWrapper.getItemAtPosition(position);
                VPlanRowViewHolder rowHolder = (VPlanRowViewHolder) holder;
                rowHolder.grade.setText(Html.fromHtml(row.getGrade(), null, tagHandler));
                rowHolder.content.setText(Html.fromHtml(row.getContent(), null, tagHandler));
                rowHolder.hour.setText(Html.fromHtml(row.getHour(), null, tagHandler));
                rowHolder.subject.setText(Html.fromHtml(row.getSubject(), null, tagHandler));
                rowHolder.background.setBackgroundResource(R.color.transparent);
                if (row.getOmitted()) {
                    rowHolder.roomOmitted.setTextColor(context.getResources().getColor(R.color.material_red_A400));
                    rowHolder.roomOmitted.setText(context.getString(R.string.text_vplan_omitted));
                    rowHolder.background.setBackgroundResource(R.color.vplan_omitted);
                } else {
                    rowHolder.roomOmitted.setTextColor(context.getResources().getColor(R.color.material_text_54));
                    rowHolder.roomOmitted.setText(Html.fromHtml(row.getRoom(), null, tagHandler));
                }
                if (row.getMarkedNew()) {
                    rowHolder.background.setBackgroundResource(R.color.vplan_new);
                }
                break;
            case VPlanElement.TYPE_FOOTER:
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


    public class StrikeTagHandler implements Html.TagHandler {

        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equalsIgnoreCase("strike") || tag.equals("s")) {
                processStrike(opening, output);
            }
        }

        private void processStrike(boolean opening, Editable output) {
            int len = output.length();
            if (opening) {
                output.setSpan(new StrikethroughSpan(), len, len, Spannable.SPAN_MARK_MARK);
            } else {
                Object obj = getLast(output, StrikethroughSpan.class);
                int where = output.getSpanStart(obj);

                output.removeSpan(obj);

                if (where != len) {
                    output.setSpan(new StrikethroughSpan(), where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        private Object getLast(Editable text, Class kind) {
            Object[] objs = text.getSpans(0, text.length(), kind);

            if (objs.length == 0) {
                return null;
            } else {
                for (int i = objs.length; i > 0; i--) {
                    if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                        return objs[i - 1];
                    }
                }
                return null;
            }
        }


    }
}
