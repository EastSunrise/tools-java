package wsg.tools.internet.resource.online;

import java.util.Locale;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.page.BasicPageReq;

/**
 * The request of pagination information.
 *
 * @author Kingen
 * @since 2021/3/22
 */
public class RenrenPageReq extends BasicPageReq {

    private static final long serialVersionUID = -5684090741879047056L;
    private static final int DEFAULT_SIZE = 10;

    private final MovieType type;
    private final Area area;
    private final Genre genre;
    private final SeriesStatus status;
    private final Sort sort;

    public RenrenPageReq(int current) {
        this(current, null, null, null, null, null);
    }

    public RenrenPageReq(int current, MovieType type, Area area, Genre genre, SeriesStatus status,
        Sort sort) {
        super(current, DEFAULT_SIZE);
        this.type = type;
        this.area = area;
        this.genre = genre;
        this.status = status;
        this.sort = sort == null ? Sort.LATEST : sort;
    }

    @Nonnull
    @Contract(" -> new")
    public static RenrenPageReq first() {
        return new RenrenPageReq(0);
    }

    @Override
    public RenrenPageReq next() {
        return new RenrenPageReq(super.next().getCurrent(), type, area, genre, status, sort);
    }

    @Override
    public RenrenPageReq previous() {
        return new RenrenPageReq(super.previous().getCurrent(), type, area, genre, status, sort);
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
