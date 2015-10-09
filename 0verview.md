# Overview #

## VIDaaS ##

This component implements the web front-end end and database management functionality. It is a Java webapp using the JBoss Seam framework (http://seamframework.org/). It was original developed as part of the sudamih project (http://sudamih.oucs.ox.ac.uk/).

![https://vidaas.googlecode.com/svn/wiki/vidaas.png](https://vidaas.googlecode.com/svn/wiki/vidaas.png)

It manages the individual projects and stores it's configuration, metadata, etc in a locally installed MySQL database.

When a user requests to create a DataInterface (a web-frontent to access a project database) uses seam-gen to dynamically generate a new webapp and deploys it to the application server.


You can browse the code here: https://code.google.com/p/vidaas/source/browse/#svn%2Fvidaas3

## jQuerySQLDesigner ##

Is a tool to graphically design databases using JavaScript.

Documentation can be found here: http://daas-7.ords.ox.ac.uk/jQuerySQLDesigner/home.seam

## VMware integration ##

We have developed a tool to automatically create and deploy VMs based on VMware's VCloud API. This works fine but is currently not integrated with the VIDaaS webapp so this functionality is currently not directly available to the end user.

![https://vidaas.googlecode.com/svn/wiki/vmware.png](https://vidaas.googlecode.com/svn/wiki/vmware.png)

Currently it provides a simple web interface to start/stop virtual machines and create new ones from templates. This interface is only to be used for testing purposes.

This functionality is also exposed through google RPC and JSON and will be called by the VIDaaS webapp to dynamically create VIDaaS instances. Currently this is only used to test VCloud API integration.

Source code: https://code.google.com/p/vidaas/source/browse/#svn%2Fovfcatalog%2Ftrunk

## Creating Images ##

Images are created automatically by scripts, but there is a (no longer updated) wiki page explaining how to create an image manually. It is still useful to get an overview how to create an image.

  * Old instructions: ViDaasSetup
  * Automated scripts: https://code.google.com/p/vidaas/source/browse/#svn%2Fadmin%2Ftrunk%2Fscripts

![https://vidaas.googlecode.com/svn/wiki/images.png](https://vidaas.googlecode.com/svn/wiki/images.png)

## PostgreSQL ##

Currently 3 databases can be created for every project. This allows to use a different version of the database for production and development work.

![https://vidaas.googlecode.com/svn/wiki/versions.png](https://vidaas.googlecode.com/svn/wiki/versions.png)

Currently the database server is installed on the same VM as the VIDaaS web interface but there is not required.

# Software used #

For a list of other software used in the project and their licenses see here: Licensing

# Future Work #

(in order of prioritisation)

### Update to JBoss 7 ###

JBoss 5.1 is no longer maintained and cannot be built from source. A lot of the VIDaaS code has to be rewritten to work with the newest version of Seam and JBoss. At the same time we will introduce architectural changes to the application, mainly splitting the VIDaaS webapp into independent components.

### VMware integration ###

Need to be updated (add support for initalisation script) and integrated with ViDaas.

### NAT/PAT ###

Allow deployment of multiple nodes on the same IP address.

### OpenStack integration ###

Add support for OpenStack.