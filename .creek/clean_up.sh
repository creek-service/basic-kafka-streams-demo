#!/bin/bash
#
# Copyright 2022-2023 Creek Contributors (https://github.com/creek-service)
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

# Script for cleaning up repositories created from the template.
# Removes some vestiges of aggregate-repository specific code.
# Usage:
#   bootstrap.sh repoUserAndName repoUser

creekDir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)
baseDir="$(dirname "$creekDir")"

echo Tidying up
rm "$creekDir/clean_up.sh"
rm "$baseDir/.github/workflows/bootstrap.yml"
rm "$baseDir/.github/workflows/gh-pages.yml"
rm "$baseDir/.github/workflows/test-scripts.yml"
find . -type d -empty -delete
./gradlew clean format
