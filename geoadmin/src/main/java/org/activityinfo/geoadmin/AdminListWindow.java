package org.activityinfo.geoadmin;

import java.awt.Dialog;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.AdminUnit;

public class AdminListWindow extends JDialog {

	public AdminListWindow(JFrame parent, GeoClient client, AdminLevel level) {
		super(parent, level.getName(),  Dialog.ModalityType.APPLICATION_MODAL);
		setSize(650, 350);
		setLocationRelativeTo(parent);
		
		List<AdminUnit> adminEntities = client.getAdminEntities(level);
		AdminTableModel tableModel = new AdminTableModel(adminEntities);
		JTable table = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(table);
		
		getContentPane().add(scroll);
	}
}
