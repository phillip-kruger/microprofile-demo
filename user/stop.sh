#!/bin/bash
kill `/usr/bin/jps | grep user.jar | cut -d ' ' -f 1`