/*
 *  Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved
 *  Airbus Defense and Space owns the copyright of this document.
 */
package com.airbus_cyber_security.graylog;

import java.util.Collection;
import java.util.Collections;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

/**
 * Implement the Plugin interface here.
 */
public class GLPIPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new GLPIMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new GLPIModule());
    }
}