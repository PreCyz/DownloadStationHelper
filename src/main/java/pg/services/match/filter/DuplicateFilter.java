package pg.services.match.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.util.StringUtils;
import pg.web.torrent.Duplicable;
import pg.web.torrent.TorrentDetail;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/** Created by Gawa 2017-10-30 */
class DuplicateFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(DuplicateFilter.class);

    private final int MAX_DIFFERENCE_COUNTER = 2;
    private final int NO_SE_IDX = -1;

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        List<TorrentDetail> filteredOut = new LinkedList<>();
        for (TorrentDetail torrent : torrents) {
            if (filteredOut.isEmpty()) {
                filteredOut.add(torrent);
            } else {
                List<TorrentDetail> list = new LinkedList<>(filteredOut);
                boolean duplicate = false;
                for (TorrentDetail torrentFromList : list) {
                    //check for duplicates only when different episodes and seasons
                    if (isDuplicate(torrent, torrentFromList)) {
                        duplicate = true;
                        if (Long.valueOf(torrentFromList.getSize()) < Long.valueOf(torrent.getSize())) {
                            filteredOut.remove(torrentFromList);
                            filteredOut.add(torrent);
                        }
                        break;
                    }
                }
                if (!duplicate) {
                    filteredOut.add(torrent);
                }
            }
        }
        logger.info("Duplicate filter applied. {} torrents were filtered out. {} torrents remained.",
                torrents.size() - filteredOut.size(), filteredOut.size());
        return filteredOut;
    }

    protected boolean isDuplicate(Duplicable torrent, Duplicable torrentFromList) {
        return !StringUtils.nullOrTrimEmpty(torrent.getSeason())
                && !StringUtils.nullOrTrimEmpty(torrent.getEpisode())
                && torrent.getSeason().equalsIgnoreCase(torrentFromList.getSeason())
                && torrent.getEpisode().equalsIgnoreCase(torrentFromList.getEpisode())
                && isTitlesDuplicate(torrentFromList.getTitle(), torrent.getTitle());
    }

    protected boolean isTitlesDuplicate(String titleFromList, String currentTitle) {
        if (titleFromList.equalsIgnoreCase(currentTitle)) {
            return true;
        }
        String[] splitTitleFromList = titleFromList.split(" ");
        String[] splitCurrentTitle = currentTitle.split(" ");

        return isSameTitleSection(splitTitleFromList, splitCurrentTitle) &&
                isSameSeasonEpisodeSection(splitTitleFromList, splitCurrentTitle); /*&&
                isSameVersionSection(splitTitleFromList, splitCurrentTitle);*/
    }

    private boolean isSameTitleSection(String[] splitTitleFromList, String[] splitCurrentTitle) {
        boolean result = false;
        int seTitleFromIdx = getSeasonEpisodeIndex(splitTitleFromList);
        int seCurrentTitleIdx = getSeasonEpisodeIndex(splitCurrentTitle);
        if (seTitleFromIdx != NO_SE_IDX && seCurrentTitleIdx != NO_SE_IDX) {
            List<String> titleSection = new LinkedList<>();
            List<String> currentTitleSection = new LinkedList<>();
            Arrays.spliterator(splitTitleFromList, 0, seTitleFromIdx).forEachRemaining(titleSection::add);
            Arrays.spliterator(splitCurrentTitle, 0, seCurrentTitleIdx).forEachRemaining(currentTitleSection::add);
            result = currentTitleSection.containsAll(titleSection);
        }
        return result;
    }

    protected int getSeasonEpisodeIndex(String[] title) {
        int result = -1;
        for (int i = 0; i < title.length; i++) {
            if (isSeasonEpisode(title[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    private boolean isSeasonEpisode(String string) {
        Predicate<String> containsSE = word -> word.length() == 6 &&
                word.charAt(0) == 'S' && Character.isDigit(word.charAt(1)) && Character.isDigit(word.charAt(2)) &&
                word.charAt(3) == 'E' && Character.isDigit(word.charAt(4)) && Character.isDigit(word.charAt(5));
        return containsSE.test(string);
    }

    private boolean isSameSeasonEpisodeSection(String[] splitTitleFrom, String[] splitCurrentTitle) {
        Optional<String> seTitle = Arrays.stream(splitTitleFrom).filter(this::isSeasonEpisode).findFirst();
        Optional<String> seCurrent = Arrays.stream(splitCurrentTitle).filter(this::isSeasonEpisode).findFirst();
        return seTitle.isPresent() && seCurrent.isPresent() && seTitle.get().equalsIgnoreCase(seCurrent.get());
    }

    private boolean isSameVersionSection(String[] splitTitleFromList, String[] splitCurrentTitle) {
        int seTitleFromIdx = getSeasonEpisodeIndex(splitTitleFromList);
        int seCurrentTitleIdx = getSeasonEpisodeIndex(splitCurrentTitle);
        if (seTitleFromIdx == NO_SE_IDX || seCurrentTitleIdx == NO_SE_IDX) {
            return false;
        }

        boolean result = true;
        List<String> versionSection = new LinkedList<>();
        List<String> currentVersionSection = new LinkedList<>();
        Arrays.spliterator(splitTitleFromList, seTitleFromIdx + 1, splitTitleFromList.length)
                .forEachRemaining(versionSection::add);
        Arrays.spliterator(splitCurrentTitle, seCurrentTitleIdx + 1, splitCurrentTitle.length)
                .forEachRemaining(currentVersionSection::add);

        int differenceCounter = 0;
        for (String word : versionSection) {
            if (!currentVersionSection.contains(word)) {
                differenceCounter++;
            }
            if (differenceCounter == MAX_DIFFERENCE_COUNTER) {
                result = false;
                break;
            }
        }

        return result;
    }

}
