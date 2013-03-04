package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.activityinfo.geoadmin.model.Country;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GeoAdmin extends JFrame {

	private static final String OPEN_TABS = "open_tabs";
	private static final String ACTIVE_TAB = "active_tab";
	
	private GeoClient client = new GeoClient();
	private JTabbedPane tabPane;
	
	private List<Country> countries;
	
	private List<CountryTab> countryTabs = Lists.newArrayList();
	private Map<Integer, CountryTab> countryMap = Maps.newHashMap();


	private Preferences prefs = Preferences.userNodeForPackage(GeoAdmin.class);
	
	public GeoAdmin() {
		setTitle("ActivityInfo Geo Administrator");
		setSize(540, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		tabPane = new JTabbedPane();

		getContentPane().add(tabPane, BorderLayout.CENTER);
		
		this.countries = client.getCountries();
		
		createTree();
		
		loadOpenTabs();
		loadActiveTab();
		
		tabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				saveActiveTab();
			}
		});
	}


	private void saveActiveTab() {
		prefs.put(ACTIVE_TAB, tabPane.getTitleAt(tabPane.getSelectedIndex()));
		flushPrefs();
	}
	
	private void loadActiveTab() {
		String activeTab = prefs.get(ACTIVE_TAB, "");
		for(int i=0;i!=tabPane.getTabCount();++i) {
			if(tabPane.getTitleAt(i).equals(activeTab)) {
				tabPane.setSelectedComponent(tabPane.getComponentAt(i));
				return;
			}
		}
	}

	private void loadOpenTabs() {
		String[] openIds = prefs.get(OPEN_TABS, "").split(",");
		for(String openId : openIds) {
			int countryId = Integer.parseInt(openId);
			for(Country country : countries) {
				if(country.getId() == countryId) {
					showCountryWindow(country);
				}
			}
		}
	}

	private void createTree() {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Countries");

		for(Country country : countries) {
			node.add( new DefaultMutableTreeNode(country));
		}
		
		final JTree tree = new JTree(node);
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if(selPath != null) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						Country country = (Country) node.getUserObject();
						showCountryWindow(country);
					}
				}
			}
		});
		
		tabPane.addTab("Countries", new JScrollPane(tree));
	}

	protected void showCountryWindow(Country country) {
		if(countryMap.containsKey(country)) {
			tabPane.setSelectedComponent(countryMap.get(country.getId()));
		} else {
			CountryTab tab = new CountryTab(client, country);
			tabPane.addTab(country.getName(), tab);
			tabPane.setSelectedComponent(tab);
			countryMap.put(country.getId(), tab);
			countryTabs.add(tab);
			saveOpenTabs();
		}	
	}


	private void saveOpenTabs() {
		prefs.put(OPEN_TABS, Joiner.on(",").join(countryMap.keySet()));
		flushPrefs();
	}


	private void flushPrefs()  {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GeoAdmin ex = new GeoAdmin();
				ex.setVisible(true);
			}
		});
	}
}