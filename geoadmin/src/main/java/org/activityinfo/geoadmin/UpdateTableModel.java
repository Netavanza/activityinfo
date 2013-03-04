package org.activityinfo.geoadmin;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.activityinfo.geoadmin.model.AdminUnit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UpdateTableModel extends AbstractTableModel {

	private List<Join> joins = Lists.newArrayList();
	private Map<Integer, AdminUnit> adminUnits;
	private ImportSource importSource;
	
	private static final int NUM_ENTITY_COLUMNS = 2;
	
	private static final int CODE_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	
	
	public UpdateTableModel(List<AdminUnit> adminUnits,
			ImportSource importSource) {
		super();
		this.adminUnits = Maps.newHashMap();
		for(AdminUnit unit : adminUnits) {
			this.adminUnits.put(unit.getId(), unit);
		}
		this.importSource = importSource;
	}

	@Override
	public int getColumnCount() {
		return NUM_ENTITY_COLUMNS + importSource.getAttributeCount();
	}

	@Override
	public int getRowCount() {
		return joins.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Join join = joins.get(rowIndex);
		if(columnIndex < NUM_ENTITY_COLUMNS) {
			if(join.getUnit() != null) {
				switch(columnIndex) {
				case CODE_COLUMN:
					return join.getUnit().getCode();
				case NAME_COLUMN:
					return join.getUnit().getName();
				}
			}
		} else {
			if(join.getFeatureIndex() != -1) {
				return importSource.getAttributeValue(join.getFeatureIndex(), 
						columnIndex - NUM_ENTITY_COLUMNS);
			}
		}
		return null;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case CODE_COLUMN:
			return "Code";
		case NAME_COLUMN:
			return "Name";
		default:
			return importSource.getAttributes()
					.get(columnIndex - NUM_ENTITY_COLUMNS)
					.getName()
					.getLocalPart();
		}
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
		fireTableDataChanged();
	}
}
