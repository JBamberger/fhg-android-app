package de.fhg_radolfzell.android_app.main.vplan;

import dagger.Module;
import dagger.Provides;
import de.fhg_radolfzell.android_app.data.source.FhgWebInterface;
import de.fhg_radolfzell.android_app.data.source.VPlanDataSource;
import de.fhg_radolfzell.android_app.data.source.VPlanHtmlDataSource;
import de.fhg_radolfzell.android_app.data.source.VPlanHtmlParser;
import de.fhg_radolfzell.android_app.util.Storage;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Module
public class VPlanModule {

    private VPlanFragment vPlanFragment;

    public VPlanModule(VPlanFragment fragment) {
        vPlanFragment = fragment;
    }

    @Provides
    @VPlanScope
    public VPlanFragment providesVPlanFragment() {
        return vPlanFragment;
    }

    @Provides
    @VPlanScope
    public VPlanAdapter providesVPlanAdapter() {
        return new VPlanAdapter();
    }

    @Provides
    @VPlanScope
    public VPlanContract.Presenter providesVPlanPresenter(Storage storage, VPlanDataSource dataSource) {
        return new VPlanPresenter(vPlanFragment, storage, dataSource);
    }

    @Provides
    @VPlanScope
    public VPlanDataSource providesVPlanDataSource(FhgWebInterface endpoint, VPlanHtmlParser parser) {
        return new VPlanHtmlDataSource(endpoint, parser);
    }

    @Provides
    @VPlanScope
    public VPlanHtmlParser providesVPlanHtmlParser() {
        return new VPlanHtmlParser();
    }
}
