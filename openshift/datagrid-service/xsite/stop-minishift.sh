#!/usr/bin/env bash

set -e


minishift profile set xsite-a
minishift stop

minishift profile set xsite-b
minishift stop
