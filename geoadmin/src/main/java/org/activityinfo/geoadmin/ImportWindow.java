package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.AdminUnit;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;

import com.vividsolutions.jts.geom.Envelope;

public class ImportWindow extends JDialog {

	private GeoClient client;
	private List<AdminUnit> parentUnits;
	
	private ImportTableModel tableModel;
	private ImportForm importForm;
	private ImportSource source;
	private JMapPane mapPane;

	public ImportWindow(JFrame parent, GeoClient client, AdminLevel parentLevel, File shapeFile) throws Exception {
		super(parent, "Import - " + shapeFile.getName(), Dialog.ModalityType.APPLICATION_MODAL);
		setSize(650, 350);
		setLocationRelativeTo(parent);

		source = new ImportSource(shapeFile);
		parentUnits = sort(client.getAdminEntities(parentLevel));

		importForm = new ImportForm(source, parentUnits);

		tableModel = new ImportTableModel(source);
		JComboBox parentComboBox = new JComboBox(parentUnits.toArray());
		parentComboBox.setEditable(false);

		JTable table = new JTable(tableModel);
		table.getColumnModel().getColumn(0).setCellEditor(
				new DefaultCellEditor(parentComboBox));
		table.setDefaultRenderer(Object.class, 
				new ImportTableCellRenderer(
						new ImportQualityEvaluator(tableModel, source, importForm)));
		table.setAutoCreateRowSorter(true);
		
		table.getSelectionModel().addListSelectionListener(new  ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
		
			}
		});
		
		//mapPane = createMap();
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(importForm, "wrap");
		panel.add(new JScrollPane(table), "grow");
		
		getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
		getContentPane().add(panel, BorderLayout.CENTER);
	}


	private JToolBar createToolBar() {

		JButton guessButton = new JButton("Guess Parents");
		guessButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				guessParents();
			}
		});

		JButton exportButton = new JButton("Export SQL");
		exportButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exportSql(System.out);
			}
		});
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(guessButton);
		toolBar.addSeparator();
//		
//        ButtonGroup cursorToolGrp = new ButtonGroup();
//
//		JButton zoomInAction = new JButton(new ZoomInAction(mapPane));
//		toolBar.add(zoomInAction);
//		cursorToolGrp.add(zoomInAction);
//
//		JButton zoomOut = new JButton(new ZoomOutAction(mapPane));
//		toolBar.add(zoomOut);
//		cursorToolGrp.add(zoomOut);
		

		return toolBar;
	}

	private void exportSql(PrintStream out) {
		int nameAttribute = importForm.getNameAttributeIndex();
		int codeAttribute = importForm.getCodeAttributeIndex();
		
		for(int featureIndex = 0; featureIndex != source.getFeatureCount();++featureIndex) {
			Envelope mbr = source.getEnvelope(featureIndex);
			out.println(String.format("INSERT INTO adminentity (name,code,x1,y1,x2,y2,adminlevelid,adminentityparentid) " + 
						"VALUES ('%s', '%s', %f, %f, %f, %f, %d, %d);",
						MatchUtils.escapeName(source.getAttributeValue(featureIndex, nameAttribute).toString()),
						MatchUtils.escapeName(source.getAttributeValue(featureIndex, codeAttribute).toString()),
						mbr.getMinX(),
						mbr.getMinY(),
						mbr.getMaxX(),
						mbr.getMaxY(),
						999,
						tableModel.getParent(featureIndex).getId()));
						
		}
	}


	private List<AdminUnit> sort(List<AdminUnit> adminEntities) {
		Collections.sort(adminEntities, new Comparator<AdminUnit>() {

			@Override
			public int compare(AdminUnit a, AdminUnit b) {
				return a.getName().compareTo(b.getName());
			}
		});
		return adminEntities;
	}

	private JMapPane createMap() {
		MapContent context = new MapContent();
		context.addLayer(new FeatureLayer(source.getFeatureSource(), SLD.createPolygonStyle(Color.BLACK, Color.YELLOW, 0)));
		JMapPane mapPane = new JMapPane(context);
		return mapPane;
	}


	private void guessParents() {
		try {
			ParentGuesser guesser = new ParentGuesser();
			guesser.setImportSource(source);
			guesser.setParents(parentUnits);
			guesser.setParentName(importForm.getParentAttribute());
			AdminUnit[] parents = guesser.run();
			for(int featureIndex=0;featureIndex != parents.length; ++featureIndex) {
				tableModel.setValueAt(parents[featureIndex], featureIndex, ImportTableModel.PARENT_COLUMN);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
