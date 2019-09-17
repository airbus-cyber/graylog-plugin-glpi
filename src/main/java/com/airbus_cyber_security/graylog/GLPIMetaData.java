package com.airbus_cyber_security.graylog;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

/**
 * Implement the PluginMetaData interface here.
 */
public class GLPIMetaData implements PluginMetaData {
    private static final String PLUGIN_PROPERTIES = "com.airbus-cyber-security.graylog.graylog-plugin-glpi/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "com.airbus-cyber-security.graylog.GLPIPlugin";
    }

    @Override
    public String getName() {
        return "GLPI lookup";
    }

    @Override
    public String getAuthor() {
        return "Airbus CyberSecurity";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.airbus-cyber-security.com");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 1, "unknown"));
    }

    @Override
    public String getDescription() {
        return "Plugin to add pipeline functions to request GLPI API";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(3, 0, 0));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
