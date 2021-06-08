package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import wsg.tools.common.Constants;
import wsg.tools.common.jackson.JsonDurationFormat;
import wsg.tools.common.jackson.JsonJoinedValue;

/**
 * Common tags contained in a stream.
 *
 * @author Kingen
 * @since 2021/6/4
 */
public class BaseTags {

    @JsonProperty("title")
    private String title;

    @JsonProperty("language")
    @JsonAlias("LANGUAGE")
    private String language;

    @JsonProperty("BPS")
    private Long bps;

    @JsonProperty("BPS-eng")
    private Long bpsEng;

    @JsonProperty("DURATION")
    @JsonDurationFormat(format = JsonDurationFormat.Format.DURATION)
    private Duration duration;

    @JsonProperty("DURATION-eng")
    @JsonDurationFormat(format = JsonDurationFormat.Format.DURATION)
    private Duration durationEng;

    @JsonProperty("NUMBER_OF_FRAMES")
    private Long numberOfFrames;

    @JsonProperty("NUMBER_OF_FRAMES-eng")
    private Long numberOfFramesEng;

    @JsonProperty("NUMBER_OF_BYTES")
    private Long numberOfBytes;

    @JsonProperty("NUMBER_OF_BYTES-eng")
    private Long numberOfBytesEng;

    @JsonProperty("_STATISTICS_WRITING_APP")
    private String statisticsWritingApp;

    @JsonProperty("_STATISTICS_WRITING_APP-eng")
    private String statisticsWritingAppEng;

    @JsonProperty("_STATISTICS_WRITING_DATE_UTC")
    @JsonFormat(pattern = Constants.P_YYYY_MM_DD_HH_MM_SS, timezone = "utc")
    private LocalDateTime statisticsWritingDateUtc;

    @JsonProperty("_STATISTICS_WRITING_DATE_UTC-eng")
    @JsonFormat(pattern = Constants.P_YYYY_MM_DD_HH_MM_SS, timezone = "utc")
    private LocalDateTime statisticsWritingDateUtcEng;

    @JsonProperty("_STATISTICS_TAGS")
    @JsonJoinedValue(separator = " ")
    private List<String> statisticsTags;

    @JsonProperty("_STATISTICS_TAGS-eng")
    @JsonJoinedValue(separator = " ")
    private List<String> statisticsTagsEng;

    BaseTags() {
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public Long getBps() {
        return bps;
    }

    public Long getBpsEng() {
        return bpsEng;
    }

    public Duration getDuration() {
        return duration;
    }

    public Duration getDurationEng() {
        return durationEng;
    }

    public Long getNumberOfFrames() {
        return numberOfFrames;
    }

    public Long getNumberOfFramesEng() {
        return numberOfFramesEng;
    }

    public Long getNumberOfBytes() {
        return numberOfBytes;
    }

    public Long getNumberOfBytesEng() {
        return numberOfBytesEng;
    }

    public String getStatisticsWritingApp() {
        return statisticsWritingApp;
    }

    public String getStatisticsWritingAppEng() {
        return statisticsWritingAppEng;
    }

    public LocalDateTime getStatisticsWritingDateUtc() {
        return statisticsWritingDateUtc;
    }

    public LocalDateTime getStatisticsWritingDateUtcEng() {
        return statisticsWritingDateUtcEng;
    }

    public List<String> getStatisticsTags() {
        return statisticsTags;
    }

    public List<String> getStatisticsTagsEng() {
        return statisticsTagsEng;
    }
}
