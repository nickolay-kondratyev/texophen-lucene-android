main() {
  echo "--------------------------------------------------------------------------------"
  echo "Running lucene-texophen clean.sh"

  source ./_setup_env.sh || return 1

  rm -rf "${MAVEN_LOCAL_REPO_TEXOPHEN:?}"
  echo "Removed ${MAVEN_LOCAL_REPO_TEXOPHEN}"

  # TODO: build directory has files that are needed for build to work.
  # Look into cleaning out the build directory to see what files
  # are actually needed and then remove files out of source control that
  # are not needed.
  # [[ tm.bug.unresolved.lucene-texophen-has-files-under-build-committed-to-source ]]
  # v8rdwkltursl47jwget7naj
}

main || exit 1
