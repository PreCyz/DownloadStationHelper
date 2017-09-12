#!/usr/bin/env bash
echo 'Start program'
echo '.............'

java -jar DownloadStationHelper-core-1.0-SNAPSHOT.jar $(cat credentials)

echo '.............'
echo 'Program ended'