#!/bin/sh

#vars
cred=/etc/vidaas/cred_mysqlroot

# checks
if ! cat $cred > /dev/null ; then
  echo "cannot read credentail file $cred" > /dev/stderr
  exit 1
fi

cat <<EndOfFile
<?xml version="1.0" encoding="UTF-8"?>
<datasources>
   <local-tx-datasource>
      <jndi-name>vidaasDatasource</jndi-name>
      <use-java-context>true</use-java-context>
      <connection-url>jdbc:mysql://localhost:3306/vidaas_v3</connection-url>
      <driver-class>com.mysql.jdbc.Driver</driver-class>
      <user-name>root</user-name>
      <password>`cat $cred`</password>
   </local-tx-datasource>
</datasources>
EndOfFile
