#!/usr/bin/env bash
echo 'Start program'
echo '.............'

java -jar DownloadStationHelper-core-1.0.jar $(cat settings/credentials) imdbMode

echo '.............'
echo 'Program ended'