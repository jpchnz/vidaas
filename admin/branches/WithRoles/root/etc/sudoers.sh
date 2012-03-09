# generate /etc/sudoers file

if [ ! -e /usr/bin/sudo ] ; then
  DEBIAN_FRONTEND=noninteractive apt-get -qqy install sudo
fi

# allow debug user to restart jboss
if [ "$debug_user" == "true" ] ; then
	cat > /etc/sudoers <<-EndOfFile
	User_Alias      VIDAAS_DEV = $debug_user
	Cmnd_Alias      JBOSS51 = /etc/init.d/jboss51
	VIDAAS_DEV      ALL = NOPASSWD: JBOSS51
	EndOfFile
fi

chmod 0440 /etc/sudoers

#check syntax
if ! visudo -c ; then
  echo "sudoers syntax check failed" ; exit 1
fi
