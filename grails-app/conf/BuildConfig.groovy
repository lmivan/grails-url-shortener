grails.project.work.dir = 'target'

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve true // whether to do a secondary resolve on plugin installation, not advised but set here for backwards compatibility
    repositories {
        grailsCentral()
        mavenCentral()
    }

    plugins {
        build(":tomcat:7.0.55",
              ":release:3.0.1") {
            export = false
        }

        compile(":guard:2.1.0") {
            export = false
        }

        runtime(":hibernate4:4.3.6.1") {
            export = false
        }
    }
}
