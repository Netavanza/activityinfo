package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.AdminUnit;

import com.vividsolutions.jts.geom.Envelope;

public class UpdateWindow extends JDialog {

	private List<AdminUnit> units;
	private List<Join> joins;
	private ImportSource source;
	private UpdateForm form;
	
	public UpdateWindow(JFrame parent, ImportSource source, AdminLevel level, GeoClient client) {
		super(parent, "Update " + level.getName(), Dialog.ModalityType.APPLICATION_MODAL);
		setSize(650, 350);
		setLocationRelativeTo(parent);

		this.source = source;

		form = new UpdateForm(source);
		
		// fetch the existing entities
		units = client.getAdminEntities(level);

		// try to join automatically the two datasets
		Joiner joiner = new Joiner();
		joiner.setImportSource(source);
		joiner.setUnits(units);
		joins = joiner.join();
		
		UpdateTableModel tableModel = new UpdateTableModel(units, source);
		tableModel.setJoins(joins);
				
		JTable table = new JTable(tableModel);
		
		JPanel panel = new JPanel(new MigLayout());
		panel.add(form, "wrap");
		panel.add(new JScrollPane(table), "grow");
		
		getContentPane().add(createToolbar(), BorderLayout.PAGE_START);
		getContentPane().add(panel, BorderLayout.CENTER);
	}
	
	private JToolBar createToolbar() {
		
		JButton exportSqlButton = new JButton("Export SQL");
		exportSqlButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exportSQL(System.out);
			}
		});
		
		JToolBar toolbar = new JToolBar();
		toolbar.add(exportSqlButton);
		
		return toolbar;
	}

	protected void exportSQL(PrintStream out) {
		for(Join join : joins) {
			Envelope envelope = source.getEnvelope( join.getFeatureIndex() );
			String name = source.getAttributeStringValue( join.getFeatureIndex(), form.getNameProperty() );
			String code = source.getAttributeStringValue( join.getFeatureIndex(), form.getCodeProperty() );
			
			out.println(String.format("UPDATE adminentity SET name = '%s', code = '%s', " +
					"x1 = %f, y1 = %f, x2 = %f, y2 = %f WHERE adminentityid = %d;", 
					MatchUtils.escapeName(name),
					code,
					envelope.getMinX(),
					envelope.getMinY(),
					envelope.getMaxX(),
					envelope.getMaxY(),
					join.getUnit().getId()));
		}
	}
}
