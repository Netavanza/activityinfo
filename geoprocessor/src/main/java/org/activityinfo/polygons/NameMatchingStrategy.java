package org.activityinfo.polygons;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.activityinfo.jsonrpc.AdminUnit;
import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NameMatchingStrategy implements MatchingStrategy {
	
	private static final Logger LOGGER = Logger.getLogger(NameMatchingStrategy.class.getName());
	
	private Properties properties;
	private String nameColumn;
	private Map<String, Integer> nameMap = Maps.newHashMap();
	private Set<String> notFound = Sets.newHashSet();

	public NameMatchingStrategy(List<AdminUnit> entities, Properties properties) {
		this.properties = properties;
		
		this.nameColumn = properties.getProperty("name.column");
		
		for(AdminUnit entity : entities) {
			String key = key(entity.getName());
			
			if(nameMap.containsKey(key)) {
				throw new UnsupportedOperationException("Cannot match by name, there are duplicates: '" + key + "'");
			}
 			nameMap.put(key, entity.getMachineId());
		}
	}

	

	private String key(String name) {
		return name.toLowerCase().trim().replace('-', ' ');
	}



	/* (non-Javadoc)
	 * @see org.activityinfo.polygons.Strategy#match(org.opengis.feature.simple.SimpleFeature)
	 */
	public int match(SimpleFeature feature) {
		String name = (String) feature.getValue(nameColumn);
		
		if(isIgnored(name)) {
			LOGGER.warning("Ignoring '" + name + "'");
			return -1;
		}
		
		String alias = null;
		Integer id = nameMap.get(key(name));
		if(id == null) {
			alias = getAlias(name);
			if(alias != null) {
				id = nameMap.get(key(alias));
			}
		}
		if(id == null) {
			onUnmatched(name, alias);
			return -1;
		}
		return id;
	}



	private String getAlias(String name) {
		return Strings.emptyToNull(properties.getProperty("alias." + name));
	}

	private boolean isIgnored(String name) {
		return !Strings.isNullOrEmpty(properties.getProperty("ignore." + name));
	}


	private void onUnmatched(String name, String alias) {
//		if(alias != null) {
//			System.err.println("Unmatched alias '" + alias + "' , (original '" + name + "'), possible matches:");
//		} else {
//			System.err.println("Unmatched name '" + name + "', possible matches:");
//		}
//		List<String> names = Lists.newArrayList(nameMap.keySet());
//		Collections.sort(names);
//		for(String keyName : names) {
//			System.err.println("\t" + keyName);
//		}
//		
		notFound.add(name);
	}



	/* (non-Javadoc)
	 * @see org.activityinfo.polygons.Strategy#done()
	 */
	public void done() {
		if(!notFound.isEmpty()) {
			System.err.println("There are polygons that could not be matched to admin entity:" );
			List<String> list = Lists.newArrayList(notFound);
			Collections.sort(list);
			for(String name : list) {
				System.err.println("\t" + name);
			}
		}
	}
	

}
