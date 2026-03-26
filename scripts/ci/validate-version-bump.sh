#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "Usage: $0 <previous-build.gradle.kts> <current-build.gradle.kts>" >&2
    exit 1
fi

previous_version="$(bash scripts/ci/read-version.sh "$1")"
current_version="$(bash scripts/ci/read-version.sh "$2")"

if [[ "$previous_version" == "$current_version" ]]; then
    echo "Version was not changed. Current version is still $current_version." >&2
    exit 1
fi

highest_version="$(printf '%s\n%s\n' "$previous_version" "$current_version" | sort -V | tail -n 1)"

if [[ "$highest_version" != "$current_version" ]]; then
    echo "Version must increase. Found $previous_version -> $current_version." >&2
    exit 1
fi

echo "Version bump validated: $previous_version -> $current_version"
