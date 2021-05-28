package wsg.tools.internet.movie.online;

import java.io.Serializable;
import java.util.Locale;
import javax.annotation.Nonnull;

/**
 * The request of pagination information.
 *
 * @author Kingen
 * @since 2021/3/22
 */
public class RenrenReq implements Serializable {

    private static final long serialVersionUID = -5684090741879047056L;

    private final MovieType type;
    private final Area area;
    private final Genre genre;
    private final SeriesStatus status;
    private final Sort sort;

    public RenrenReq() {
        this(null, null, null, null, null);
    }

    public RenrenReq(MovieType type, Area area, Genre genre, SeriesStatus status,
        Sort sort) {
        this.type = type;
        this.area = area;
        this.genre = genre;
        this.status = status;
        this.sort = sort == null ? Sort.LATEST : sort;
    }

    public MovieType getType() {
        return type;
    }

    public Area getArea() {
        return area;
    }

    public Genre getGenre() {
        return genre;
    }

    public SeriesStatus getStatus() {
        return status;
    }

    public Sort getSort() {
        return sort;
    }

    public enum MovieType {
        /**
         * tv series
         */
        SERIES("series"),
        /**
         * variety shows
         */
        VARIETY("veriety"),
        /**
         * documentaries
         */
        DOCUMENTARY("documentary"),
        /**
         * cartoons
         */
        CARTOON("cartoon");

        private final String text;

        MovieType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum Area {
        /**
         * USA
         */
        US("usk"),
        /**
         * Japan
         */
        JP("jp"),
        /**
         * Korean
         */
        KR("kr"),
        /**
         * Thailand
         */
        TH("th"),
        /**
         * China
         */
        CN("chn"),
        /**
         * UK
         */
        UK("uk"),
        /**
         * other areas
         */
        OTHER("other");

        private final String text;

        Area(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum Genre {
        /**
         * genres
         */
        PLOT, COMEDY, ACTION, LOVE, CRIME, ADVENTURE, WAR, SUSPENSE, TERROR, SCIENCE, FAMILY;

        @Nonnull
        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Sort {
        /**
         * by time
         */
        LATEST,
        /**
         * by clout
         */
        HOT,
        /**
         * by positive comments
         */
        PRAISE;

        @Nonnull
        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
