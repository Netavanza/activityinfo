package org.activityinfo.geoadmin;

import java.util.List;
import java.util.Set;

import org.activityinfo.geoadmin.model.AdminUnit;
import org.opengis.feature.type.PropertyDescriptor;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class ParentColumnGuesser {

	private ImportSource importSource;
	private List<AdminUnit> parentUnits;

	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
	}
	public void setParentUnits(List<AdminUnit> parentUnits) {
		this.parentUnits = parentUnits;
	}

	public PropertyDescriptor guess() {
		
		Set<String> index = indexParents();
		
		// total the number of matches per attribute
		int[] matches = new int[importSource.getAttributeCount()];
		for(int featureIndex=0;featureIndex!=importSource.getFeatureCount();++featureIndex) {
			for(int attribIndex=0;attribIndex!=importSource.getAttributeCount();++attribIndex) {
				Object value = importSource.getAttributeValue(featureIndex, attribIndex);
				if(value != null) {
					if(index.contains(value.toString().toLowerCase())) {
						matches[attribIndex]++;
					}
				}
			}
		}
		
		// find the attribute with the most matches
		int max = 0;
		int maxAttrib = -1;
		for(int i=0;i!=matches.length;++i) {
			if(matches[i] > max) {
				max = matches[i];
				maxAttrib = i;
			}
		}
		
		
		if(max == 0) {
			return null;
		} else {
			return importSource.getAttributes().get(maxAttrib);
		}
	}
	
	private Set<String> indexParents() {
		Set<String> index = Sets.newHashSet();
		for(AdminUnit unit : parentUnits) {
			index.add(unit.getName().toLowerCase());
			if(! Strings.isNullOrEmpty(unit.getCode())) {
				index.add(unit.getCode().toLowerCase());
			}
		}
		return index;
	}

}
