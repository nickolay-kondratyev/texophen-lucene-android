main() {
  echo "--------------------------------------------------------------------------------"
  echo "Building Lucene Texophen"

  source ./_setup_env.sh || return 1

  cd "${LUCENE_TEXOPHEN_DIR:?}" || return 1

  install_if_missing ant
  ant
}

main || exit 1
