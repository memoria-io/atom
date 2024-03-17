#!/bin/bash

listGoals(){
  mvn help:describe -Dcmd="${1}"
}

"$@"
