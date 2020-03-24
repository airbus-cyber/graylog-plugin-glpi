/*
 * Copyright © - Airbus Defense and Space (SAS)- All Rights Reserved 
 * Airbus Defense and Space owns the copyright of this document. 
 */
import PropTypes from 'prop-types';
import React from 'react';
import Reflux from "reflux";
import createReactClass from 'create-react-class';
import { Button } from 'react-bootstrap';
import { BootstrapModalForm, Input } from 'components/bootstrap';
import { IfPermitted } from 'components/common';
import ObjectUtils from 'util/ObjectUtils';
import GLPIConfigurationsActions from '../config/GLPIConfigurationsActions';
import GLPIConfigurationsStore from "../config/GLPIConfigurationsStore";
import StoreProvider from 'injection/StoreProvider';

const NodesStore = StoreProvider.getStore('Nodes');

const GLPIPluginConfiguration = createReactClass({
	displayName: 'GLPIPluginConfiguration',

	mixins: [Reflux.connect(GLPIConfigurationsStore), Reflux.connect(NodesStore, 'nodes')],

	propTypes: {
		config: PropTypes.object,
		updateConfig: PropTypes.func.isRequired,
	},

	getDefaultProps() {
		return {
			config: {
				glpi_url: 'http://url:port/glpi/apirest.php',
				user_token: 'user token',
				app_token: 'app token',
				heap_size: 100,
				ttl: 60,
				timeout: 500,
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

	_test_connection() {
		GLPIConfigurationsActions.testConfig(this.state.config);
	},

	render() {
		return (
			<div>
				<h3>GLPI Plugin Configuration</h3>

				<p>
					Base configuration GLPI plugin (URL and API key). Note that some parameters will be stored
					in MongoDB without encryption. Graylog users with required permissions will be able
					to read them in the configuration dialog on this page.</p>

				<dl className="deflist">
					<dt>GLPI URL:</dt>
					<dd>
						{this.state.config.glpi_url
							? this.state.config.glpi_url
							: '[not set]'}
					</dd>

					<dt>User API Token:</dt>
					<dd>
						{this.state.config.user_token
							? this.state.config.user_token
							: '[not set]'}
					</dd>

					<dt>App Token:</dt>
					<dd>
						{this.state.config.app_token
							? this.state.config.app_token
							: '[not set]'}
					</dd>

					<dt>Cache heap size:</dt>
					<dd>
						{this.state.config.heap_size
							? this.state.config.heap_size
							: '[not set]'}
					</dd>

					<dt>Cache TTL:</dt>
					<dd>
						{this.state.config.ttl
							? this.state.config.ttl
							: '[not set]'}
					</dd>

					<dt>Timeout:</dt>
					<dd>
						{this.state.config.timeout
							? this.state.config.timeout
							: '[not set]'}
					</dd>
				</dl>

				<IfPermitted permissions="clusterconfigentry:edit">
					<Button bsStyle="info" bsSize="xs" onClick={this._openModal}>Configure</Button>
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
								<span>GLPI instance URL (http://url:port/glpi/apirest.php).</span>
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
								<span>Note that this will be stored in plaintext.
								Please consult the documentation for suggested rights to
								assign to the underlying IAM user.</span>
							}
							name="user_token"
							value={this.state.config.user_token}
							onChange={this._onUpdate('user_token')}
						/>

						<Input
							id="app-token"
							type="text"
							label="GLPI App Token"
							help={
								<span>Note that this will be stored in plaintext.
								Please consult the documentation for suggested rights to
								assign to the underlying IAM user.</span>
							}
							name="app_token"
							value={this.state.config.app_token}
							onChange={this._onUpdate('app_token')}
						/>

						<Input
							id="heap-size"
							type="number"
							label="Cache Heap Size (Mib)"
							help={
								<span> Cache size in Mib. Graylog service restart is needed after change.</span>
							}
							name="heap_size"
							value={this.state.config.heap_size}
							onChange={this._onUpdate('heap_size')}
						/>

						<Input
							id="ttl"
							type="number"
							label="Cache TTL (seconds)"
							help={
								<span>Cache TTL in seconds. Graylog service restart is needed after change.</span>
							}
							name="ttl"
							value={this.state.config.ttl}
							onChange={this._onUpdate('ttl')}
						/>

						<Input
							id="timeout"
							type="number"
							label="Timeout (milliseconds)"
							help={
								<span>Timeout in milliseconds.</span>
							}
							name="timeout"
							value={this.state.config.timeout}
							onChange={this._onUpdate('timeout')}
						/>

						<Button bsStyle="info" bsSize="s" onClick={this._test_connection}>Test</Button>
					</fieldset>
				</BootstrapModalForm>
			</div>
		);
	},
});

export default GLPIPluginConfiguration;
