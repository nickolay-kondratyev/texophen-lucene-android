main() {
  install_if_missing.realpath

  export LUCENE_TEXOPHEN_GIT_DIR="$(realpath "$(dirname "$(realpath "$0")")")"
  export LUCENE_TEXOPHEN_DIR="${LUCENE_TEXOPHEN_GIT_DIR:?}"/lucene-7.3.0

  export MAVEN_LOCAL_REPO_TEXOPHEN="${HOME:?}/.m2/repository/org/apache/lucene/texophen_android"
}

main || exit 1
