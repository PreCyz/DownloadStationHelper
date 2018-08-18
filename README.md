# DownloadStationHelper introduction

Application is useful for people who are lazy like me and do not want to create torrent tasks manually.

Application does following things:
1) Scan for new torrents,
    - user defines titles of torrents to be scanned (recommendation is to use it with TV shows),
    - application scans internet for this torrents,
    - findings are locally stored in json file.
2) Prepare or create task for DownloadStation on Synology device:
    - creates new torrent tasks via Synology DownloadStation API,
    - saves torrent files in directory, directory is pointed out for automatic scan by DownloadStation.
3) Trigger torrent task straight from link. Supported protocols are: (http, https, ftp, ftps, sftp,
magnet, thunder, flashget, qqdl.

_TIP 1:_ Application can download *.torrent file to given location. If user has torrent client like:
BitTorrent, BitComet, BitLord, uTorrent and that client has proper functionality, then user can set
client to scan download folder and automatically launch torrent task.

### User Interface
User interface is simple. If I have time I will add some screen.
Basically it has 3 buttons and defined few shortcuts.
1) 'Download Favourites' - triggers scanning and creating torrent task for defined titles.
2) 'Download from link' - triggers creating torrent task from given link.
3) 'Download by IMDB ID - triggers creating torrent task for given imdb id. Set of imdb ids is kept
locally and it is updated each time 'Download Favourites' is pressed. It can be found at
_'./settings/imdbTitleMap.json'_

Shortcuts are described on main window of UI.

### How to setup application

Whole configuration is located in _application.properties_ file. In order to work user has to set up application 
first. Further in this intro you will find hwo to set up program.

_`application.properties`_ has several parameters that could be changed in different ways.

_`query.page`_ defines how many times request for new torrents is executed. Default value is 1. 
Greater value equals longer program execution time.

_`torrent.age`_ is age of the torrent given in days. This parameter is required on order to have filtering torrents 
by creation date. If not specified then no filtering by date will be applied. If parameter for instance set as **2**
days, then torrents older than 2 days will be filtered out.

_`task.creation.method`_ method of task creation. Possible values: _`REST`_, _`COPY_FILE`_. When REST is set than program
will create torrent task by calling DownloadStation API. With this option program can be run from whole internet.
When COPY_FILE is set, then program will download torrent files and save them in specified directory. `I use this
option when program is launched from LAN`.

When **COPY_FILE** option is used then _torrent.file.location_ is mandatory. This parameter specifies location of 
directory, where downloaded torrents will be saved

When **REST** option is set all below parameters are mandatory.

_`torrent.url.type`_ what link to use in order to create task. Default value is torrent. 
Possible values _`magnet`,`torrent`_.
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
There is one more settings file, that contains shows which should be filtered from all torrents. This file is
_`shows.json`_. Sample content is here:

    {
        "id" : 1,
        "title" : "Game of Thrones",
        "baseWords" : "HDTV,720p,x264",
        "matchPrecision" : 3
    }

It contains _id_, _title_, _baseWords_ and _matchPrecision_ properties.
    -  _id_ simple unique number which starts from 1 added automatically,
    - _title_ title of the torrent (TV show),
    - _baseWards_ additional words that title may contains (name of the codec, resolution etc.),
    - _matchPrecision_ how many words from _title_ and _baseWards_ should torrent contains to be considered
      as matched.

Json is generated automatically by application and can be found at _`./settings/shows.json`_

#### Important
1) The words in _baseWards_ has to be comma separated. Otherwise it will be only one phrase defined.
2) If _matchPrecision_ is not specified, then match precision is equal to number of commas in _baseWards_ plus 1.

Real scenario: I want to find and create torrent task for TV show titled _Game of Thrones_. Show should contain
following additional words: _HDTV,720p,x264_ and only 2 of them should taking in to account while matching torrent.
The json representation of this case is:

    {
        "id" : 1, //created auctomatically
        "title" : "Game of Thrones",
        "baseWords" : "HDTV,720p,x264",
        "matchPrecision" : 2
    }