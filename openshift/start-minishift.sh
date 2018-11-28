#!/usr/bin/env bash

set -e


startMinishift() {
  minishift start
  eval $(minishift oc-env)
}


main () {
  ./registry-credentials-validate.sh
  startMinishift
  ./registry-credentials-setup.sh
}


main
