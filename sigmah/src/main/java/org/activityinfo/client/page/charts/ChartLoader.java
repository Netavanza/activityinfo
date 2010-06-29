package org.activityinfo.client.page.charts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.client.inject.AppInjector;
import org.activityinfo.client.page.*;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class ChartLoader implements PageLoader {

    private final AppInjector injector;

    @Inject
    public ChartLoader(AppInjector injector, NavigationHandler pageManager, PageStateSerializer placeSerializer) {
        this.injector = injector;

        pageManager.registerPageLoader(Charts.Charts, this);
        placeSerializer.registerStatelessPlace(Charts.Charts, new ChartPageState());
    }

    @Override
    public void load(final PageId pageId, final PageState pageState, final AsyncCallback<Page> callback) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess() {

                if(pageState instanceof ChartPageState) {
                    callback.onSuccess(injector.getCharter());
                }
            }
        });


    }
}