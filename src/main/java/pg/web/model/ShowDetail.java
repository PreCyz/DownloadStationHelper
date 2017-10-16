package pg.web.model;

/**Created by Gawa 2017-10-16*/
public class ShowDetail {
    private int id;
    private String baseWords;
    private int matchPrecision;

    public ShowDetail(int id, String baseWords, int matchPrecision) {
        this.id = id;
        this.baseWords = baseWords;
        this.matchPrecision = matchPrecision;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
