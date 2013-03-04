package org.activityinfo.geoadmin;

import javax.swing.table.AbstractTableModel;

import org.activityinfo.geoadmin.model.AdminUnit;

public class ImportTableModel extends AbstractTableModel {

	public static final int PARENT_COLUMN = 0;
	public static final int STATUS_COLUMN = 1;
	
	public static final int NUM_EXTRA_COLUMNS = 2;
	
	private ImportSource source;
	private AdminUnit[] parents;
		
	public ImportTableModel(ImportSource source) {
		this.source = source;
		this.parents = new AdminUnit[source.getRows().size()];
	}
	
	@Override
	public int getColumnCount() {
		return source.getAttributes().size() + NUM_EXTRA_COLUMNS;
	}

	@Override
	public int getRowCount() {
		return source.getRows().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		switch(colIndex) {
		case 0:
			return parents[rowIndex];
		default:
			return source.getRows().get(rowIndex)[colIndex-1];
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
		case PARENT_COLUMN:
			return AdminUnit.class;
		default:		
			return source.getAttributes().get(columnIndex-1).getType().getBinding();
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case PARENT_COLUMN:
			return "PARENT";
		default:
			return source.getAttributes().get(columnIndex-NUM_EXTRA_COLUMNS).getName().getLocalPart();
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case PARENT_COLUMN:
			return true;
		default: 
			return false;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex == PARENT_COLUMN) {
			parents[rowIndex] = (AdminUnit)aValue;
			fireRowChanged(rowIndex);
		} 
	}

	private void fireRowChanged(int rowIndex) {
		for(int i=0;i!=getColumnCount();++i) {
			fireTableCellUpdated(rowIndex, i);
		}
	}

	public AdminUnit getParent(int featureIndex) {
		return parents[featureIndex];
	}
}
