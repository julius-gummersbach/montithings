#!/bin/sh

# cd to root directory /core.
# this is necessary, as we need to copy the rte directory which would have been out of context otherwise
cd ../..

docker build -t monithings.recorder -f services/recorder/Dockerfile .

cd - || exit
