# install and setup OpenJDK

if ! DEBIAN_FRONTEND=noninteractive apt-get -qqy install openjdk-6-jdk ; then
  echo "failed to install openjdk" ; exit 1
fi

# save env (TODO duplicates entries)
if [ -z "$JAVA_HOME" ] ; then
  # squeeze
  if [ -d /usr/lib/jvm/java-6-openjdk/ ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-6-openjdk/
  fi
  # wheezy
  if [ -d /usr/lib/jvm/java-1.6.0-openjdk-amd64 ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-amd64/
  fi
  echo "export JAVA_HOME=$JAVA_HOME" >> /etc/vidaas/env
fi
