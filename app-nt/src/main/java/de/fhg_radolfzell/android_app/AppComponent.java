package de.fhg_radolfzell.android_app;


import javax.inject.Singleton;

import dagger.Component;
import de.fhg_radolfzell.android_app.cloudmessaging.InstanceIdService;
import de.fhg_radolfzell.android_app.cloudmessaging.MessagingService;
import de.fhg_radolfzell.android_app.main.MainComponent;
import de.fhg_radolfzell.android_app.main.MainModule;
import de.fhg_radolfzell.android_app.main.NetModule;
import de.fhg_radolfzell.android_app.view.BaseFragment;
import de.fhg_radolfzell.android_app.main.credits.CreditsFragment;

@Singleton
@Component(
        modules = {
                AppModule.class,
                NetModule.class
        }
)
public interface AppComponent {
    MainComponent newMainComponent(MainModule module);

    void inject(FHGApplication application);

    void inject(CreditsFragment fragment);

    void inject(InstanceIdService idService);

    void inject(MessagingService messagingService);

    void inject(BaseFragment fragment);
}