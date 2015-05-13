#!/bin/bash

export FUSE_VERSION=jboss-fuse-6.1.0.redhat-379
export JDG_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\['`

if [ -z "$FUSE_INSTALL_PATH" ]; then
    echo "The variable FUSE_INSTALL_PATH is not set. Exiting..."
    exit
fi
if [ -z "$FUSE_BINARY_PATH" ]; then
    echo "The variable FUSE_BINARY_PATH is not set. Exiting..."
    exit
fi

if [ ! -f $FUSE_BINARY_PATH ]; then
    echo "There is no binary found at the path: $FUSE_BINARY_PATH. Exiting..."
fi

export EXISTING_INSTALL="$FUSE_INSTALL_PATH/$FUSE_VERSION"

echo 
echo "FUSE_INSTALL_PATH=$FUSE_INSTALL_PATH"
echo "FUSE_BINARY_PATH=$FUSE_BINARY_PATH"
echo "FUSE_VERSION=$FUSE_VERSION"
echo "JDG_VERSION=$JDG_VERSION"
echo

####################################################################
#  If any Fuse instances are running stop it
####################################################################
echo "- Stopping any Fuse instance that might be running"
echo
jps -lm | grep karaf | grep -v grep | awk '{print $1}' | xargs kill -KILL

####################################################################
# Removing any existing installation, extracting the binary and 
# adding an admin user with a default password to Fuse installation
####################################################################

if [ -d $EXISTING_INSTALL ]; then 
    echo "- Removing existing installation"
    echo
    rm -rf "$FUSE_INSTALL_PATH/$FUSE_VERSION"
fi

mkdir -p $FUSE_INSTALL_PATH

echo "- Extracting the binary into $FUSE_INSTALL_PATH"
echo
unzip -q $FUSE_BINARY_PATH -d $FUSE_INSTALL_PATH

if [ "$(uname)" = "Linux" ]
then
    sed -i "s/#admin/admin/" $EXISTING_INSTALL/etc/users.properties
else
    sed -i '' "s/#admin/admin/" $EXISTING_INSTALL/etc/users.properties
fi

####################################################################
# Starting JBoss Fuse
####################################################################

echo "- Starting JBoss Fuse"
echo
pushd $EXISTING_INSTALL/bin > /dev/null
./start
popd > /dev/null

echo "- Sleeping for 20 seconds to ensure Fuse has fully started"
echo
sleep 20

####################################################################
# Starting the client 
####################################################################
echo "- Starting the client"
echo
pushd $EXISTING_INSTALL/bin > /dev/null
sh client -r 2 -d 10 "wait-for-service -t 300000 io.fabric8.api.BootstrapComplete" > /dev/null 2>&1
sh client -r 2 -d 10 "fabric:create --clean --wait-for-provisioning --global-resolver=manualip --manual-ip=127.0.0.1 --profile fabric" > /dev/null 2>&1

echo "- Fabric created"
echo
sh client -r 2 -d 10 "fabric:container-create-child --profile=fabric root child 2" > /dev/null 2>&1
sh client -r 2 -d 10 "fabric:profile-edit --pid io.fabric8.agent/org.ops4j.pax.url.mvn.repositories='http://maven.repository.redhat.com/techpreview/all@id=techpreview-all-repository' default" > /dev/null 2>&1 

echo "- Containers child1 and child2 created"
sh client -r 2 -d 10 "fabric:profile-edit --repositories mvn:org.apache.camel/camel-jbossdatagrid/${JDG_VERSION}/xml/features default" > /dev/null 2>&1

sh client -r 2 -d 10 "fabric:profile-edit --repositories mvn:org.jboss.quickstarts.jdg/features/${JDG_VERSION}/xml/features default" > /dev/null 2>&1
sh client -r 2 -d 10 "fabric:profile-create --parents feature-camel --version 1.0 demo-local_producer" > /dev/null 2>&1
sh client -r 2 -d 10 "fabric:profile-create --parents feature-camel --version 1.0 demo-local_consumer" > /dev/null 2>&1

sh client -r 2 -d 10 "fabric:version-create --parent 1.0 --default 1.1" > /dev/null 2>&1

sh client -r 2 -d 10 "fabric:profile-edit --features local-cache-producer demo-local_producer 1.1" > /dev/null 2>&1
sh client -r 2 -d 10 "fabric:profile-edit --features local-cache-consumer demo-local_consumer 1.1" > /dev/null 2>&1

echo "- Applying local_producer profile to child1 and local_consumer profile to child2"
echo
sh client -r 2 -d 10 "fabric:container-add-profile child1 demo-local_producer"
sh client -r 2 -d 10 "fabric:container-add-profile child2 demo-local_consumer"
sh client -r 2 -d 10 "container-upgrade --all 1.1"

popd > /dev/null

echo "Installation Successful!"
