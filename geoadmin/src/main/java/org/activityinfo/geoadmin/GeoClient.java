package org.activityinfo.geoadmin;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.activityinfo.geoadmin.model.AdminLevel;
import org.activityinfo.geoadmin.model.Country;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class GeoClient {
	private Client client;
	private URI root;
	
	public GeoClient() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = Client.create(clientConfig);
		root = UriBuilder.fromUri("http://localhost:8888/resources").build();
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
}
