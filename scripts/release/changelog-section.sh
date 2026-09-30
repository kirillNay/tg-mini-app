#!/usr/bin/env bash
# Prints the CHANGELOG.md section of the given version (without its heading).
set -euo pipefail

if [[ $# -ne 1 ]]; then
    echo "Usage: $0 <version>" >&2
    exit 1
fi

version="$1"
file="$(dirname "$0")/../../CHANGELOG.md"

section="$(awk -v version="$version" '
    /^## \[/ {
        if (found) exit
        if (index($0, "## [" version "]") == 1) { found = 1; next }
    }
    found { print }
' "$file")"

if [[ -z "${section//[[:space:]]/}" ]]; then
    echo "No CHANGELOG.md section for version $version" >&2
    exit 1
fi

printf '%s\n' "$section"
