#!/bin/bash
#

cat <<EndOfFile
<?xml version="1.0"?>

<!--===== a sample Maven 2 user settings file for the RHQ build =====-->

<!-- See: http://maven.apache.org/settings.html -->

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">

   <!--**************************** PROFILES ****************************-->

   <!-- See: http://maven.apache.org/guides/introduction/introduction-to-profiles.html
        and: http://docs.codehaus.org/display/MAVENUSER/Profiles -->

   <activeProfiles>
      <!-- Always enable the 'overrides' profile, where we override various build properties. -->
      <activeProfile>overrides</activeProfile>

      <!-- The 'dev' profile enables automatic copying of artifacts to a dev RHQ container. -->
      <!--<activeProfile>dev</activeProfile>-->

      <!-- The 'check-java-api' profile can be used to build RHQ using JDK6, while still enforcing that
           only Java 5 APIs are used - see the 'check-java-api' profile block below. -->
      <!--<activeProfile>check-java-api</activeProfile>-->

      <!-- The 'dist' profile is for release builds and enables things such as JSP precompilation. -->
      <activeProfile>dist</activeProfile>

      <!-- Profiles to configure database connection settings. Use separate profiles for
           Postgres and Oracle to allow easily switching back and forth between them. -->
      <activeProfile>postgres</activeProfile>
   </activeProfiles>

   <profiles>

      <profile>
         <id>overrides</id>
         <properties>
            <!-- NOTE: Add properties here that you always want set, to avoid
                       having to explicitly pass them on the command line. Note,
                       you can still override the values set here via the command
                       line. -->

            <!-- *** RHQ-specific properties *** -->
            <!-- tells the dbutils tests not to fail -->
            <DatabaseTest.nofail>true</DatabaseTest.nofail>

            <!-- Tests that should be skipped (e.g. because they're long running), comma-separated (with no whitespace!) -
                 some possible values: agent-comm,comm-client,native-system,postgres-plugin -->
            <rhq.testng.excludedGroups>agent-comm,comm-client,native-system,postgres-plugin</rhq.testng.excludedGroups>

            <!-- *** enforcer plugin settings *** -->
            <!--<enforcer.skip>true</enforcer.skip>-->

            <!-- *** surefire plugin settings *** -->
            <!--<skipTests>true</skipTests>-->
            <!--<maven.test.skip>true</maven.test.skip>-->
            <!--<maven.test.failure.ignore>true</maven.test.failure.ignore>-->
            <!--<maven.test.error.ignore>true</maven.test.error.ignore>-->
            <!--<surefire.useFile>false</surefire.useFile>-->

            <!-- *** release plugin settings *** -->
            <!-- Set the below properties to your fedorahosted.org username and
                 password (used to check in poms to RHQ git). -->
            <username>SET_ME</username>
            <password>SET_ME</password>
         </properties>

         <repositories>

            <!-- Oracle Driver
                 To package the Oracle OJDBC driver set -Drhq.m2.repo.url.oracle and this repo definition will
                 be used to pick it up at build time. -->
            <repository>
               <id>ojdbc-repo</id>
               <name>M2 Repo containing OJDBC driver jar</name>
               <url>\${rhq.m2.repo.url.oracle}</url>
            </repository>

         </repositories>

         <pluginRepositories>

            <!-- Oracle Driver
                 To package the Oracle OJDBC driver set -Drhq.m2.repo.url.oracle and this repo definition will
                 be used to pick it up at build time. -->
            <pluginRepository>
               <id>ojdbc-repo</id>
               <name>M2 Repo containing OJDBC driver jar</name>
               <url>\${rhq.m2.repo.url.oracle}</url>
            </pluginRepository>

         </pluginRepositories>

      </profile>

      <profile>
         <id>postgres</id>
         <properties>
            <rhq.test.ds.connection-url>jdbc:postgresql://127.0.0.1:5432/rhqtest</rhq.test.ds.connection-url>
            <rhq.test.ds.user-name>rhqadmintest</rhq.test.ds.user-name>
            <rhq.test.ds.password>`cat /etc/vidaas/cred_rhqadmintest`</rhq.test.ds.password>
            <rhq.test.ds.type-mapping>PostgreSQL</rhq.test.ds.type-mapping>
            <rhq.test.ds.driver-class>org.postgresql.Driver</rhq.test.ds.driver-class>
            <rhq.test.ds.xa-datasource-class>org.postgresql.xa.PGXADataSource</rhq.test.ds.xa-datasource-class>
            <rhq.test.ds.server-name>127.0.0.1</rhq.test.ds.server-name>
            <rhq.test.ds.port>5432</rhq.test.ds.port>
            <rhq.test.ds.db-name>rhqtest</rhq.test.ds.db-name>
            <rhq.test.ds.hibernate-dialect>org.hibernate.dialect.PostgreSQLDialect</rhq.test.ds.hibernate-dialect>
            <!-- quartz properties -->
            <rhq.test.quartz.driverDelegateClass>org.quartz.impl.jdbcjobstore.PostgreSQLDelegate</rhq.test.quartz.driverDelegateClass>
            <rhq.test.quartz.selectWithLockSQL>SELECT * FROM {0}LOCKS ROWLOCK WHERE LOCK_NAME = ? FOR UPDATE</rhq.test.quartz.selectWithLockSQL>
            <rhq.test.quartz.lockHandlerClass>org.quartz.impl.jdbcjobstore.StdRowLockSemaphore</rhq.test.quartz.lockHandlerClass>
            
            <rhq.dev.ds.connection-url>jdbc:postgresql://127.0.0.1:5432/rhqdev</rhq.dev.ds.connection-url>
            <rhq.dev.ds.user-name>rhqadmindev</rhq.dev.ds.user-name>
            <rhq.dev.ds.password>`cat /etc/vidaas/cred_rhqadmindev`</rhq.dev.ds.password>
            <rhq.dev.ds.type-mapping>PostgreSQL</rhq.dev.ds.type-mapping>
            <rhq.dev.ds.driver-class>org.postgresql.Driver</rhq.dev.ds.driver-class>
            <rhq.dev.ds.xa-datasource-class>org.postgresql.xa.PGXADataSource</rhq.dev.ds.xa-datasource-class>
            <rhq.dev.ds.server-name>127.0.0.1</rhq.dev.ds.server-name>
            <rhq.dev.ds.port>5432</rhq.dev.ds.port>
            <rhq.dev.ds.db-name>rhqdev</rhq.dev.ds.db-name>
            <rhq.dev.ds.hibernate-dialect>org.hibernate.dialect.PostgreSQLDialect</rhq.dev.ds.hibernate-dialect>
            <!-- quartz properties -->
            <rhq.dev.quartz.driverDelegateClass>org.quartz.impl.jdbcjobstore.PostgreSQLDelegate</rhq.dev.quartz.driverDelegateClass>
            <rhq.dev.quartz.selectWithLockSQL>SELECT * FROM {0}LOCKS ROWLOCK WHERE LOCK_NAME = ? FOR UPDATE</rhq.dev.quartz.selectWithLockSQL>
            <rhq.dev.quartz.lockHandlerClass>org.quartz.impl.jdbcjobstore.StdRowLockSemaphore</rhq.dev.quartz.lockHandlerClass>
         </properties>
      </profile>

      <!-- This can be used to build RHQ using JDK6, while still enforcing that
           only Java 5 APIs are used. -->
      <profile>
         <id>check-java-api</id>
         <properties>
            <!-- Set this to your a JRE5 installation dir (e.g. /usr/java/jdk-1.5.0_21/jre). -->
            <java5.home>/usr/java/jdk-1.5.0_21/jre</java5.home>
         </properties>
      </profile>

      <profile>
         <id>dev</id>
         <properties>
            <!-- Set the below prop to the absolute path of your RHQ trunk dir (e.g. /home/bob/projects/rhq).
                (\${rhq.rootDir}/dev-container will be used as the dev container dir) -->
            <rhq.rootDir>SET_ME</rhq.rootDir>

            <!-- Alternatively, if you don't want to use the default location of {rhq.rootDir}/dev-container/
                 for your dev container, then set the below prop to the desired location. -->
            <!--<rhq.containerDir>C:/home/bob/rhq-dev-container</rhq.containerDir>-->

            <!-- Set the below prop to the location to your dev JBAS 4.2/4.3 deploy location for the admin console WAR.
                 e.g.: C:/opt/jboss-eap-4.3.0.GA_CP02/jboss-as/server/production -->
            <jbas4.configDir>SET_ME</jbas4.configDir>
            <!-- Set the below prop to the location to your dev JBAS 5.0 deploy location for the embedded console WAR.
                 e.g.: C:/opt/jboss-5.0.0.CR1/server/default -->
            <jbas5.configDir>SET_ME</jbas5.configDir>

             <!-- set browser(s) to compile the GWT stuff for
               ~~ See http://rhq-project.org/display/RHQ/Advanced+Build+Notes#AdvancedBuildNotes-GWTCompilationForDifferentBrowsers
               ~~ for details and browser versions
               ~~ Possible engines are ie6,ie8,gecko,gecko1_8,safari,opera
             -->
             <gwt.userAgent>SET_ME</gwt.userAgent>
             <!-- Only gwt-compile JavaScript for FF2 and later. -->
             <!--<gwt.userAgent>gecko1_8</gwt.userAgent>-->
             <!-- Enable faster, but less-optimized, gwt compilations. -->
             <gwt.draftCompile>true</gwt.draftCompile>
             <!-- Only compile the default locale. -->
             <gwt.locale>default</gwt.locale>
             <!-- If user specifies an unsupported locale, fallback to the default locale. -->
             <gwt.fallback.locale>default</gwt.fallback.locale>
         </properties>
      </profile>

      <profile>
         <id>publish</id>
         <properties>
            <confluenceUrl>http://docs.example.com/confluence/</confluenceUrl>
            <confluenceSpace>DOCS</confluenceSpace>
            <confluenceUserName>SET_ME</confluenceUserName>
            <confluencePassword>SET_ME</confluencePassword>
         </properties>
      </profile>

   </profiles>


   <!--**************************** PLUGIN GROUPS ****************************-->

   <!-- See: http://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html -->
   <pluginGroups>
      <!--  This will allow you to invoke "org.rhq" Maven goals using the "rhq" prefix -->
      <pluginGroup>org.rhq</pluginGroup>
   </pluginGroups>


   <!--**************************** SERVERS ****************************-->

   <!-- See: http://maven.apache.org/settings.html#Servers
        and: http://maven.apache.org/guides/mini/guide-wagon-providers.html -->

   <servers>

    <server>
      <id>jboss-developer-repository-group</id>
      <!-- jboss.org username and password -->
      <username>SET_ME</username>
      <password>SET_ME</password>
    </server>

    <server>
      <id>jboss-snapshots-repository</id>
      <!-- jboss.org username and password -->
      <username>SET_ME</username>
      <password>SET_ME</password>
    </server>

    <server>
      <id>jboss-releases-repository</id>
      <!-- jboss.org username and password -->
      <username>SET_ME</username>
      <password>SET_ME</password>
    </server>

   </servers>


   <!--**************************** MIRRORS ****************************-->

   <!-- Uncomment the below if you want to use the JBoss Nexus repo as a proxy
        to the central, java.net, etc. repos. -->
<!--
   <mirrors>
    <mirror>
      <id>jboss-developer-repository-group</id>
      <mirrorOf>*,!jboss-deprecated-repository,!ojdbc-repo</mirrorOf>
      <name>JBoss Developer Maven Repository Group</name>
      <url>https://repository.jboss.org/nexus/content/groups/developer/</url>
    </mirror>
   </mirrors>
-->

</settings>
EndOfFile
