/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import org.sigmah.client.dispatch.callback.Got;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.inject.AppInjector;
import org.sigmah.client.page.Frames;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageLoader;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.PageStateSerializer;
import org.sigmah.client.page.common.filter.FilterPanel;
import org.sigmah.client.page.entry.editor.SiteFormPage;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.SchemaDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class DataEntryLoader implements PageLoader {
    private final AppInjector injector;
    private final Provider<DataEntryFrameSet> dataEntryFrameSetProvider;
    private final Provider<SiteFormPage> siteFormProvider;
    private FilterPanel filterPanelSet = null;
    
    @Inject
    public DataEntryLoader(AppInjector injector, NavigationHandler pageManager, 
    		PageStateSerializer placeSerializer,
    		Provider<DataEntryFrameSet> dataEntryFrameSetProvider, Provider<SiteFormPage> siteFormProvider) {
        this.injector = injector;
        this.dataEntryFrameSetProvider = dataEntryFrameSetProvider;
        this.siteFormProvider=siteFormProvider;
        pageManager.registerPageLoader(Frames.DataEntryFrameSet, this);
        pageManager.registerPageLoader(SiteEditor.ID, this);
        placeSerializer.registerParser(SiteEditor.ID, new SiteGridPageState.Parser());
        
        placeSerializer.registerParser(SiteFormPage.EDIT_PAGE_ID, new SiteFormPage.EditPageStateParser());
        pageManager.registerPageLoader(SiteFormPage.EDIT_PAGE_ID, this);
        
        placeSerializer.registerParser(SiteFormPage.NEW_PAGE_ID, new SiteFormPage.NewStateParser());
        pageManager.registerPageLoader(SiteFormPage.NEW_PAGE_ID, this);

    }

    @Override
    public void load(final PageId pageId, final PageState pageState, final AsyncCallback<Page> callback) {

        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                if (Frames.DataEntryFrameSet.equals(pageId)) {
                    loadFrame(pageState, callback);
                } else if (SiteEditor.ID.equals(pageId)) {
                    loadSiteGrid(pageState, callback);
                } else if (SiteFormPage.EDIT_PAGE_ID.equals(pageId) || 
                		   SiteFormPage.NEW_PAGE_ID.equals(pageId)) {
                	SiteFormPage siteFormPage = siteFormProvider.get();
                	siteFormPage.navigate(pageState);
                	callback.onSuccess(siteFormPage);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });

    }

    private void loadFrame(PageState place, AsyncCallback<Page> callback) {

    	DataEntryFrameSet frameSet = dataEntryFrameSetProvider.get();
    	this.filterPanelSet = frameSet.getFilterPanelSet();
		callback.onSuccess(frameSet);
       
    }

    protected void loadSiteGrid(final PageState place, final AsyncCallback<Page> callback) {
        injector.getService().execute(new GetSchema(), null, new Got<SchemaDTO>() {
        	
            @Override
            public void got(SchemaDTO schema) {
                SiteGridPageState sgPlace = (SiteGridPageState) place;
                if (sgPlace.getActivityId() == 0) {
                	if (schema.getFirstActivity() != null) {
                		sgPlace.setActivityId(schema.getFirstActivity().getId());
                	}
                }

                ActivityDTO activity = schema.getActivityById(sgPlace.getActivityId());

                SiteGridPage grid = new SiteGridPage(true);
                SiteEditor editor = new SiteEditor(injector.getEventBus(), injector.getService(),
                        injector.getStateManager(), grid);
                editor.bindFilterPanel(filterPanelSet);

                if (activity.getReportingFrequency() == ActivityDTO.REPORT_MONTHLY) {
                    MonthlyGrid monthlyGrid = new MonthlyGrid(activity);
                    MonthlyTab monthlyTab = new MonthlyTab(monthlyGrid);
                    MonthlyPresenter monthlyPresenter = new MonthlyPresenter(
                            injector.getEventBus(),
                            injector.getService(),
                            injector.getStateManager(),
                            activity, monthlyGrid);
                    editor.addSubComponent(monthlyPresenter);
                    grid.addSouthTab(monthlyTab);
                } else {

                    DetailsTab detailsTab = new DetailsTab();
                    DetailsPresenter detailsPresenter = new DetailsPresenter(
                            injector.getEventBus(),
                            activity,
                            injector.getMessages(),
                            detailsTab);
                    grid.addSouthTab(detailsTab);
                    editor.addSubComponent(detailsPresenter);
                }
                SiteMap map = new SiteMap(injector.getEventBus(), injector.getService(),
                        activity);
                editor.addSubComponent(map);
                grid.addSidePanel(I18N.CONSTANTS.map(), IconImageBundle.ICONS.map(), map);

                editor.go((SiteGridPageState) place, activity);
                callback.onSuccess(editor);

            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

}
