#!/bin/sh

./activator assembly "run 8008"
java -jar target/scala-2.11/finagle-elasticsearch-assembly-1.0-SANTO.jar