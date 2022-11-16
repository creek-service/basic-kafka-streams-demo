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
   echo "If on Mac, 'brew install gnu-sed' and add to path" >&2
   exit 1;
fi

succinct=false
useDefaults=false
force=false

while :; do
  case $1 in
    -h|--help)
        echo "$0 [-d] [-s]"
        echo
        echo "   -d, --use-defaults   Use defaults for all questions (also enables -s)"
        echo "   -s, --succinct       Minimise output"
        echo "   -f, --force          Do not prompt for confirmation"
        exit
        ;;
    -s|--succint)
        succinct=true
        ;;
    -d|--use-defaults)
        useDefaults=true
        succinct=true
        ;;
    -f|--force)
        force=true
        ;;
    --)
        shift
        break
        ;;
    -?*)
        echo "WARN: Unknown option (ignored): $1" >&2
        ;;
    *)
        break
  esac

  shift
done

# userOption(var, default, tag, description)
function userOption() {
  if [ "$succinct" = false ]; then
    echo
    echo -e "$4"
  fi
  if [ "$useDefaults" = false ]; then
    eval "read -r \"$1?$3 [$2]: \""
  fi
  eval "$1=\${$1:-$2}"
}

if [ "$succinct" = false ]; then
  echo This script will customise a repository cloned from the tempalte.
  echo WARNING: only run this script once. If there are issues, revert all changes before running again.
  echo
  echo Please answer the following questions to control the customisation:
fi

serviceName=""
serviceClass=""

userOption "serviceName" "example-service" "Service module name" \
 "The template contains a single service. Its name can be customised.\n
 It is recommended the name ends in '-service'.\n
 Additional services can be added by duplicating the service module
 and adding them to settings.gradle.kts
 "

defaultServiceClass=$(echo "$serviceName" | sed 's/-\([a-z]\)/\U\1/g' | sed 's/^\([a-z]\)/\U\1/')Descriptor
userOption "serviceClass" "$defaultServiceClass" "Service class name" \
 "The service descriptor is the class that forms the API of the service within the aggregate.\n
 It is the class that other services in the aggregate will use to access its inputs and outputs.\n
 The default name is based off the service name given above. Or you can provide a custom name.
 "

if [ "$force" = false ]; then
  echo
  echo "About to customise the repository using:"
  echo "Service module name: $serviceName"
  echo "Service class name: $serviceClass"
  echo

  read -rk "confirm?Are you sure? (y/n): "
  if [[ ! $confirm =~ ^[Yy]$ ]]
  then
      exit 1
  fi

  echo
fi

./init_headless.sh \
  --service-name "$serviceName" \
  --service-class "$serviceClass"
