package de.fhg_radolfzell.android_app.main;

import dagger.Module;
import dagger.Provides;

/**
 * @author Jannik
 * @version 06.08.2016.
 */
@Module
public class MainModule {

    MainActivity activity;

    public MainModule(MainActivity activity) {
        this.activity = activity;
    }

    @Provides
    @MainScope
    MainActivity provideMainActivity() {
        return activity;
    }
}
