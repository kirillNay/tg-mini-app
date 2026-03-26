#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 1 ]]; then
    echo "Usage: $0 <build.gradle.kts>" >&2
    exit 1
fi

file_path="$1"
version="$(sed -nE 's/^version = "([^"]+)"/\1/p' "$file_path" | head -n 1)"

if [[ -z "$version" ]]; then
    echo "Unable to read version from $file_path" >&2
    exit 1
fi

printf '%s\n' "$version"
