# Introduction #

We use apache's basic auth to secure the development system and set the REMOTE\_USER variable which can be used by vidaas to identify the user.

This is only to be used in development but not for production.

# Instructions #

Link to the apache2 documentation: http://httpd.apache.org/docs/2.1/howto/auth.html

## Create a username/password file ##

This is to create a user for apache as similar as possible to what you'd get from shib:

```
htpasswd -c /opt/VIDaaSData/apache-passwords https//registry.shibboleth.ox.ac.uk/idp\!https//examplesp.org/shibboleth\!/yaoc/E4DdCAkoR/4WvJCn6NTck=
```

The un-escaped username is

```
https//registry.shibboleth.ox.ac.uk/idp!https//examplesp.org/shibboleth!/yaoc/E4DdCAkoR/4WvJCn6NTck=
```

This will prompt you for a password. If you already have a password file don't use '-c' .

## Update your apache config to require BASIC authentication ##

Add this to your apache config (e.g directory you want to secure)

```
        AllowOverride AuthConfig 
        AuthType Basic
        AuthName "Restricted Files"
        AuthBasicProvider file
        AuthUserFile /opt/VIDaaSData/apache-passwords
        Require valid-user
```

and reload apache2

```
service apache2 reload
```