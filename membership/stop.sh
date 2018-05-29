#!/bin/bash
kill `/usr/bin/jps | grep payara-micro-5.181.jar | cut -d ' ' -f 1`