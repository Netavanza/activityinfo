package org.activityinfo.geoadmin;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.opengis.feature.type.PropertyDescriptor;

public class UpdateForm extends JPanel {

	private ImportSource source;
	private Object parentUnits;
	private JComboBox nameCombo;
	private JComboBox codeCombo;

	public UpdateForm(ImportSource source) {
		super(new MigLayout());
		
		this.source = source;
		
		nameCombo = new JComboBox(source.getAttributeNames());
		codeCombo = new JComboBox(source.getAttributeNames());
		
		add(new JLabel("Name Attribute"));  
		add(nameCombo, "width 160!, wrap");  

		add(new JLabel("Code Attribute"));  
		add(codeCombo, "width 160!, wrap");  
	}
	
	public PropertyDescriptor getNameProperty() {
		return source.getAttributes().get(nameCombo.getSelectedIndex());
	}
	
	public PropertyDescriptor getCodeProperty() {
		return source.getAttributes().get(codeCombo.getSelectedIndex());
	}
}
