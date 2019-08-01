import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';
import GLPIPluginsConfig from 'components/GLPIPluginConfiguration';
import packageJson from '../../package.json';

PluginStore.register(new PluginManifest(packageJson, {
  systemConfigurations: [
    {
      component: GLPIPluginsConfig,
      configType: 'com.airbus_cyber_security.graylog.config.GLPIPluginConfiguration',
    },
  ],
}));
