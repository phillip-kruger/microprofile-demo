#!/bin/bash
kill `/usr/bin/jps | grep profiling-swarm.jar | cut -d ' ' -f 1`