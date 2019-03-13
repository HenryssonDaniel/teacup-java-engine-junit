# teacup-java-engine-junit
Java **Te**sting Fr**a**mework for **C**omm**u**nication **P**rotocols and Web Services with JUnit

## What ##
Custom JUnit 5 engine.  
This engine interacts with the Teacup core project to get fixture data, such as the current fixture and added clients/servers.

## Why ##
It can be time and resource consuming to use fixtures.  
Therefore this engine will change the order of the tests before executing them so that the minimal fixture changes are required.

## How ##
This is done by extending the Jupiter engine.  

To use the Teacup engine, do as follows:
1. Create a file named org.junit.platform.engine.TestEngine in src/main/resources/META-INF/services
1. Add the content org.teacup.engine.junit.TeacupTestEngine
1. Add the engine to the build file, this is different depending on the build tool you are using.  
The best thing is to check: https://junit.org/junit5/docs/current/user-guide/#running-tests-build
1. Write your tests as you would normally do with JUnit.