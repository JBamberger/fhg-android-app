package de.fhg_radolfzell.android_app.main.calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.R;
import de.fhg_radolfzell.android_app.event.CalendarLoadedEvent;
import de.fhg_radolfzell.android_app.event.CalendarLoadingFailedEvent;
import de.fhg_radolfzell.android_app.view.BaseFragment;
import de.fhg_radolfzell.android_app.main.MainActivity;
import timber.log.Timber;

/**
 * @author Jannik
 * @version 05.08.2016.
 */
public class CalendarFragment extends BaseFragment {

    private static final String TAG = "CalendarFragment";
    CalendarAdapter adapter;
    @Inject
    CalendarInteractor interactor;
    private CalendarComponent calendarComponent;

    public CalendarComponent getCalendarComponent() {
        return calendarComponent;
    }

    public void setCalendarComponent(CalendarComponent calendarComponent) {
        this.calendarComponent = calendarComponent;
    }

    @Override
    public void onDestroy() {
        calendarComponent = null;
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        calendarComponent = ((MainActivity) getActivity()).getMainComponent().newCalendarComponent(new CalendarModule(this));
        calendarComponent.inject(this);
        adapter = new CalendarAdapter(getActivity());
//        eventBus.post(new UpdateMainSubTitleEvent(getString()));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setAdapter(adapter);
        update();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.calendar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calendar_export_ics:
//                interactor.getICALCalendar();// TODO: 11.09.2016
                break;
            case R.id.action_calendar_export_xml:
//                interactor.getXMLCalendar();// TODO: 11.09.2016
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Subscribe
    public void calendarLoadingFailed(CalendarLoadingFailedEvent event) {
        Timber.d("calendarLoadingFailed: loading failed");
        showLoadingIndicator(false);
        Toast.makeText(context, R.string.calendar_loading_failed, Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void calendarLoaded(CalendarLoadedEvent event) {
        Timber.d("Received Calendar update.");
        showLoadingIndicator(false);
        adapter.setData(event.events);
        adapter.notifyDataSetChanged();
//        eventBus.post(new UpdateMainSubTitleEvent(getGradeString()));
    }


    public void update() {
        Timber.d("Invoked Calendar update.");
        showLoadingIndicator(true);
        interactor.getCalendar();
    }
}
