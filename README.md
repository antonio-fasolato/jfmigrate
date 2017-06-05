# JFMigrate
Java fluent database migration library

## Overview

JFMigration is a library to perform [Database migrations](https://en.wikipedia.org/wiki/Schema_migration).

A Migration is a Java class that specifies with a [Fluent interface](https://en.wikipedia.org/wiki/Fluent_interface) a series of Database modifications (schema and data modifications) that can be performed up (to update a database to a higher version) or down (to downgrade a Database to a previous version). A series of migrations can be collected in a package and applied to any given database (see [Limitations and TODO](#Limitations-and-TODO)), the library will create a special table in the database and register the current applied migration. Each further migration will start with the database current version.

The fluent interface is freely inspired by [FluentMigrator](https://github.com/fluentmigrator/fluentmigrator) (as much as possible working with Java) and will roughly have the same basic functionality.

## Features

- Already implemented
    - Database agnostic (for the supported databases)
    - Pure Java with as little dependencies as possible
- Work in progress (but implemented soon)
    - Migrations are executed directly from Java or can be exported in a database specific script to be executed in production environment or given to a DBA

## Limitations and TODO

- The library is **absolutely not production ready**. The project is still in the first stages of development. That said I am using JFMigrate at work in a series of real life projects, so the library will grow fast.
- One of the first feature I will implement is the possibility to export a list of migrations as a SQL script, where possible with automatic database version checking
- At the moment the library uses [org.reflections](https://code.google.com/archive/p/reflections/). The library is *HUGE* and I am using it only to scan a package by name for all its classes. It will disappear from the dependencies, but at the moment is extremely useful and it speeds up the development greatly, so for a while it will stay
- At the moment I am adding Database engines compatibility as I need it (at the moment only SqlServer and H2), in the future more will be added in a more thorough way
- testing the code is difficult. At the moment is unit-tested on a different package not added to the repository because frankly the code is quit a mess. In the future I will be more diligent and start adding test to the main project

## Build and install

(soon)

## Getting started

(soon)

## Help needed

There is a lot of things to do.

The first thing the project is missing, and something I am *absolutely* incapable of doing by myself is a logo.

If someone more artistically-inclined than me (my 4 year-old daughter already is...) is willing to help, open an issue with a proposition and I will check it as soon as possible.

## Contributing

(soon)

## Motivation and history

JFMigrate is born from my experience with database migrations in Ruby on Rails and C#. I particularly like to work, in C#, with [FluentMigrator](https://github.com/fluentmigrator/fluentmigrator), a straightforward yet powerful library. This project is deeply inspired by FluentMigrator (the Fluent interface is for the most part similar, given the differences between Java and C#).

Recently I started a project mainly written in java and I started looking for a native Java migration library that could compare with tools like FluentMigrator. What I found was quite disappointing (my opinion). I am aware of the various schools of thought about how to implement a Migration library (should the migrations be SQL code, or database agnostic? Should there be a down method in a migration? should the migration be triggered by your code, by Maven, by a standalone application?), this implementation is what I found usable and comfortable for a developer that wants to quickly implement a database migration infrastructure for a single specific project.

It is written as a library, but with 5 lines of code it is possible to create an executable jar file that performs a migration and with a little more work it is possible to script it and integrate it with a generic build system (maven, gradle... your choice).

## License

[Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0)