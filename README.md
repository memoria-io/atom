# atom

[![Release](https://github.com/memoria-io/atom/workflows/Release/badge.svg)](https://github.com/memoria-io/atom/actions?query=workflow%3ARelease)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/memoria-io/atom?label=Version&logo=github)](https://github.com/orgs/memoria-io/packages?repo_name=atom)
[![GitHub commits since latest release (by date)](https://img.shields.io/github/commits-since/memoria-io/atom/latest?logoColor=github)](https://github.com/memoria-io/atom/commits/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=memoria-io_active&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=memoria-io_active)

> هذا العلم والعمل وقف للّه تعالي اسأل اللّه ان يرزقنا الاخلاص فالقول والعمل
>
> This work and knowledge is for the sake of Allah, may Allah grant us sincerity in what we say or do.

## Introduction

* Atom is a collection of utilities for web, and event based applications.
* Current JDK used is `Java 20`.
* Atom relies heavily
  on [Reactive Streams](https://www.reactive-streams.org/) and uses [Project-Reactor](https://projectreactor.io/),
  [Reactor Netty](https://github.com/reactor/reactor-netty), it also uses functional paradigms and collections
  from [Vavr](https://www.vavr.io/).

**Disclaimer:**
> `atom` is on edge, it's a work in progress and a pragmatic learning effort, so feel free to create issues or PRs.

## Usage

First make sure you can fetch repositories under such memoria organisation from github

```xml

<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/memoria-io/*</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

```

Then import nomrally in your pom dependencies

```xml

<dependency>
    <groupId>io.memoria</groupId>
    <artifactId>atom</artifactId>
    <version>20.1.0</version>
</dependency>
```

## Versioning

The versioning is similar to semantic but with a shift where the first segment being the jdk version.

Format Example: `JDK_Version.major.mino`

## Atom Features

* Eventsourcing sdk in two flavours active, and reactive
* Kafka, Nats adapters
* Virtual threading
* Vavr + Reactor Functional utilities
* Reactor Netty utility functions
* Reactive functional in-memory generic cruds (for tests, not for production)
* Jackson Adapter (Json & Yaml) utilities
* FileOps reader utility
* ResourceFileOps utility
* ConfigFileOps is a module for reading yaml configuration files (depends on Jackson Adapter)
    * Allows nesting of files using a marker e.g `include: sub_file.yaml` would replace this line with content
      of `sub_file.yaml`
    * Reading as a system property if not found as environment variable or else the default value if it was supplied:
        * `path: ${JAVA_HOME}`
        * `myVar: ${SOME_VAR:-defaultValue}`

# Architecture

![](docs/atom.jpg)

## Core module

Core module has basic set of utilities:

* Basic Eventsourcing functional interfaces
* ConfigFileOps, and ResourceFileOps (similar to Typesafe HOCOON library)
* TextTransformer base interfaces

## Adapter-* modules

Adapter initially were built to implement eventsourcing infra ports, but might later have more

## TODOs

* [x] Event Sourcing
    * [x] State decider, evolver, Stream commandAggregate
    * [x] Sagas decider, Stream commandAggregate
    * [x] id safety with typed classed (StateId, CommandId, EventId)
    * [x] Events reduction
        * If using reduction the event reducer should map all states to creation event
        * Init states can't have creation events.
    * [x] Stream sharding to be used later for scaling
        * [x] Tests
        * Due to sharding (reading from **multiple** src event streams) the whole cluster should be down first before
          executing sharding, so that oldStreams are not receiving new events, while being ingested, they should be in
          read
          only state
* [x] Streaming
    * [x] Stream api for usage in event sourcing
* [ ] Increase test coverage to >85%

## Release notes

* `20.1.0` Fixing Jacoco and sonar to work with `--enable-preview`

## Contribution

You can just do pull requests, and I will check them asap.

## Related Articles

* [Error handling using Reactor and VAVR](https://marmoush.com/2019/11/12/Error-Handling.html)
* [Why I stopped using getters and setters](https://marmoush.com/2019/12/13/stopped-using-getters-and-setters.html)
