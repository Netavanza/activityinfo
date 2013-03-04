package org.activityinfo.geoadmin;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.activityinfo.geoadmin.model.AdminUnit;
import org.opengis.feature.type.PropertyDescriptor;

public class ImportForm extends JPanel {

	private ImportSource source;
	private JTextField levelNameField;
	private JComboBox nameCombo;
	private JComboBox codeCombo;
	private JComboBox parentNameCombo;
	private List<AdminUnit> parentUnits;
	private JComboBox parentCodeCombo;

	public ImportForm(ImportSource source, List<AdminUnit> parents) {
		super(new MigLayout());
		
		this.source = source;
		this.parentUnits = parents;
		
		levelNameField = new JTextField();
		nameCombo = new JComboBox(source.getAttributeNames());
		codeCombo = new JComboBox(source.getAttributeNames());
		parentNameCombo = new JComboBox(source.getAttributeNames());		
	
		parentCodeCombo = new JComboBox(source.getAttributeNames());
		parentCodeCombo.setSelectedIndex(-1);	
		
		add(new JLabel("Level Name:"));  
		add(levelNameField, "width 100!, wrap");  
		
		add(new JLabel("Name Attribute"));  
		add(nameCombo, "width 160!, wrap");  

		add(new JLabel("Code Attribute"));  
		add(codeCombo, "width 160!, wrap");  
		
		add(new JLabel("Parent Name Attribute"));  
		add(parentNameCombo, "width 160!, wrap");
		
		add(new JLabel("Parent Code Attribute"));  
		add(parentCodeCombo, "width 160!, wrap");
		
		guessInitial();
	}

	private void guessInitial() {
		ParentColumnGuesser parentColumnGuesser = new ParentColumnGuesser();
		parentColumnGuesser.setImportSource(source);
		parentColumnGuesser.setParentUnits(parentUnits);
		
		PropertyDescriptor parentColumn = parentColumnGuesser.guess();
		parentNameCombo.setSelectedIndex( source.getAttributes().indexOf(parentColumn) );
	}
	
	public int getNameAttributeIndex() {
		return nameCombo.getSelectedIndex();
	}

	public int getCodeAttributeIndex() {
		return codeCombo.getSelectedIndex();
	}
	
	public int getParentAttributeIndex() {
		return parentNameCombo.getSelectedIndex();
	}

	public PropertyDescriptor getParentAttribute() {
		return source.getAttributes().get(parentNameCombo.getSelectedIndex());
	}
	
	
}
