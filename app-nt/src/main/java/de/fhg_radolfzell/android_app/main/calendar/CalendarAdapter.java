package de.fhg_radolfzell.android_app.main.calendar;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.databinding.CalendarItemBinding;
import de.fhg_radolfzell.android_app.data.CalendarEvent;
import de.fhg_radolfzell.android_app.util.TimeFormatter;
import timber.log.Timber;

public class CalendarAdapter extends RecyclerView.Adapter {

    private static final String TAG = "CalendarAdapter";
    private static final String PATTERN_FULL_DAY = "....-..-.. 00:00:00";
    private Activity activity;
    private SimpleDateFormat formatter;
    private TimeFormatter timeFormatter;

    private CalendarEvent[] calendarEvents;

    @Inject
    public CalendarAdapter(Activity activity) {
        this.activity = activity;
        timeFormatter = new TimeFormatter(activity.getApplicationContext());
    }

    public void setData(CalendarEvent[] calendarEvents) {
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }
        Arrays.sort(calendarEvents, new Comparator<CalendarEvent>() {
            @Override
            public int compare(CalendarEvent e1, CalendarEvent e2) {
                return e1.getStartDateObject().compareTo(e2.getStartDateObject());
            }
        });
        this.calendarEvents = calendarEvents;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((CalendarItemBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.calendar_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            CalendarEvent event = calendarEvents[position];
            viewHolder.binding.setListener(this);
            viewHolder.binding.setEvent(event);
            viewHolder.binding.setTimeFormatter(timeFormatter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return calendarEvents == null ? 0 : calendarEvents.length;
    }

    public void onClickOpenUrl(String url) {
        Timber.d("onClickOpenUrl() called with: link = [%s]", url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.setToolbarColor(activity.getResources().getColor(R.color.brand_primary)).build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }

    public void onClickSaveEvent(CalendarEvent event) {
        Timber.d("onClickSaveEvent() called with: event = [%s]", event);
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());

        if (event.getStartDate() == null) {
            Toast.makeText(activity, R.string.vplan_loading_failed, Toast.LENGTH_LONG).show();// TODO: 14.08.2016 correct string
            return;
        } else {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartDateObject().toDateTime().getMillis());
        }
        if (TimeFormatter.isCalendarEventAllDay(event)) {
            intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        }
        if (event.getEndDate() != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndDateObject().toDateTime().getMillis());
        }


        intent.setData(CalendarContract.Events.CONTENT_URI);
        activity.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CalendarItemBinding binding;

        public ViewHolder(CalendarItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
