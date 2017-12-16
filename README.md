# DownloadStationHelper introduction

Application is useful for people who are lazy like me and do not want to create torrent tasks manually.

Application does two separate things:
1) Scan for new torrents,
2) Prepare or create task for DownloadStation on Synology device:
    - creates new torrent tasks via DownloadStation API,
    - saves torrent files in directory. Directory is pointed out for automatic scan by application DownloadStation.

### User Interface

### How to setup application

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

If user wants to see torrent matching result, one can specify _`result.filePath`_ property. This is location of 
directory where json files with matched torrents will be stored. If not given (value is empty or property deleted) 
than no result will be saved. 

### How to specify shows
There is one more property file, that contains setup regarding shows which should be filtered from all torrents.
This file is _`shows.properties`_ and its setup is very simple. There is one mandatory property:
_`show.1.baseWords`_ property defines phrases used to filter out needed torrents. For example there is need to
find _`Seth Meyers 2017 09 12 Emma Roberts 720p HDTV x264-CROOKS`_ show then definition of property 
_`show.1.baseWords`_ might looks like this:

#### `show.1.baseWords=Seth Meyers 2017 09 12,Emma Roberts,720p` 

Important thing is that phrases has to be comma separated. Otherwise it will be only one phrase defined.

Definition of second show the same as first with exception, that instead of `1` in name of property has to be used
`2`. For example:

#### `show.2.baseWords=The Murder of Laci Peterson,S01E04,HDTV`

Similarly is with the next shows. Digit in the name of the property has to be incremented by 1 with each new show.

There is one optional property in this file.
_`show.1.matchPrecision`_ defines how many phrases torrent's title has to contain in order to match the torrent.
If not specified, than match precision is equal to number of commas plus 1 from _show.2.baseWords_

Example:

`show.1.baseWords=Seth Meyers 2017 09 12,Emma Roberts,720p`

`show.1.matchPrecision=2`

It means that if title of each founded torrent has 2 phrases such as ( _Seth Meyers 2017 09 12,Emma Roberts_ ) or 
( _Seth Meyers 2017 09 12,720p_ ) or ( _Emma Roberts,720p_ ), than it will be matched.
So my advice is to choose precision wisely or do not specify at all and leave it with default value.  