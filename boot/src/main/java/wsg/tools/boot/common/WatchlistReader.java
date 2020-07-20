package wsg.tools.boot.common;

import org.apache.commons.csv.CSVRecord;

/**
 * Read CSV files of watch lists exported from IMDb.
 *
 * @author Kingen
 * @since 2020/7/10
 */
public class WatchlistReader extends BaseCsvReader<String> {

    public WatchlistReader() {
        super(new String[]{
                "Position", "Const", "Created", "Modified", "Description", "Title", "URL", "Title Type", "IMDb Rating",
                "Runtime (mins)", "Year", "Genres", "Num Votes", "Release Date", "Directors", "Your Rating", "Date Rated"
        });
    }

    @Override
    protected String readRecord(CSVRecord record) {
        return record.get("Const");
    }
}
