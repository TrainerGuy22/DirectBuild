package directbuild.test

import org.directcode.ci.jobs.JobStatus
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.MethodSorters

import static org.junit.Assert.*

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TestJobs extends CITest {

    @Test(timeout = 60000L)
    void testAntJob() {
        def antBuild = ci.build(ci.getJobByName("AntTest"))
        antBuild.waitFor()
        assertTrue(antBuild.complete)
        assertFalse(antBuild.running)
        assertFalse(antBuild.waiting)
        assertEquals(JobStatus.SUCCESS, antBuild.job.status)
    }

    @Test(timeout = 120000L)
    void testMavenJob() {
        def mavenBuild = ci.build(ci.getJobByName("MavenTest"))
        mavenBuild.waitFor()
        assertTrue(mavenBuild.complete)
        assertFalse(mavenBuild.running)
        assertFalse(mavenBuild.waiting)
        assertEquals(JobStatus.SUCCESS, mavenBuild.job.status)
    }

    @Test(timeout = 120000L)
    void testGradleJob() {
        def gradleBuild = ci.build(ci.getJobByName("GradleTest"))
        gradleBuild.waitFor()
        assertTrue(gradleBuild.complete)
        assertFalse(gradleBuild.running)
        assertFalse(gradleBuild.waiting)
        assertEquals(JobStatus.SUCCESS, gradleBuild.job.status)
    }

    @Ignore("Groovy may not be installed on the CI")
    @Test(timeout = 16000L)
    void testGroovyScriptJob() {
        def groovyScriptBuild = ci.build(ci.getJobByName("GroovyScriptTest"))
        groovyScriptBuild.waitFor()
        assertTrue(groovyScriptBuild.complete)
        assertFalse(groovyScriptBuild.running)
        assertFalse(groovyScriptBuild.waiting)
        assertEquals(JobStatus.SUCCESS, groovyScriptBuild.job.status)
    }

    @Test(timeout = 120000L)
    void testDownloadJob() {
        def downloadBuild = ci.build(ci.getJobByName("DownloadTest"))
        downloadBuild.waitFor()
        assertTrue(downloadBuild.complete)
        assertFalse(downloadBuild.running)
        assertFalse(downloadBuild.waiting)
        assertEquals(JobStatus.SUCCESS, downloadBuild.job.status)
    }
}
