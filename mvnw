#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# ----------------------------------------------------------------------------
set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

die() {
  printf '%s\n' "$1" >&2
  exit 1
}

case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  ;;
esac

if [ -n "${JAVA_HOME-}" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD="$(command -v java)" || die "JAVA_HOME not set and 'java' not on PATH"
fi
[ -x "$JAVACMD" ] || die "JAVA_HOME or PATH points to invalid java: $JAVACMD"

wrapper_dir="$(dirname "$0")/.mvn/wrapper"
distributionUrl=""
# shellcheck disable=SC1091
. "$wrapper_dir/maven-wrapper.properties"
[ -n "${distributionUrl-}" ] || die "distributionUrl missing in $wrapper_dir/maven-wrapper.properties"

distributionUrlName="${distributionUrl##*/}"
distributionUrlNameMain="${distributionUrlName%.*}"
distributionUrlNameMain="${distributionUrlNameMain%-bin}"
MAVEN_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}/wrapper/dists/${distributionUrlNameMain}"

if [ ! -d "$MAVEN_HOME" ]; then
  die "Maven wrapper not bootstrapped. Run 'mvn -N wrapper:wrapper' first, or use system 'mvn'."
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
