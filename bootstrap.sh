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

if [[ $(echo "ab-cd" | sed 's/-\([a-z]\)/\U\1/g') != "abCd" ]]; then
   echo "ERROR: incompatible version of sed detected." >&2
   exit 1;
fi

repoUserAndName="$1"
repoUser="$2"
repoName="${repoUserAndName/${repoUser}\//}"
aggregateClass=$(echo "${(L)${repoName}}" | sed 's/\([-_]\)\([a-z]\)/\U\2/g' | sed 's/^\([a-z]\)/\U\1/')AggregateDescriptor
modNamePrefix=${(L)${repoName//([_-])/.}}
groupName="io.github.${(L)${repoUser//([_-])/.}}"
rootPackage="$groupName.$modNamePrefix"

# todo: remove
echo "1 = $1"
echo "2 = $2"
echo "repoUserAndName = $repoUserAndName"
echo "repoUser = $repoUser"
echo "repoName = $repoName"
echo "aggregateClass = $aggregateClass"
echo "modNamePrefix = $modNamePrefix"
echo "groupName = $groupName"
echo "rootPackage = $rootPackage"

# sedCode(sedCmd)
function sedCode() {
  find . -type f -not \( -path "./bootstrap.sh" -o -path "./init.sh" -o -path "./init_headless.sh" -o -path "*/.git/*" -o -path "*/build/*" -o -path "*/.gradle/*" \) -print0 | xargs -0 sed -i "$1"
}

# replaceInCode(text-to-replace, replacement)
function replaceInCode() {
  sedCode "s:$1:$2:g"
}


# renamePackage(old-pkg-name, new-pkg-name)
function renamePackage() {
  # Update code:
  replaceInCode "$(echo "$1" | sed 's/\./\\./g')\." "$2."

  # Move code:
  oldBasePattern=$(echo "$1" | sed 's/\./\\\//g')
  oldBaseDir=$(echo "$1" | sed 's/\./\//g')
  newBaseDir=$(echo "$2" | sed 's/\./\//g')

find . -type f -path "*$oldBaseDir*" -not \( -path "./init.sh" -o -path "./init_headless.sh" -o -path "*/.git/*" -o -path "*/build/*" -o -path "*/.gradle/*" \) -exec bash -c '
    newPath=${3/$1/$0}
    mkdir -p "$(dirname $newPath)"
    mv "$3" "$newPath"
    ' "$newBaseDir" "$oldBasePattern" "$oldBaseDir" {} \;
}

echo Prepare
find . -type d -empty -delete

echo Removing test expectation
echo "Topologies:" > example-service/src/test/resources/kafka/streams/expected_topology.txt

echo "Updating repo name to: $repoName"
replaceInCode "creek-service/aggregate-template" "$repoUserAndName"
replaceInCode "aggregate-template" "${(L)${repoName}}"

echo "Updating aggregate descriptor to: $aggregateClass"
replaceInCode "ExampleAggregateDescriptor" "$aggregateClass"
mv "api/src/main/java/org/acme/example/api/ExampleAggregateDescriptor.java" "api/src/main/java/org/acme/example/api/$aggregateClass.java"

echo "Updating root packages to: $rootPackage"
renamePackage "org.acme.example" "$rootPackage"

echo "Updating group name to: $groupName"
replaceInCode "group = \"org.creekservice\"" "group = \"$groupName\""

echo "Updating module names to have prefix: $modNamePrefix"
replaceInCode "example.mod" "$modNamePrefix"

if [ "$repoUser" != "creek-service" ]; then
  echo Updating repo user
  replaceInCode "maven.pkg.github.com/creek-service/" "maven.pkg.github.com/$repoUser/"
fi

echo Deleting Creek specific code
sedCode "/.*init:remove.*/d"
rm -rf system-tests/src/system-test/example-suite

echo Revert workflow changes
# Changing workflows requires elevated privileges, only available via a PAT:
# So revert changes:
git checkout -- ".github/workflows/*"

echo Tidying up
rm ./bootstrap.sh
rm .github/CODEOWNERS
find . -type d -empty -delete
./gradlew format

