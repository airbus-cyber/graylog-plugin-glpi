import Reflux from 'reflux';
import URLUtils from 'util/URLUtils';
import UserNotification from 'util/UserNotification';
import fetch from 'logic/rest/FetchProvider';
import GLPIConfigurationsActions from './GLPIConfigurationsActions';

const GLPIConfigurationsStore = Reflux.createStore({
    listenables: [GLPIConfigurationsActions],
    sourceUrl: '/plugins/com.airbus_cyber_security.graylog/glpi/config',

    testConfig() {
        const promise = fetch('GET', URLUtils.qualifyUrl(this.sourceUrl))
            .then(
                data => {
                    if (data.status_code == 0) {
                        UserNotification.success(data.message);
                    }
                    else {
                        UserNotification.error(data.message);
                    }
                },
                error => {
                    UserNotification.error(`GLPI connection failed with status: ${error}`);
                });
        GLPIConfigurationsActions.testConfig.promise(promise);
    }
});

export default GLPIConfigurationsStore;