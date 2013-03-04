package org.activityinfo.geoadmin;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ImportTableCellRenderer extends DefaultTableCellRenderer {

	private ImportQualityEvaluator qualityEvaluator;
	
	public ImportTableCellRenderer(ImportQualityEvaluator qualityEvaluator) {
		super();
		this.qualityEvaluator = qualityEvaluator;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(!isSelected) {
			System.out.println("Rendering "  +value);
			c.setBackground(qualityEvaluator.evaluate(row));
		}
        return c;
	}
}
