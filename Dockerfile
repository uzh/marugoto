# tomcat image
FROM tomcat:9.0.13-jre10

# transfer application
COPY backend/target/backend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/marugoto.war

# default tomcat port
EXPOSE 8080

