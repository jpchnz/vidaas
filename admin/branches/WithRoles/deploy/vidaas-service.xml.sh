cat <<EndOfFile
<mbean code="org.jboss.varia.property.SystemPropertiesService" name="jboss.util:type=Service,name=VIDaaSProperties">
  <attribute name="Properties">
    seamLocaion=$target_dir/seam/
    serverLocation=$JBOSS_HOME
    serverURL=http://`/sbin/ifconfig  | grep 'inet addr:'| grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $1}'`/
    VIDaaSDataLocation=$vidaas_home
    jdbcDriverJar=/usr/share/java/postgresql.jar
    uk.ac.ox.oucs.sudamih.adminUserName = sudamihAdmin
    uk.ac.ox.oucs.sudamih.adminUserNamePW = `cat /etc/vidaas/cred_sudamihAdmin`
    uk.ac.ox.oucs.sudamih.databaseName = sudamihtestdb
    uk.ac.ox.oucs.sudamih.driverName = org.postgresql.Driver
    uk.ac.ox.oucs.sudamih.databaseURL = jdbc:postgresql:
 </attribute>
</mbean>
EndOfFile
