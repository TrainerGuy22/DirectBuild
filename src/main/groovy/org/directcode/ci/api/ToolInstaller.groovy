package org.directcode.ci.api

import org.directcode.ci.core.CI

/**
 * A tool installer will install things like Build Systems etc.
 */
abstract class ToolInstaller {
    protected CI ci

    abstract boolean install();

    abstract boolean remove();
}
