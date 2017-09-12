# DownloadStationHelper introduction

Application is useful for people who are lazy like me and do not want to manually create torrent tasks.

Application does two separate things:
1) Scan for new torrents,
2) Prepare or create task for DownloadStation on Synology device:
    - creates new torrent tasks via DownloadStation API
    - saves torrent files in directory, which is pointed to automatic scan by DownloadStation.

### How to set up

Whole configuration is located in _application.properties_ file. In order to work user has to set up application 
first. Further in this intro you will find hwo to set up program.

_`application.properties`_ has several parameters that could be changed in different ways.

_`query.page`_ defines how many times request for new torrents is executed. Default value is 1. 
Greater value equals longer program execution time.

_`torrent.age`_ is age of the torrent given in days. This parameter is required on order to have filtering torrents 
by creation date. If not specified then no filtering by date will be applied. If parameter for instance set as **2**
days, then torrents older than 2 days will be filtered out.

_`task.creation.method`_ method of task creation. Possible values: [REST, COPY_FILE]. When REST is set than program
will create torrent task by calling DownloadStation API. With this option program can be run from whole internet.
When COPY_FILE is set, then program will download torrent files and save them in specified directory. `I use this
option when program is launched from LAN`.

When **COPY_FILE** option is used then _torrent.file.location_ is mandatory. This parameter specifies location of 
directory, where downloaded torrents will be saved

When **REST** option is set all below parameters are mandatory.

_`torrent.url.type`_ what link to use in order to create task. Default value is torrent. 
Possible values [magnet,torrent].
_`synology.http.username`_ login to Synology server. If you specified here, it will override username given as run 
parameter.
_`synology.http.password`_ password to Synology server. If you specified here, it will override password given as run 
parameter.
_`synology.server.url`_ url of Synology device. When user wants to use url address from LAN than one may
use for instance `192.168.0.103` (In my LAN this is address of Synlogy server).
_`synology.server.port`_ port used to communication with Synology device. For http is 5000 and for https is 5001.
If 5000 is given then `http` protocol is used otherwise `https` is used. If you do not have valid https certificate
use port 5000, otherwise program will not be able to make request to your Synology server.
_`synology.download.folder`_ location where completed torrents will be saved. Location should start from one of the 
shared folders. For instance when `downloads` is the name of directory then `downloads` is the value of this 
parameter. 

If user wants to see torrent matching result, one can specify _result.filePath_ property. This is location of 
directory where json files with matched torrents will be stored. If not given (value is empty or property deleted) 
than no result will be saved. 
