package org.activityinfo.geoadmin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.merge.MergeAction;
import org.activityinfo.geoadmin.merge.MergeNode;
import org.activityinfo.geoadmin.merge.MergeTreeBuilder;
import org.activityinfo.geoadmin.merge.MergeTreeTableModel;
import org.activityinfo.geoadmin.model.ActivityInfoClient;
import org.activityinfo.geoadmin.model.AdminEntity;
import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.VersionMetadata;
import org.activityinfo.geoadmin.writer.FileSetWriter;
import org.jdesktop.swingx.JXTreeTable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Window proving a user interface to match a shapefile to an existing admin
 * level. For example, updating with better/new geography or new entities.
 * 
 * <p>
 * The user, with a lot of help from automatic algorithms, needs to match each
 * feature from the shapefile to an existing admin entity.
 * 
 */
public class UpdateWindow extends JFrame {

    private List<Join> joins;
    private ImportSource source;
    private UpdateForm form;
    private AdminLevel level;
    private ActivityInfoClient client;
    private JLabel scoreLabel;
    private JXTreeTable treeTable;
    private MergeTreeTableModel treeModel;

    public UpdateWindow(JFrame parent, ImportSource source, AdminLevel level, ActivityInfoClient client) {
        super("Update " + level.getName());
        setSize(650, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.client = client;
        this.level = level;
        this.source = source;

        form = new UpdateForm(source);

        MergeTreeBuilder treeBuilder = new MergeTreeBuilder(client, level, source);
        treeModel = new MergeTreeTableModel(treeBuilder.build(), source);
        treeTable = new JXTreeTable(treeModel);

        JComboBox actionCombo = new JComboBox(MergeAction.values());

        treeTable.getColumnModel()
            .getColumn(MergeTreeTableModel.ACTION_COLUMN)
            .setCellEditor(new DefaultCellEditor(actionCombo));

        treeTable.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent event) {
                showScore(event);
            }
        });

        scoreLabel = new JLabel();
        JLabel countLabel = new JLabel(source.getFeatureCount() + " features");

        JPanel panel = new JPanel(new MigLayout("fill"));
        panel.add(form, "wrap");
        panel.add(new JScrollPane(treeTable), "wrap,grow");

        panel.add(scoreLabel, "height 25!, wrap, growx");
        panel.add(countLabel);

        getContentPane().add(createToolbar(), BorderLayout.PAGE_START);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    /**
     * Displays the score of the selected match bet
     * 
     * @param event
     */
    private void showScore(TreeSelectionEvent event) {
        MergeNode node = (MergeNode) event.getPath().getLastPathComponent();
        if (node.getFeature() == null || node.getEntity() == null) {
            scoreLabel.setText("");
        } else {
            double nameSim = Joiner.scoreName(node.getEntity(), node.getFeature());
            double intersection = Joiner.calculateOverlap(node.getEntity(), node.getFeature());

            scoreLabel.setText(String.format("Name match: %.2f  Intersection: %.2f",
                nameSim, intersection));
        }
    }

    private JToolBar createToolbar() {

        final JButton acceptTheirsButton = new JButton("Accept Theirs");
        acceptTheirsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                acceptTheirs();
            }
        });

        final JButton mergeButton = new JButton("Merge");
        mergeButton.setEnabled(false);
        treeTable.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent event) {
                mergeButton.setEnabled(isSelectionMergeable());
            }
        });
        mergeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                mergeSelection();
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                update();
            }
        });

        JToolBar toolbar = new JToolBar();
        toolbar.add(acceptTheirsButton);
        toolbar.add(mergeButton);
        toolbar.add(updateButton);

        return toolbar;
    }

    /**
     * Checks to see if the current selection is candidate for merging.
     */
    private boolean isSelectionMergeable() {
        if (treeTable.getSelectedRowCount() != 2) {
            return false;
        }

        MergeNode a = (MergeNode) treeTable.getPathForRow(
            treeTable.getSelectedRows()[0]).getLastPathComponent();
        MergeNode b = (MergeNode) treeTable.getPathForRow(
            treeTable.getSelectedRows()[1]).getLastPathComponent();
        if (a.isJoined() || b.isJoined()) {
            return false;
        }
        return ((a.getFeature() == null && b.getFeature() != null) || (b.getFeature() == null && a.getFeature() != null));
    }

    private void acceptTheirs() {
        for (MergeNode node : getLeaves()) {
            if(node.isLeaf()) {
                if(node.getFeature() == null) {
                    treeModel.setValueAt(MergeAction.DELETE, node, MergeTreeTableModel.ACTION_COLUMN);
                } else if (node.getEntity() == null) {
                    treeModel.setValueAt(MergeAction.UPDATE, node, MergeTreeTableModel.ACTION_COLUMN);
                }
            }
        }
    }

    /**
     * Merges an unmatched existing entity with an unmatched imported feature
     */
    private void mergeSelection() {
        MergeNode a = (MergeNode) treeTable.getPathForRow(
            treeTable.getSelectedRows()[0]).getLastPathComponent();
        MergeNode b = (MergeNode) treeTable.getPathForRow(
            treeTable.getSelectedRows()[1]).getLastPathComponent();

        MergeNode entityNode;
        MergeNode featureNode;

        if (a.getEntity() != null) {
            entityNode = a;
            featureNode = b;
        } else {
            entityNode = b;
            featureNode = a;
        }

        entityNode.setFeature(b.getFeature());

        treeModel.fireNodeChanged(entityNode);
        treeModel.removeNodeFromParent(featureNode);
    }

    /**
     * Updates the server with the imported features.
     */
    private void update() {

        List<AdminEntity> entities = Lists.newArrayList();
        Map<Integer, String> geometryText = loadGeometry();
        	
        for (MergeNode join : getLeaves()) {
            if (join.getAction() != null && join.getAction() != MergeAction.IGNORE) {
                AdminEntity unit = new AdminEntity();
                if (join.getEntity() != null) {
                    unit.setId(join.getEntity().getId());
                }
                if (join.getFeature() != null) {
                    unit.setName(join.getFeature().getAttributeStringValue(form.getNameProperty()));
                    if (form.getCodeProperty() != null) {
                        unit.setCode(join.getFeature().getAttributeStringValue(form.getCodeProperty()));
                    }
                    unit.setBounds(GeoUtils.toBounds(join.getFeature().getEnvelope()));
                    unit.setGeometryText(geometryText.get(join.getFeature().getIndex()));
                
                }
                unit.setDeleted(join.getAction() == MergeAction.DELETE);
                entities.add(unit);
            }
        }

        VersionMetadata metadata = new VersionMetadata();
        metadata.setSourceFilename(source.getFile().getName());
        metadata.setSourceMD5(source.getMd5Hash());
        metadata.setSourceUrl(form.getSourceUrl());
        try {
            metadata.setSourceMetadata(source.getMetadata());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        AdminLevel updatedLevel = new AdminLevel();
        updatedLevel.setId(level.getId());
        updatedLevel.setName(level.getName());
        updatedLevel.setParentId(level.getParentId());
        updatedLevel.setEntities(entities);
        updatedLevel.setVersionMetadata(metadata);

        client.updateAdminLevel(updatedLevel);
    }

    private Map<Integer, String> loadGeometry() {
    	
    	WKTWriter writer = new WKTWriter();
    	
		Map<Integer, String> map = Maps.newHashMap();
		int featureIndex = 0;
		for (Geometry geometry : source.getGeometery()) {
			map.put(featureIndex, writer.write(geometry));
		}
		
		return map;
	}

	private List<MergeNode> getLeaves() {
        List<MergeNode> nodes = ((MergeNode) treeModel.getRoot()).getLeaves();
        return nodes;
    }

    private void writeGeometry() throws IOException {
        Map<Integer, Integer> idMap = featureIndexToIdMap();
        FileSetWriter fileSetWriter = new FileSetWriter(level.getId());
        fileSetWriter.start(source.getFeatureSource().getFeatures());

        int featureIndex = 0;
        for (Geometry geometry : source.getGeometery()) {
            fileSetWriter.write(idMap.get(featureIndex), geometry);
        }
        fileSetWriter.close();
    }

    private Map<Integer, Integer> featureIndexToIdMap() {
        Map<Integer, Integer> map = Maps.newHashMap();
        for (Join join : joins) {
            map.put(join.getFeature().getIndex(), join.getEntity().getId());
        }
        return map;
    }
}
