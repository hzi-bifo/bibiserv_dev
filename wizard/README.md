Wizard
======

Description
-----------
The Wizard is a web application to build a BiBiServ application description step by step without writing any code by hand.  The applications guides in an user friendly manner through all required parts needed for a full description.

Requirements
------------
* Maven 3.3 or newer
* Java 8
* any J2EE 7 or newer compatible Java Application Server (e.g. Glassfish, Tomcat)

How to use
----------
Use `maven package` to build a WAR from sources and deploy it on your J2EE application server.
Browse the application using <http://localhost:8080/wizard> when the applications server runs locally and listen on port 8080. A [complete documentation](doc/manual.md) is available within the `doc` folder.