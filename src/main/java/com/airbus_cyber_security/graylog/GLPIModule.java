/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
package com.airbus_cyber_security.graylog;

import java.util.Collections;
import java.util.Set;

import com.airbus_cyber_security.graylog.config.rest.GLPIPluginConfigurationResource;
import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

public class GLPIModule extends PluginModule {
	/**
	 * Returns all configuration beans required by this plugin.
	 *
	 * Implementing this method is optional. The default method returns an empty
	 * {@link Set}.
	 */
	@Override
	public Set<? extends PluginConfigBean> getConfigBeans() {
		return Collections.emptySet();
	}

	@Override
	protected void configure() {
		addMessageProcessorFunction(GLPI.NAME, GLPI.class);
		addRestResource(GLPIPluginConfigurationResource.class);
	}

	protected void addMessageProcessorFunction(String name, Class<? extends Function<?>> functionClass) {
		addMessageProcessorFunction(binder(), name, functionClass);
	}

	public static MapBinder<String, Function<?>> processorFunctionBinder(Binder binder) {
		return MapBinder.newMapBinder(binder, TypeLiteral.get(String.class), new TypeLiteral<Function<?>>() {
		});
	}

	public static void addMessageProcessorFunction(Binder binder, String name,
			Class<? extends Function<?>> functionClass) {
		processorFunctionBinder(binder).addBinding(name).to(functionClass);
	}
}
