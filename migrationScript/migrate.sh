#!/bin/bash

# variables to manually set.
# Don't forget the trailing / in *_MODULE_FOLDER variables
OLD_MODULE_FOLDER="../src/modules-core/"
NEW_MODULE_FOLDER="../modules-core/"
MODULE_NAME="properties" #no trailing slash in module_name

# end variables to set
OLD=$OLD_MODULE_FOLDER$MODULE_NAME
NEW=$NEW_MODULE_FOLDER$MODULE_NAME

##
echo "Start script"
echo "Create a backup folder for the old module if not existing"

BKP=$OLD.bkp

if [ ! -d "$BKP" ]; then
  cp -r $OLD $BKP
  echo "--> backup created"
fi

echo "create pom"
echo "Copy name and description from this file when migration finished"
cat $OLD/config/module.xml

if [ ! -d "$NEW" ]; then
  mkdir -p $NEW
  echo "New module folder created"
fi

if [ ! -f "$NEW/pom.xml" ]; then
  cp ./files/pom.xml $NEW/.
  echo "New pom file installed"
fi

echo "move src files"

########## TODO : a move function that first check if something exist in the old folder, if not, check if the new folder exist, if not, create the new and do the move

N_JAVA=$NEW/src/main/java/
if [ ! -d "$N_JAVA" ]; then
  mkdir -p $N_JAVA
  echo "java folder created"
fi

mv $OLD/java/src/* $N_JAVA
echo "java files moved"

N_PATCHES=$NEW/src/main/resources/META-INF/patches/
if [ ! -d "$N_PATCHES" ]; then
  mkdir -p $N_PATCHES
  echo "patches folder created"
fi

mv $OLD/config/* $N_PATCHES
rm $N_PATCHES/module.xml
echo "patch files moved"

N_JAVA_TEST=$NEW/src/test/java/.
if [ ! -d "$N_JAVA_TEST" ]; then
  mkdir -p $N_JAVA_TEST
  echo "java test folder created"
fi
mv $OLD/java/test/* $N_JAVA_TEST
echo "java test files moved"

N_CANOO_TEST=$NEW/TEST_TO_MIGRATE/.
if [ ! -d "$N_CANOO_TEST" ]; then
  mkdir -p $N_CANOO_TEST
  echo "Canoo test folder created"
fi
mv $OLD/test/canoo/ $N_CANOO_TEST


echo "move all the rest to webressources"

N_WEBRESOURCES=$NEW/src/main/resources/webresources/lenya/modules/$MODULE_NAME/.

if [ ! -d "$N_WEBRESOURCES" ]; then
  mkdir -p $N_WEBRESOURCES
  echo "Canoo test folder created"
fi
mv $OLD/* $N_WEBRESOURCES

echo "Some empty folder where migrated to $N_WEBRESOURCES please check it and remove them manually"
