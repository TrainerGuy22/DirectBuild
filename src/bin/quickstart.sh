#!/bin/bash
####################################
# A QuickStart Script for SimpleCI #
# Created by: Kenneth Endfinger    #
####################################
# Options #
USE_PREBUILTS="false"
# End Options #

function check_commands() {
    for cmd in git java; do
        if which ${cmd} > /dev/null 2>&1; then
            # Command Found
            echo -n ""
        else
            # Command not found
            echo "ERROR: Unable to find '${cmd}' on this system. Please install it using your package manager and rerun this script."
            exit 1
        fi
    done
}

function check_java_version() {
    if java -version 2>&1 | awk '/version/ {print $3}' | grep '"1\.8\..*"' > /dev/null 2>&1; then
        # Java Version 8
        echo -n ""
    else
        # Not Java Version 8
        echo "ERROR: Java Version 8 is required."
        exit 1
    fi
}

function build() {
    git clone --recursive --branch master --depth 1 git://github.com/DirectMyFile/SimpleCI.git _build_
    if [[ ${?} -ne 0 ]]; then
        echo "ERROR: Failed to clone SimpleCI."
        exit 1
    fi
    cd _build_
    ./gradlew jar
    if [[ ${?} -ne 0 ]]; then
        echo "ERROR: Failed to build SimpleCI. Falling back to prebuilts."
        download_prebuilts
    fi
    cp -R build/libs/SimpleCI.jar ../SimpleCI.jar
    cd ..
    echo "Cleaning Up..."
    rm -rf _build_
}

function download_prebuilts() {
    wget https://kaendfinger.ci.cloudbees.com/job/SimpleCI/lastSuccessfulBuild/artifact/build/libs/SimpleCI.jar -OSimpleCI.jar
    if [[ ${?} -ne 0 ]]; then
        echo "ERROR: Failed to download prebuilts."
    fi
    chmod a+x SimpleCI.jar # For Good Measure
}

if [[ ${USE_PREBUILTS} == true ]]; then
    download_prebuilts
else
    echo "Checking system for needed commands..."
    check_commands
    echo "Checking Java Version..."
    check_java_version
    echo "Building SimpleCI..."
    build
fi
echo "You may now start SimpleCI by typing 'java -jar SimpleCI.jar'"