source ./_setup_env.sh || return 1

_install_lucene_texophen(){
  artifact_id="${1:?Artifact id is required as first argument for ${FUNCNAME[0]}}"
  jar_file="${2:?Jar file is required as second argument for ${FUNCNAME[0]}}"

  install_if_missing maven mvn

  if [[ ! -f "${jar_file}" ]]
  then
  	echo.red "File ${jar_file} does NOT exists. Have you ran ./_build.sh?"
    return 1
  fi

  mvn install:install-file \
    -Dfile="${jar_file:?}" \
    -DgroupId="org.apache.lucene.texophen_android" \
    -DartifactId="${artifact_id:?}" \
    -Dversion=7.3.0 \
    -Dpackaging=jar \
    -DgeneratePom=true || return 1

  echo "Published ${jar_file} to local maven repo"
}

main() {
  echo "--------------------------------------------------------------------------------"
  echo "Publishing texophen to local maven repo"

  _install_lucene_texophen \
    lucene-core "${LUCENE_TEXOPHEN_DIR:?}/build/core/lucene-core-7.3.0-SNAPSHOT.jar" \
    || return 1

  _install_lucene_texophen \
    lucene-queryparser "${LUCENE_TEXOPHEN_DIR:?}/build/queryparser/lucene-queryparser-7.3.0-SNAPSHOT.jar" \
    || return 1

  echo.green "Done publishing texophen to local maven repo"
}

main || exit 1
