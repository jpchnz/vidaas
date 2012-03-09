#!/bin/bash
#
# Creates database roles and stores credential in filesystem. If the creditial
# file already exists this script assues the role has already been created and
# does nothing.
#
# Parameters:
#  1. role-name
#  2. database-name (optional) create database for this role

if [ -n "$1" ] ; then
  role_name=$1
else
  echo "parameter role-name missing" ; exit 1
fi

if [ -n "$2" ] ; then
  database_name=$2
fi

if ! [ -e /etc/vidaas/cred_$role_name ] ; then
  if ! touch /etc/vidaas/cred_$role_name && chmod 600 /etc/vidaas/cred_$role_name ; then
    echo "Failed to create cred file /etc/vidaas/cred_$role_name" ; exit 1
  fi
  credtmp=`pwgen -s 12 1 | tr -d '\n'`
  if [ ${#credtmp} -ne 12 ] ; then
    echo "pwgen failed"
  fi
  if ! echo -n $credtmp >> /etc/vidaas/cred_$role_name ; then
    echo "Failed to create cred file /etc/vidaas/cred_$role_name" ; exit 1
  fi
  if ! su -p -c "psql -c \"CREATE ROLE \\\"$role_name\\\" ENCRYPTED PASSWORD '$credtmp' SUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;\"" postgres ; then
    echo "failed to create $role_name postgres role" ; exit 1
  fi
  credtmp = "12345678901234567890"
  if [ -n "$database_name" ] ; then
    echo "creating database..."
    if ! su -p -c "createdb -T template0 -O $role_name -E UTF8 $database_name" postgres ; then
      echo "failed to create postgres database" ; exit 1
    fi
  fi
fi
