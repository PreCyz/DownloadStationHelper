package pg.converter;

import pg.program.ShowDetail;
import pg.props.ShowsPropertiesHelper;

import java.util.Properties;
import java.util.Set;

public class PropertiesToShowDetailsConverter extends AbstractConverter<Properties, Set<ShowDetail>> {

    @Override
    public Set<ShowDetail> convert(Properties source) {
        Set<ShowDetail> showDetails = ShowsPropertiesHelper.getInstance().getShowDetails();
        if (!showDetails.isEmpty()) {
            for (ShowDetail showDetail : showDetails) {
                String baseWords = showDetail.getBaseWords();
                int firstComma = baseWords.indexOf(",", 0);
                String title = baseWords.substring(0, firstComma);

                showDetail.setTitle(title);
                showDetail.setBaseWords(baseWords.substring(firstComma + 1));

                showDetail.setMatchPrecision(showDetail.getMatchPrecision() - 1);
                if (showDetail.getMatchPrecision() > showDetail.getBaseWordsCount()) {
                    showDetail.setMatchPrecision(showDetail.getBaseWordsCount());
                }
            }
        }
        return showDetails;
    }

}
