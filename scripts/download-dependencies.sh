#!/bin/bash

DOWNLOAD_DEPENDENCIES_PATH="build/plugin-dependencies.txt"

while read dependency || [ -n "$dependency" ]; do
	echo "DOWNLOADING PLUGIN DEPENDENCY: $dependency"
	mvn dependency:copy -q -Dartifact=$dependency -DoutputDirectory=server/plugins
done < "$DOWNLOAD_DEPENDENCIES_PATH"