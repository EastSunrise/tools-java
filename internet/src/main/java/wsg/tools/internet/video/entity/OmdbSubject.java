package wsg.tools.internet.video.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Movie subject on OMDb.
 *
 * @author Kingen
 * @since 2020/6/18
 */
@Setter
@Getter
public class OmdbSubject {

    public static final String NULL_NA = "N/A";
    public static final String MONEY_DOLLAR = "$";
    private static final String DATE_FORMAT = "dd MMM yyyy";
}
