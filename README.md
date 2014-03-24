# SimpleCI

SimpleCI is a Continuous Integration Server written in [Groovy](http://groovy.codehaus.org/)

[![Build Status](https://travis-ci.org/DirectMyFile/SimpleCI.svg?branch=master)](https://travis-ci.org/DirectMyFile/SimpleCI)
[![Version](http://img.shields.io/github/release/DirectMyFile/SimpleCI.svg)]

## Links

- [Wiki](https://github.com/DirectMyFile/SimpleCI/wiki)
- [Issues](https://github.com/DirectMyFile/SimpleCI/issues)

## Features

- Jobs
- Job Queue
- Job Hooks
- REST API
- Tasks
- SCM
- Plugins
- Web Interface

Plugins can be written in Groovy/JavaScript and can add Task Types and SCM support. SimpleCI has support for Gradle, GNU Make, Commands for tasks and Git for SCMs built-in.

## Building

To build SimpleCI, execute the following command:
```./gradlew jar```

The Jar File will be located at build/libs/SimpleCI.jar

## Running

Execute the following command:
```java -jar SimpleCI.jar```

## Configuration

Edit the config.groovy file to match your configuration.

SimpleCI uses a custom storage system to store all information inclduing Build Status and Build History.
