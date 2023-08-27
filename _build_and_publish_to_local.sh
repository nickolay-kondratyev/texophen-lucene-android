main() {
  ./_build.sh || return 1

  ./_publish_to_local.sh || return 1
}

main || exit 1
