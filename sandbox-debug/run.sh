#!/bin/sh
set -x
g++ -std=gnu++23 -O2 -pipe Main.cpp -o Main 1>/tmp/compile.out 2>/tmp/compile.err
compile_status=$?
if [ "$compile_status" -ne 0 ]; then
  cat /tmp/compile.err 1>&2
  exit 101
fi
timeout -s KILL 1.000s ./Main < input.txt
run_status=$?
if [ "$run_status" -eq 124 ] || [ "$run_status" -eq 137 ]; then
  exit 124
fi
exit "$run_status"
