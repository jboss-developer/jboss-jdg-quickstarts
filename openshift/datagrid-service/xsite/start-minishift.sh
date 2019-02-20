#!/usr/bin/env bash

set -e

startMinishift() {
  minishift start
  eval $(minishift oc-env)
}

main () {
  ../../registry-credentials-validate.sh

  minishift profile set xsite-a
  startMinishift
  ../../registry-credentials-setup.sh

  minishift profile set xsite-b
  startMinishift
  ../../registry-credentials-setup.sh
}

main
