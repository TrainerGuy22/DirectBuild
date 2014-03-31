package org.directcode.ci.source

/**
 * A Version Control System.
 */
interface VCS {
    VCSChangelog changelog(int count);
}