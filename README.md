# JFMigrate
Java fluent database migration library

## Overview

JFMigration is a library to perform [Database migrations](https://en.wikipedia.org/wiki/Schema_migration).

A Migration is a Java class that specifies with a [Fluent interface](https://en.wikipedia.org/wiki/Fluent_interface) a series of Database modifications (schema and data modifications) that can be performed up (to update a database to a higher version) or down (to downgrade a Database to a previous version). A series of migrations can be collected in a package and applied to any given database (see [Limitations and TODO](#Limitations-and-TODO)), the library will create a special table in the database and register the current applied migration. Each further migration will start with the database current version.

The fluent interface is freely inspired by [FluentMigrator](https://github.com/fluentmigrator/fluentmigrator) (as much as possible working with Java) and will roughly have the same basic functionality.

## Features

- Database agnostic (for the supported databases)
- Pure Java with as little dependencies as possible
- Migrations are executed directly from Java or can be exported in a database specific script to be executed in production environment or given to a DBA

## Limitations and TODO

- The library is **absolutely not production ready**, but somewhat tested in real life applications. The project is still in the first stages of development.
- At the moment I am adding Database engines compatibility as I need it, in the future more will be added in a more thorough way
- testing the code is difficult. At the moment is unit-tested on a different package not added to the repository because frankly the code is quit a mess. In the future I will be more diligent and start adding test to the main project

## Build

JFMigrate uses Java 8 and Maven, so to build it you need to have installed in your machine Java JDK 1.8 and Maven (JFMigrate is built and managed with Maven version 3.3.9)

JFMigrate is managed by Maven, so to generate the jar file with the library you should follow these steps:
- Download JFMigrate from git
    ```text
    git clone https://github.com/antonio-fasolato/jfmigrate.git
    ```
- Change directory into jfmigrate/JFMigrate
    ```text
    cd jfmigrate\JFMigrate
    ```
- Build it
    ```text
    mvn clean package
    ```
The jar file will be in the folder `target`

### Branches and tags

JFMigrate is developed using [git flow](http://nvie.com/posts/a-successful-git-branching-model/), so the branches are organized like this:
- `master`: is aligned with the latest release
- `develop`: is the latest stable development branch (yet still not released, so experimental)
- anything under `feature/`: a current feature being implemented
- tags: each release corresponds to a tag

I am making an exception to this only to edit the documentation in this file, as it would be too cumbersome to prepare a release just to edit this readme.

## Documentation and getting started

All technical documentation will be in the [project wiki](https://github.com/antonio-fasolato/jfmigrate/wiki)

## Help needed

There is a lot of things to do.

The first thing the project is missing, and something I am *absolutely* incapable of doing by myself is a logo.

If someone more artistically-inclined than me (my 6 year-old daughter already is...) is willing to help, open an issue with a proposition and I will check it as soon as possible.

## Contributing

(soon)

## Motivation and history

JFMigrate is born from my experience with database migrations in Ruby on Rails and C#. I particularly like to work, in C#, with [FluentMigrator](https://github.com/fluentmigrator/fluentmigrator), a straightforward yet powerful library. This project is deeply inspired by FluentMigrator (the Fluent interface is for the most part similar, given the differences between Java and C#).

Recently I started a project mainly written in java and I started looking for a native Java migration library that could compare with tools like FluentMigrator. What I found was quite disappointing (my opinion). I am aware of the various schools of thought about how to implement a Migration library (should the migrations be SQL code, or database agnostic? Should there be a down method in a migration? should the migration be triggered by your code, by Maven, by a standalone application?), this implementation is what I found usable and comfortable for a developer that wants to quickly implement a database migration infrastructure for a single specific project.

It is written as a library, but with 5 lines of code it is possible to create an executable jar file that performs a migration and with a little more work it is possible to script it and integrate it with a generic build system (maven, gradle... your choice).

## License

[Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0)