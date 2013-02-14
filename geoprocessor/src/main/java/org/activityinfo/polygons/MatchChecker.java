package org.activityinfo.polygons;

import java.util.Map;

import org.activityinfo.jsonrpc.AdminUnit;

import com.google.common.collect.Maps;

public class MatchChecker {

	public Map<Integer, AdminUnit> unmatched = Maps.newHashMap();
	public Map<Integer, AdminUnit> units = Maps.newHashMap();
	
	public MatchChecker(Iterable<AdminUnit> entities) {
		for(AdminUnit entity : entities) {
			units.put(entity.getMachineId(), entity);
			unmatched.put(entity.getMachineId(), entity);
		}
	}
	
	public void onMatched(int id) {
		if(unmatched.containsKey(id)) {
			unmatched.remove(id);
		} else {
			AdminUnit unit = units.get(id);
			if(unit == null) {
				throw new RuntimeException(id + " does not exist in this admin level");
			} else {
				throw new RuntimeException(unit.getName() + " (" + unit.getMachineId() + ") matched twice");
			}
		} 
	}
	
	public void check() {
		if(!unmatched.isEmpty()) {
			System.err.println("WARNING: " + unmatched.size() + " entities were not matched to polygons: ");
			for(AdminUnit entity : unmatched.values()) {
				System.err.println("\t" + entity.getName());
			}
		}
	}
	
}
