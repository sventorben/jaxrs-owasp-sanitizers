# JAX-RS OWASP Sanitizers

Integration of [OWASP JSON Sanitizer](https://www.owasp.org/index.php/OWASP_JSON_Sanitizer) and [OWASP Java HTML Sanitizer](https://www.owasp.org/index.php/OWASP_Java_HTML_Sanitizer_Project) for JAX-RS.

[![Build Status](https://travis-ci.org/sventorben/jaxrs-owasp-sanitizers.svg?branch=master)](https://travis-ci.org/sventorben/jaxrs-owasp-sanitizers)

[![Code Coverage](https://img.shields.io/codecov/c/github/sventorben/jaxrs-owasp-sanitizers/master.svg)](https://codecov.io/github/sventorben/jaxrs-owasp-sanitizers?branch=master)

## Maven Artifacts

You will need to integrate the Sonatype Maven repositories. 

```
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository
    
    <repository>
        <id>sonatype-nexus-releases</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/releases</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
```

### JSON Sanitizer

To use the OWASP JSON Sanitizer with Maven, add the dependency like this:

```
    <dependency>
        <groupId>de.sven-torben.jaxrs-owasp-sanitizers</groupId>
        <artifactId>json</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```

### HTML Sanitizer

To use the OWASP HTML Sanitizer with Maven, add the dependency like this:

```
    <dependency>
        <groupId>de.sven-torben.jaxrs-owasp-sanitizers</groupId>
        <artifactId>html</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```
