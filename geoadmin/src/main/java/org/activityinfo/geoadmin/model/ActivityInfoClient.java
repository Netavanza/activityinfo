package org.activityinfo.geoadmin.model;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ContextResolver;

import org.codehaus.jackson.map.ObjectMapper;

import com.bedatadriven.geojson.GeoJsonModule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * ActivityInfo REST Client
 */
public class ActivityInfoClient {
    private Client client;
    private URI root;

    public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

        @Override
        public ObjectMapper getContext(Class<?> type) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new GeoJsonModule());
            return mapper;
        }

    }

    /**
     * Creates a new instance using the given endpoint, ActivityInfo username
     * and password.
     * 
     * @param endpoint
     *            Rest endpoint (for example:
     *            https://www.activityinfo.org/resources)
     * @param username
     *            Email address of user (for example: akbertram@gmail.com)
     * @param password
     *            User's plaintext password
     */
    public ActivityInfoClient(String endpoint, String username, String password) {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(ObjectMapperProvider.class);

        client = Client.create(clientConfig);
        client.addFilter(new HTTPBasicAuthFilter(username, password));

        root = UriBuilder.fromUri(endpoint).build();
    }

    /**
     * @return the list of Countries in ActivityInfo's geographic reference
     *         database
     */
    public List<Country> getCountries() {
        URI uri = UriBuilder.fromUri(root).path("countries").build();
        return Arrays.asList(
            client.resource(uri)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(Country[].class));

    }

    /**
     * @return the list of Administrative levels for a given country in
     *         ActivityInfo's geographic reference database
     */
    public List<AdminLevel> getAdminLevels(Country country) {
        URI uri = UriBuilder.fromUri(root)
            .path("country")
            .path(country.getCode())
            .path("adminLevels").build();
        return Arrays.asList(
            client.resource(uri)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(AdminLevel[].class));

    }

    public void updateAdminLevel(AdminLevel level) {
        URI uri = UriBuilder.fromUri(root)
            .path("adminLevel")
            .path(Integer.toString(level.getId()))
            .build();

        client.resource(uri)
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON)
            .put(level);
    }

    public AdminLevel getAdminLevel(int id) {
        URI build = UriBuilder.fromUri(root)
            .path("adminLevel")
            .path(Integer.toString(id))
            .build();
        return client.resource(build)
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON)
            .get(AdminLevel.class);
    }

    public List<AdminEntity> getAdminEntities(AdminLevel level) {
        return getAdminEntities(level.getId());
    }

    public List<AdminEntity> getAdminEntities(int levelId) {
        URI uri = UriBuilder.fromUri(root)
            .path("adminLevel")
            .path(Integer.toString(levelId))
            .path("entities")
            .build();

        return Arrays.asList(
            client.resource(uri)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(AdminEntity[].class));
    }

    /**
     * Creates a new root administrative level for a given country
     */
    public void postRootLevel(Country country, AdminLevel newLevel) {
        URI uri = UriBuilder.fromUri(root)
            .path("country")
            .path(country.getCode())
            .path("adminLevels")
            .build();
        client.resource(uri)
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON)
            .post(newLevel);
    }

    /**
     * Creates a new root administrative level for a given country
     */
    public void postChildLevel(AdminLevel parentLevel, AdminLevel newLevel) {
        URI uri = UriBuilder.fromUri(root)
            .path("adminLevel")
            .path(Integer.toString(parentLevel.getId()))
            .path("childLevels")
            .build();
        client.resource(uri)
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON)
            .post(newLevel);
    }
}
