package wsg.tools.internet.video.enums;

import lombok.Getter;
import wsg.tools.internet.base.PathParameterized;

/**
 * Type of subject
 *
 * @author Kingen
 * @since 2020/6/29
 */
public enum CatalogEnum implements PathParameterized {
    /**
     * movie/book/music
     */
    MOVIE(CreatorEnum.CELEBRITY),
    BOOK(CreatorEnum.AUTHOR),
    MUSIC(CreatorEnum.MUSICIAN);

    @Getter
    private final CreatorEnum creator;

    CatalogEnum(CreatorEnum creator) {
        this.creator = creator;
    }

    @Override
    public String getPath() {
        return name().toLowerCase();
    }

    public enum CreatorEnum implements PathParameterized {
        /**
         * Celebrities/Authors/Musicians
         */
        CELEBRITY("celebrities"),
        AUTHOR("authors"),
        MUSICIAN("musicians");

        private final String plurality;

        CreatorEnum(String plurality) {
            this.plurality = plurality;
        }

        @Override
        public String getPath() {
            return plurality;
        }
    }
}
