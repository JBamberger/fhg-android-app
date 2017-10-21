package de.fhg_radolfzell.android_app.main.calendar;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.data.source.FhgApiInterface;
import de.fhg_radolfzell.android_app.data.source.FhgWebInterface;
import de.fhg_radolfzell.android_app.event.CalendarLoadedEvent;
import de.fhg_radolfzell.android_app.event.CalendarLoadingFailedEvent;
import de.fhg_radolfzell.android_app.data.CalendarEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CalendarInteractorImpl implements CalendarInteractor {

    private static final String TAG = "FeedInteractorImpl";

    private final Bus eventBus;
    private final FhgWebInterface web;
    private final FhgApiInterface api;

    @Inject
    public CalendarInteractorImpl(Bus eventBus, FhgWebInterface web, FhgApiInterface api) {
        this.eventBus = eventBus;
        this.web = web;
        this.api = api;
    }

    private void failedLoading() {
        eventBus.post(new CalendarLoadingFailedEvent());
    }

    @Override
    public void getCalendar() {
        api.getCalendar()
                .enqueue(new Callback<CalendarEvent[]>() {
                    @Override
                    public void onResponse(Call<CalendarEvent[]> call, Response<CalendarEvent[]> response) {
                        if (response.isSuccessful()) {
                            Timber.d("onResponse: Calendar loaded");
                            eventBus.post(new CalendarLoadedEvent(response.body()));
                        } else {
                            failedLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<CalendarEvent[]> call, Throwable t) {
                        Timber.e(t, "onFailure: Calendar loading failed");
                        failedLoading();
                    }
                });
    }

    @Override
    public void getICALCalendar() {
        throw new RuntimeException("Implementation missing!");// TODO: 09.08.2016 Implement
    }

    @Override
    public void getXMLCalendar() {
        throw new RuntimeException("Implementation missing!");// TODO: 09.08.2016 Implement
    }
}
