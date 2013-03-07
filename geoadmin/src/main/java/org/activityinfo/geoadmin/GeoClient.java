package org.activityinfo.geoadmin;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.AdminUnit;
import org.activityinfo.geoadmin.model.Country;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;

public class GeoClient {
	private Client client;
	private URI root;
	
	public GeoClient(String endpoint, String username, String password) {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		client.addFilter(new HTTPBasicAuthFilter(username, password));
		
		root = UriBuilder.fromUri(endpoint).build();
	}
	
	public List<Country> getCountries() {
		return Arrays.asList( 
				client.resource(UriBuilder.fromUri(root).path("countries").build())
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(Country[].class));
		
	}
	
	public List<AdminLevel> getAdminLevels(Country country) {
		return Arrays.asList(
				client.resource(UriBuilder.fromUri(root)
						.path("country")
						.path(country.getCode())
						.path("adminLevels").build())
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(AdminLevel[].class));
		
	}
	
	public void updateAdminLevel(AdminLevel level) {
		client.resource(UriBuilder.fromUri(root)
				.path("adminUnitLevel")
				.path(Integer.toString(level.getId()))
				.build())
			.accept(MediaType.APPLICATION_JSON)
			.type(MediaType.APPLICATION_JSON)
			.put(level);
				
	}

	
	public List<AdminUnit> getAdminEntities(AdminLevel level) {
		return Arrays.asList(
				client.resource(UriBuilder.fromUri(root)
						.path("adminUnitLevel")
						.path(Integer.toString(level.getId()))
						.path("units")
						.build())
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(AdminUnit[].class));
	}

	public void updateAdminEntities(AdminLevel level, List<AdminUnit> entities) {
		client.resource(UriBuilder.fromUri(root)
				.path("adminUnitLevel")
				.path(Integer.toString(level.getId()))
				.path("units")
				.build())
			.accept(MediaType.APPLICATION_JSON)
			.type(MediaType.APPLICATION_JSON)
			.put(entities);	
	}

	public void postChildLevel(AdminLevel parentLevel, AdminLevel newLevel) {
		client.resource(UriBuilder.fromUri(root)
				.path("adminUnitLevel")
				.path(Integer.toString(parentLevel.getId()))
				.path("childLevels")
				.build())
			.accept(MediaType.APPLICATION_JSON)
			.type(MediaType.APPLICATION_JSON)
			.post(newLevel);			
	}
}
