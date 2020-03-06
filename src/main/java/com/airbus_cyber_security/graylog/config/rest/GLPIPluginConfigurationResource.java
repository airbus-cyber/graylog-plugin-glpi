package com.airbus_cyber_security.graylog.config.rest;

import com.airbus_cyber_security.graylog.GLPIConnection;
import com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.rest.PluginRestResource;
import org.graylog2.shared.rest.resources.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.StringReader;

import static java.util.Objects.requireNonNull;
import static org.graylog2.shared.security.RestPermissions.CLUSTER_CONFIG_ENTRY_READ;

@RequiresAuthentication
@Api(value = "Glpi/Config", description = "Manage glpi settings")
@Path("/glpi/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GLPIPluginConfigurationResource extends RestResource implements PluginRestResource {

    private final ClusterConfigService clusterConfigService;

    private static final int STATUS_OK = 0;

    private static final int STATUS_KO = -1;

    Logger log = LoggerFactory.getLogger(GLPIPluginConfigurationResource.class);

    @Inject
    public GLPIPluginConfigurationResource(final ClusterConfigService clusterConfigService) {
        this.clusterConfigService = requireNonNull(clusterConfigService);
    }

    @GET
    @Timed
    @ApiOperation(value = "Test glpi configuration")
    @RequiresPermissions({CLUSTER_CONFIG_ENTRY_READ})
    public GLPIAuthResponse testConfig() {
        GLPIPluginConfiguration config = clusterConfigService.getOrDefault(GLPIPluginConfiguration.class, GLPIPluginConfiguration.createDefault());
        GLPIConnection connection = new GLPIConnection();
        String message;
        try {
            connection.connectToURL(config.glpiUrl(), config.apiToken());
            JsonReader reader = Json.createReader(new StringReader(connection.getResponseStream().toString()));
            JsonObject jsonObject = reader.readObject();
            if (jsonObject != null) {
                message = "Connection to " + config.glpiUrl() + " with token " + config.apiToken() + " succeed. Succeed connection";
                return new GLPIAuthResponse(STATUS_OK, message);
            }
            else {
                message = "Connection to " + config.glpiUrl() + " with token " + config.apiToken() + " failed. Failed to connect";
                return new GLPIAuthResponse(STATUS_KO, message);
            }
        } catch (Exception e) {
            log.error("GLPI: Impossible to parse {} into json", connection.getResponseStream());
            message = "Impossible to connect to " + config.glpiUrl() + " with token " + config.apiToken() + " and error: " + e.toString() + " Failed to connect";
            return new GLPIAuthResponse(STATUS_KO, message);
        }
    }
}
