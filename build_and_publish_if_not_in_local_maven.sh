main() {
  source ./_setup_env.sh || return 1

  if [[ ! -d "${MAVEN_LOCAL_REPO_TEXOPHEN}" ]]
  then
  	echo "Directory ${MAVEN_LOCAL_REPO_TEXOPHEN} does NOT exists. Starting rebuild..."
  	./_build_and_publish_to_local.sh || return 1
  else
    echo "Directory ${MAVEN_LOCAL_REPO_TEXOPHEN} exists. Skipping rebuild..."
  fi
}

main || exit 1
