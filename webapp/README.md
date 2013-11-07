# Current working state : 

* Import existing publication don't work (result of imported document is not okay)
* Create a new publication from scratch work
* Modules (for editing, etc,...) don't work : 
** need to re-enable the maven-patch module with retoring the "touch" goal in the maven-webapp pom build section.

# deploying in J2E container

## Jetty 

* mvn jetty:run

* Just list the web-inf content. No start.
* Seems to be based on the src/webapp/WEB-INF directory and not the target/WEB-inf one

## Tomcat 7 

* method 1 : copy-past the .war to the tomcat webapp directory : 
** works at the state of the art

* method 2 : mvn tomcat7:run
** blank page on localhost:8080 and localhost:8080/lenya
** may pom configuration bits missing

* method 3 : directly publish to tomcat 7
** mvn3 package tomcat7:deploy -o
** require tomcat-7 already running and manager installed in it (with login/mp defined ?)

* others methods to see and check : http://tomcat.apache.org/maven-plugin-2.0/context-goals.html

## with tomcat 6 

* may work as tomcat 7

## with tomcat 5.5.29

* copy the target/lenya.war to the tomcat/webapp folder and then start the Tomcat/

## standalone WAR : 

* retry it !

# for selenium tests : 
* see the end of this document : http://tomcat.apache.org/maven-plugin-2.0/run-mojo-features.html

