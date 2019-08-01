import PropTypes from 'prop-types';
import React from 'react';
import createReactClass from 'create-react-class';
import { Button } from 'react-bootstrap';
import { BootstrapModalForm, Input } from 'components/bootstrap';
import { IfPermitted } from 'components/common';
import ObjectUtils from 'util/ObjectUtils';

const GLPIPluginConfiguration = createReactClass({
  displayName: 'GLPIPluginConfiguration',

  propTypes: {
    config: PropTypes.object,
    updateConfig: PropTypes.func.isRequired,
  },

  getDefaultProps() {
    return {
      config: {
        glpi_url: 'http://url:port/glpi/apirest.php',
        api_token: 'api token',
      },
    };
  },

  getInitialState() {
    return {
      config: ObjectUtils.clone(this.props.config),
    };
  },

  componentWillReceiveProps(newProps) {
    this.setState({ config: ObjectUtils.clone(newProps.config) });
  },

  _updateConfigField(field, value) {
    const update = ObjectUtils.clone(this.state.config);
    update[field] = value;
    this.setState({ config: update });
  },

  _onCheckboxClick(field, ref) {
    return () => {
      this._updateConfigField(field, this.refs[ref].getChecked());
    };
  },

  _onSelect(field) {
    return (selection) => {
      this._updateConfigField(field, selection);
    };
  },

  _onUpdate(field) {
    return e => {
      this._updateConfigField(field, e.target.value);
    };
  },

  _openModal() {
    this.refs.glpiConfigModal.open();
  },

  _closeModal() {
    this.refs.glpiConfigModal.close();
  },

  _resetConfig() {
    // Reset to initial state when the modal is closed without saving.
    this.setState(this.getInitialState());
  },

  _saveConfig() {
    this.props.updateConfig(this.state.config).then(() => {
      this._closeModal();
    });
  },

  render() {
    return (
      <div>
        <h3>GLPI Plugin Configuration</h3>

        <p>
          Base configuration GLPI plugin (URL and API key). Note that some parameters will be stored in MongoDB without encryption.
          Graylog users with required permissions will be able to read them in the configuration dialog on this page.
        </p>

        <dl className="deflist">
          <dt>GLPI URL:</dt>
          <dd>
            {this.state.config.glpi_url
              ? this.state.config.glpi_url
              : '[not set]'}
          </dd>

          <dt>API Token:</dt>
          <dd>
            {this.state.config.api_token 
              ? this.state.config.api_token
              : '[not set]'}
          </dd>

        </dl>

        <IfPermitted permissions="clusterconfigentry:edit">
          <Button bsStyle="info" bsSize="xs" onClick={this._openModal}>
            Configure
          </Button>
        </IfPermitted>

        <BootstrapModalForm
          ref="glpiConfigModal"
          title="Update GLPI Plugin Configuration"
          onSubmitForm={this._saveConfig}
          onModalClose={this._resetConfig}
          submitButtonText="Save">
          <fieldset>

            <Input
              id="glpi-url"
              type="text"
              label="GLPI URL"
              help={
                <span>
                  GLPI instance URL (http://url:port/glpi/apirest.php).
                </span>
              }
              name="glpi_url"
              value={this.state.config.glpi_url}
              onChange={this._onUpdate('glpi_url')}
            />

            <Input
              id="api-token"
              type="text"
              label="GLPI API Token"
              help={
                <span>
                  Note that this will be stored in plaintext. Please consult the documentation for
                  suggested rights to assign to the underlying IAM user.
                </span>
              }
              name="api_token"
              value={this.state.config.api_token}
              onChange={this._onUpdate('api_token')}
            />
              
          </fieldset>
        </BootstrapModalForm>
      </div>
    );
  },
});

export default GLPIPluginConfiguration;