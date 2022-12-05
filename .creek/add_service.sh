#!/bin/zsh
#
# Copyright 2022 Creek Contributors (https://github.com/creek-service)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Script for adding a new service module to the repo.
# Usage:
#   add_Service.sh serviceName

if [[ $(echo "ab-cd" | sed 's/-\([a-z]\)/\U\1/g') != "abCd" ]]; then
   echo "ERROR: incompatible version of sed detected." >&2
   echo "If on Mac, 'brew install gnu-sed' and add to path" >&2
   exit 1
fi

creekDir=${0:A:h}
serviceName=$1

if [ -f "$creekDir/bootstrap.sh" ]; then
   echo "bootstrap.sh has not run yet. Re-run once bootstrapping is complete." >&2
   exit 1
fi

if [ -z "$serviceName" ]
then
    echo "serviceName can not be blank" >&2
    exit 1
fi

if [ -d "$serviceName" ]; then
   echo "module already exists" >&2
   exit 1
fi

if [[ "$serviceName" = "example-service" ]]
then
   echo "serviceName can not be 'example-service'." >&2
   exit 1
fi

if ! [[ "$serviceName" =~ ^[0-9a-z]+[0-9a-z-]*[0-9a-z]+$ ]]
then
    echo "invalid serviceName. Only lowercase alpha-numerics and dashes allowed. Can not start or end in dash. serviceName: '$serviceName'." >&2
    exit 1
fi

serviceClass=$(echo "$serviceName" | sed 's/-\([a-z]\)/\U\1/g' | sed 's/^\([a-z]\)/\U\1/')Descriptor
rootPackage=$(<.creek/service_template/root.package)

# sedCode(sedCmd)
function sedCode() {
  find . -type f -not \( -path "./init.sh" -o -path "./init_headless.sh" -o -path "*/.git/*" -o -path "*/build/*" -o -path "*/.gradle/*" -o -path "*/.creek/*" \) -print0 | xargs -0 sed -i "$1"
}

# replaceInCode(text-to-replace, replacement)
function replaceInCode() {
  sedCode "s/$1/$2/g"
}

echo Prepare
find . -type d -empty -delete

echo "Creating $serviceClass"
cp -R "$creekDir/service_template/services" "./"

find . -type f -name "ExampleServiceDescriptor.java" -not \( -path "*/.git/*" -o -path "*/.gradle/*" -o -path "*/.creek/*" \) -exec bash -c '
   newPath="${0/ExampleServiceDescriptor/$1}";
   mv "$0" "$newPath"
 ' {} "$serviceClass" \;

echo "Registering $serviceClass"

if grep -q "provides ComponentDescriptor" "services/src/main/java/module-info.java"; then
  sed -i 's/}/    provides ComponentDescriptor with\n}/g' "services/src/main/java/module-info.java"
fi

sed -i "s/provides ComponentDescriptor with/provides ComponentDescriptor with\n$rootPackage.$serviceClass}/g" "services/src/main/java/module-info.java"

echo "Creating $serviceName module"

cp -R "$creekDir/service_template/example-service" "$serviceName"
replaceInCode "example-service" "$serviceName"
replaceInCode "ExampleServiceDescriptor" "$serviceClass"

echo adding new service module to settings.gradle.kts
sed -i "s/include(/include(\n    \"$serviceName\",/g" settings.gradle.kts

echo Tidy up
find . -type f -name "Keep.java" -not \( -path "*/.git/*" -o -path "*/.gradle/*" \) -exec rm {} \;
find . -type d -empty -delete
./gradlew format