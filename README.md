# [User Guide](https://henryssondaniel.github.io/teacup.github.io/)
[![Build Status](https://travis-ci.com/HenryssonDaniel/teacup-java-engine-junit.svg?branch=master)](https://travis-ci.com/HenryssonDaniel/teacup-java-engine-junit)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=HenryssonDaniel_teacup-java-engine-junit&metric=coverage)](https://sonarcloud.io/dashboard?id=HenryssonDaniel_teacup-java-engine-junit)
[![latest release](https://img.shields.io/badge/release%20notes-1.1.1-yellow.svg)](https://github.com/HenryssonDaniel/teacup-java-engine-junit/blob/master/doc/release-notes/official.md)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.henryssondaniel.teacup.engine/junit.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22io.github.henryssondaniel.teacup.engine%22%20AND%20a%3A%22junit%22)
[![Javadocs](https://www.javadoc.io/badge/io.github.henryssondaniel.teacup.engine/junit.svg)](https://www.javadoc.io/doc/io.github.henryssondaniel.teacup.engine/junit)
## What ##
Custom JUnit 5 engine.  
This engine interacts with the Teacup core project to get fixture data, such as the current fixture
and added clients/servers.
## Why ##
It can be time and resource consuming to use fixtures.  
Therefore this engine will change the order of the tests before executing them so that the minimal
fixture changes are required.
## How ##
This is done by extending the Jupiter engine.  

To use the Teacup engine, do as follows:
1. Create a file named org.junit.platform.engine.TestEngine in src/main/resources/META-INF/services
1. Add the content org.teacup.engine.junit.TeacupTestEngine
1. Add the engine to the build file, this is different depending on the build tool you are using.  
The best thing is to check: https://junit.org/junit5/docs/current/user-guide/#running-tests-build
1. Write your tests as you would normally do with JUnit.