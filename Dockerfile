# tomcat image
FROM tomcat:9.0.13-jre10

# remove management folders
RUN rm -rf /usr/local/tomcat/webapps/*

# transfer application
COPY backend/target/backend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# default tomcat port
EXPOSE 8080
