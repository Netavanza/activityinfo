package org.activityinfo.geoadmin.tree;

import java.util.List;
import java.util.Map;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

import org.activityinfo.geoadmin.GeoClient;
import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.Country;

import com.google.common.collect.Maps;

public class CountryTreeListener implements TreeWillExpandListener {

	private final GeoClient client;
	
	public CountryTreeListener(GeoClient client) {
		super();
		this.client = client;
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {

		DefaultMutableTreeNode countryNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
		if(countryNode.getUserObject() instanceof Country) {
			List<AdminLevel> levels = client.getAdminLevels((Country)countryNode.getUserObject());
			
			countryNode.removeAllChildren();
			Map<Integer, DefaultMutableTreeNode> nodes = Maps.newHashMap();
			
			// add root nodes
			for(AdminLevel level : levels) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(level);
				nodes.put(level.getId(), node);
				if(level.isRoot()) {
					countryNode.add(node);
				}
			}
			
			// add child nodes
			for(AdminLevel level : levels) {
				if(!level.isRoot()) {
					DefaultMutableTreeNode parent = nodes.get(level.getParentId());
					DefaultMutableTreeNode node = nodes.get(level.getId());
					parent.add(node);
				}
			}
		}
		
	}

}
