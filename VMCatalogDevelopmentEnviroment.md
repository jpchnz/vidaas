# Introduction #

The VM Catalog is part to VIDaaS and manages VMs using VMwares vCloud SDK (interface to REST interface)

VMware should not be a requirement for the final release, but we will use it right now since the infrastructure available at Oxford is based on VMware. Any virtualisation solution supporting OVF should work with VMCatalog.

# Setup #

## Eclipse Indigo ##

  * Install subclipse via eclipse marketplace
    * Sources are hosted here: https://code.google.com/p/vidaas/source/checkout
  * Install m2e via eclipse marketplace
  * Install Google Plugin for Eclipse

## VCloud SDK ##

see http://communities.vmware.com/community/vmtn/developer/forums/vcloudsdkjava/

Currently we use the 1.0.1 SDK because the 1.5 SDK is not compatible with the current vmware deployment in Oxford. We plan to switch to the 1.5 SDK soon.

# Code #

Use eclipse to checkout `https://vidaas.googlecode.com/svn/ovfcatalog/trunk`

The build process is based on the Maven GWT Plugin.
Documentation: http://mojo.codehaus.org/gwt-maven-plugin/
Goals: http://mojo.codehaus.org/gwt-maven-plugin/plugin-info.html

Before the project can be built in eclipse the GWT's asyc interface classes have to generated by running `mvn gwt:generateAsync`.