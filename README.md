# Project 1 - Custom Object Relational Mapping Framework

## Description

My first project with Revature was to create a custom object relational mapping (ORM) framework. This framework allows for a simplified and SQL-free interaction with the relational data source. To this end I developed a package (orm_nluther) that utilizes the Reflections API to analyze arbitrary Java objects to convert them to a form that can be persisted to and retrieved from an RDS. Additionally I made another package (otherpart_nluther) that utilizes the Java EE Servlet API to expose endpoints that allow a user to remotely utilize the orm_nluther package. 

## Tech Stack

- [X] Java 8
- [X] Apache Maven
- [X] Jackson library (for JSON marshalling/unmarshalling)
- [X] Java EE Servlet API (v4.0+)
- [X] PostGreSQL deployed on AWS RDS
- [X] Git SCM (on GitHub)

## Functional Requirements

- [X] CRUD operations are supported for one or more domain objects via the web application's exposed endpoints
- [X] JDBC logic is abstracted away by the custom ORM
- [X] Programmatic persistence of entities (basic CRUD support) using custom ORM
- [X] File-based or programmatic configuration of entities

## Non-Functional Requirements

- [X] Custom ORM source code should be included within the web application as a Maven dependency
