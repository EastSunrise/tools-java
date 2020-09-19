package wsg.tools.internet.video.entity.imdb.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;

import java.util.List;

/**
 * Info of titles of IMDb that can be contributed to the page.
 *
 * @author Kingen
 * @since 2020/9/5
 */
@Getter
@Setter
public class ImdbInfo {

    /**
     * Basic Data
     */
    private List<CertificateInfo> ratings;
    private ColorInfo color;
    private List<RegionEnum> regions;
    private List<LanguageInfo> languages;
    private List<ReleaseInfo> releases;
    private List<RuntimeInfo> durations;
    private List<SoundMixInfo> soundMixes;

    /**
     * Technical Info
     */
    private Object filmLength;
    private Object filmNegativeFormat;
    private Object printedFilmFormat;
    private AspectRatio aspectRatio;
    private Object cinematographicProcess;
    private List<Camera> cameras;
    private List<Laboratory> laboratories;

    /**
     * Plot & Quotes
     */
    private List<PlotOutlineInfo> plotOutlines;
    private List<SummaryInfo> summaries;
    private String synopsis;
    @JoinedValue(separator = SignEnum.COMMA)
    private List<String> keywords;
    private List<QuoteInfo> quotes;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
}
