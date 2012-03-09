# install and setup OpenJDK

if ! DEBIAN_FRONTEND=noninteractive apt-get -qqy install openjdk-6-jdk ; then
  echo "failed to install openjdk" ; exit 1
fi

# save env (TODO duplicates entries)
if [ -z "$JAVA_HOME" ] ; then
  export JAVA_HOME=/usr/lib/jvm/java-6-openjdk/
  echo "export JAVA_HOME=/usr/lib/jvm/java-6-openjdk/" >> /etc/vidaas/env
fi
