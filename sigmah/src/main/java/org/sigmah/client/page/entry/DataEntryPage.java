package org.sigmah.client.page.entry;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.nav.NavigationPanel;
import org.sigmah.client.page.entry.place.DataEntryPlace;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.inject.Inject;

/**
 * This is the main container for the data entry section. 
 *
 */
public class DataEntryPage extends LayoutContainer  implements Page {

	public static final PageId PAGE_ID = new PageId("data-entry");

	private final Dispatcher dispatcher;
	
	private NavigationPanel databasePanel;
	private SiteGridPanel gridPanel;
	
	
	@Inject
	public DataEntryPage(EventBus eventBus, Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		
		setLayout(new BorderLayout());
		
		databasePanel = new NavigationPanel(eventBus, new DataEntryNavigator(dispatcher));
		BorderLayoutData filterLayout = new BorderLayoutData(LayoutRegion.WEST);
		filterLayout.setCollapsible(true);
		filterLayout.setMargins(new Margins(0,5,0,0));
		filterLayout.setSplit(true);
		add(databasePanel, filterLayout);
		
		gridPanel = new SiteGridPanel(dispatcher);
		add(gridPanel, new BorderLayoutData(LayoutRegion.CENTER));
		
		final SitePane sitePane = new SitePane(dispatcher);
		BorderLayoutData sitePaneLayout = new BorderLayoutData(LayoutRegion.EAST);
		sitePaneLayout.setSize(0.4f);
		sitePaneLayout.setCollapsible(true);
		sitePaneLayout.setMargins(new Margins(0, 0, 0, 5));
		sitePaneLayout.setSplit(true);
		add(sitePane, sitePaneLayout);
		
		gridPanel.addSelectionChangedListener(new SelectionChangedListener<SiteDTO>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<SiteDTO> se) {
				sitePane.setSite(se.getSelectedItem());
			}
		});
		
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PageId getPageId() {
		return PAGE_ID;
	}

	@Override
	public Object getWidget() {
		return this;
	}

	@Override
	public void requestToNavigateAway(PageState place,
			NavigationCallback callback) {
		callback.onDecided(true);
	}

	@Override
	public String beforeWindowCloses() {
		return null;
	}

	@Override
	public boolean navigate(PageState place) {
		gridPanel.navigate((DataEntryPlace) place);
		return true;
	}
}
