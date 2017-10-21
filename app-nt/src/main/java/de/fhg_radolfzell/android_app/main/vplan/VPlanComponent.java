package de.fhg_radolfzell.android_app.main.vplan;

import dagger.Subcomponent;
import de.fhg_radolfzell.android_app.data.source.VPlanHtmlDataSource;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@VPlanScope
@Subcomponent(
        modules = {
                VPlanModule.class
        }
)
public interface VPlanComponent {

    void inject(VPlanFragment vPlanFragment);
    void inject(VPlanPresenter presenter);
    void inject(VPlanHtmlDataSource dataSourceHtml);
}
