package pg.program;

import java.util.Comparator;

/**
 * Created by Gawa 2017-10-16
 */
public class ShowDetail {
    private int id;
    private String title;
    private String baseWords;
    private int matchPrecision;

    public static Comparator<ShowDetail> COMPARATOR = (o1, o2) -> {
        if (o1.getId() > o2.getId()) {
            return 1;
        } else if (o1.getId() < o2.getId()) {
            return -1;
        }
        return 0;
    };

    public ShowDetail(int id, String title) {
        this.id = id;
        this.title = title;
        matchPrecision = 0;
    }

    public ShowDetail(int id, String baseWords, int matchPrecision) {
        this(id, "");
        this.baseWords = baseWords;
        this.matchPrecision = matchPrecision;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseWords() {
        return baseWords;
    }

    public void setBaseWords(String baseWords) {
        this.baseWords = baseWords;
    }

    public int getMatchPrecision() {
        return matchPrecision;
    }

    public void setMatchPrecision(int matchPrecision) {
        this.matchPrecision = matchPrecision;
    }

    public int getBaseWordsCount() {
        return getBaseWords().split(",").length;
    }

    @Override
    public String toString() {
        return "ShowDetail{" +
                "id=" + id +
                ", title=" + title +
                ", baseWords='" + baseWords + '\'' +
                ", matchPrecision=" + matchPrecision +
                '}';
    }
}
