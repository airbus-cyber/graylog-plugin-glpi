# GLPI Connector Pipelines functions

[![Build Status](https://travis-ci.org/airbus-cyber/graylog-plugin-glpi.svg?branch=master)](https://travis-ci.org/airbus-cyber/graylog-plugin-glpi)
[![License](https://img.shields.io/badge/license-GPL--3.0-orange.svg)](https://www.gnu.org/licenses/gpl-3.0.txt)
[![GitHub Release](https://img.shields.io/badge/release-v1.2.1-blue.svg)](https://github.com/airbus-cyber/graylog-plugin-glpi/releases)

## Installation

[Download the plugin](https://github.com/airbus-cyber/graylog-plugin-glpi/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

## Configuration

In the configuration tab of Graylog, you will have to provide:
  * GLPI API URL
  * GLPI User API Token
  * GLPI APP Token
  * Cache Heap Size
  * Cache TTL
 
 You may have to change the order into "Message Processors Configuration". Just switch between "Message Filter Chain" and 	"Pipeline Processor"

## Usage

To search into the Computer category for the source field and display all fields, create a pipeline function like:
```
rule "GLPIComputer"
when
  has_field("source")
then
  let computer = GLPI(to_string($message.source), "Computer", "");
  let computer_fieds = key_value(computer);
  set_fields(computer_fieds, "Computer-");
end
```

If you want to do the same search but displaying only the field called OSName, create a pipeline function like:
```
rule "GLPIComputer"
when
  has_field("source")
then
  let computer = GLPI(to_string($message.source), "Computer", "OSName");
  let computer_fieds = key_value(computer);
  set_fields(computer_fieds, "Computer-");
end
```
## Build

This project is using Maven 3 and requires Java 8 or higher.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

## License

This plugin is released under version 3.0 of the [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.txt).
