#!/usr/bin/env bash

set -e


startMinishift() {
  minishift start
  eval $(minishift oc-env)
}


main () {
  local dir=$(dirname $0)

  ${dir}/registry-credentials-validate.sh
  startMinishift
  ${dir}/registry-credentials-setup.sh
}


main
