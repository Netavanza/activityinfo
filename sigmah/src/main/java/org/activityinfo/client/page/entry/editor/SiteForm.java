package org.activityinfo.client.page.entry.editor;


import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.maps.client.Maps;
import org.activityinfo.client.Application;
import org.activityinfo.client.dispatch.AsyncMonitor;
import org.activityinfo.client.dispatch.monitor.MaskingAsyncMonitor;
import org.activityinfo.client.page.common.widget.LoadingPlaceHolder;
import org.activityinfo.client.page.config.form.ModelFormPanel;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.CountryDTO;
import org.activityinfo.shared.dto.PartnerDTO;
import org.activityinfo.shared.dto.SiteDTO;

import java.util.Map;

public class SiteForm extends ModelFormPanel implements SiteFormPresenter.View {

    protected SiteFormPresenter presenter;
    protected ActivityDTO activity;

    private ActivityFieldSet activityFieldSet;
    private LocationFieldSet locationFieldSet;
    private MapPresenter.View mapView;
    private AttributeFieldSet attributeFieldSet;
    private IndicatorFieldSet indicatorFieldSet;
    private CommentFieldSet commentFieldSet;


    public SiteForm() {
        this.setBodyStyle("padding: 3px");
        this.setIcon(Application.ICONS.editPage());
        this.setHeading(Application.CONSTANTS.loading());

        add(new LoadingPlaceHolder());
    }


    @Override
    public void init(SiteFormPresenter presenter,
                     ActivityDTO activity,
                     ListStore<PartnerDTO> partnerStore,
                     ListStore<SiteDTO> assessmentStore) {

        removeAll();
        setLayout(new FlowLayout());

        this.presenter = presenter;
        this.activity = activity;

        this.setLayout(new FlowLayout());
        this.setScrollMode(Scroll.AUTOY);
        this.setHeading(activity.getName());

        // ACTIVITY fieldset

        activityFieldSet = new ActivityFieldSet(activity, partnerStore, assessmentStore);
        add(activityFieldSet);

        // LOCATION fieldset

        add(locationFieldSet);

        // GEO POSITION

        add((FieldSet) mapView);

        if (Maps.isLoaded()) {
            registerField(((MapFieldSet) mapView).getLngField());
            registerField(((MapFieldSet) mapView).getLatField());
        } else {
            registerFieldSet((FieldSet) mapView);
        }

        // ATTRIBUTE fieldset

        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {

            attributeFieldSet = new AttributeFieldSet(activity);
            registerFieldSet(attributeFieldSet);
            add(attributeFieldSet);

            // INDICATOR fieldset

            indicatorFieldSet = new IndicatorFieldSet(activity);
            registerFieldSet(indicatorFieldSet);
            add(indicatorFieldSet);


            // COMMENT

            commentFieldSet = new CommentFieldSet();
            add(commentFieldSet);
        }

        registerAll();

        layout();
    }


    public void setSite(SiteDTO site) {
        updateForm(site);

    }

    @Override
    public AsyncMonitor getMonitor() {
        return new MaskingAsyncMonitor(this, Application.CONSTANTS.saving());
    }

    @Override
    public AdminFieldSetPresenter.View createAdminFieldSetView(ActivityDTO activity) {

        locationFieldSet = new LocationFieldSet(activity);

        return locationFieldSet;
    }

    @Override
    public MapPresenter.View createMapView(CountryDTO country) {

        if (Maps.isLoaded()) {
            MapFieldSet mapFieldSet = new MapFieldSet(country);
            this.mapView = mapFieldSet;
        } else {
            CoordFieldSet coordFieldSet = new CoordFieldSet();
            this.mapView = coordFieldSet;
        }

        return mapView;
    }

    @Override
    public Map<String, Object> getChanges() {
        return super.getModified();
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    protected void onDirtyFlagChanged(boolean isDirty) {
        presenter.onModified();
    }

    @Override
    public void setActionEnabled(String actionId, boolean enabled) {

    }

    public Map<String, Object> getPropertyMap() {
        return super.getAllValues();
    }
}