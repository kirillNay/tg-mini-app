#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "Usage: $0 <previous-build.gradle.kts> <current-build.gradle.kts>" >&2
    exit 1
fi

compare_identifiers() {
    local left="$1"
    local right="$2"

    if [[ "$left" =~ ^[0-9]+$ && "$right" =~ ^[0-9]+$ ]]; then
        if ((10#$left < 10#$right)); then
            echo -1
        elif ((10#$left > 10#$right)); then
            echo 1
        else
            echo 0
        fi
        return
    fi

    if [[ "$left" =~ ^[0-9]+$ ]]; then
        echo -1
        return
    fi

    if [[ "$right" =~ ^[0-9]+$ ]]; then
        echo 1
        return
    fi

    if [[ "$left" < "$right" ]]; then
        echo -1
    elif [[ "$left" > "$right" ]]; then
        echo 1
    else
        echo 0
    fi
}

compare_prerelease() {
    local left="$1"
    local right="$2"
    local IFS=.
    local -a left_parts=()
    local -a right_parts=()
    local index

    read -r -a left_parts <<< "$left"
    read -r -a right_parts <<< "$right"

    for ((index = 0; index < ${#left_parts[@]} || index < ${#right_parts[@]}; index++)); do
        if ((index >= ${#left_parts[@]})); then
            echo -1
            return
        fi

        if ((index >= ${#right_parts[@]})); then
            echo 1
            return
        fi

        result="$(compare_identifiers "${left_parts[index]}" "${right_parts[index]}")"
        if [[ "$result" != "0" ]]; then
            echo "$result"
            return
        fi
    done

    echo 0
}

compare_versions() {
    local left="${1%%+*}"
    local right="${2%%+*}"
    local left_core="${left%%-*}"
    local right_core="${right%%-*}"
    local left_prerelease=""
    local right_prerelease=""
    local IFS=.
    local -a left_core_parts=()
    local -a right_core_parts=()
    local index left_value right_value

    if [[ "$left" == *-* ]]; then
        left_prerelease="${left#*-}"
    fi

    if [[ "$right" == *-* ]]; then
        right_prerelease="${right#*-}"
    fi

    read -r -a left_core_parts <<< "$left_core"
    read -r -a right_core_parts <<< "$right_core"

    for ((index = 0; index < ${#left_core_parts[@]} || index < ${#right_core_parts[@]}; index++)); do
        left_value="${left_core_parts[index]:-0}"
        right_value="${right_core_parts[index]:-0}"

        if ((10#$left_value < 10#$right_value)); then
            echo -1
            return
        elif ((10#$left_value > 10#$right_value)); then
            echo 1
            return
        fi
    done

    if [[ -z "$left_prerelease" && -z "$right_prerelease" ]]; then
        echo 0
    elif [[ -z "$left_prerelease" ]]; then
        echo 1
    elif [[ -z "$right_prerelease" ]]; then
        echo -1
    else
        compare_prerelease "$left_prerelease" "$right_prerelease"
    fi
}

previous_version="$(bash scripts/ci/read-version.sh "$1")"
current_version="$(bash scripts/ci/read-version.sh "$2")"
comparison_result="$(compare_versions "$previous_version" "$current_version")"

if [[ "$previous_version" == "$current_version" ]]; then
    echo "Version was not changed. Current version is still $current_version." >&2
    exit 1
fi

if [[ "$comparison_result" != "-1" ]]; then
    echo "Version must increase. Found $previous_version -> $current_version." >&2
    exit 1
fi

echo "Version bump validated: $previous_version -> $current_version"
