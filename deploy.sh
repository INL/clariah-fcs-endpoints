mvn package
mvn war:war
sudo cp target/fcs-blacklab-endpoint-0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/blacklab-sru-server.war
