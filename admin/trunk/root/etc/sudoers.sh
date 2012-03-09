# generate /etc/sudoers file

# allow debug user to restart jboss
if [ "$debug_user" == "true" ] ; then
	cat > /etc/sudoers <<-EndOfFile
	User_Alias      VIDAAS_DEV = $debug_user
	Cmnd_Alias      JBOSS51 = /etc/init.d/jboss51
	VIDAAS_DEV      ALL = NOPASSWD: JBOSS51
	EndOfFile
fi

chmod 220 /etc/sudoers
if [ ! -e /usr/bin/sudo ] ; then
  apt-get install sudo
fi