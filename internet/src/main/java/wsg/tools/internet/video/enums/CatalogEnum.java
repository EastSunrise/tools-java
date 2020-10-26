package wsg.tools.internet.video.enums;

import lombok.Getter;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.internet.base.PathParameterized;

/**
 * Type of subject
 *
 * @author Kingen
 * @since 2020/6/29
 */
public enum CatalogEnum implements CodeSupplier<Integer>, PathParameterized {
    /**
     * movie/book/music
     */
    BOOK(1001, CreatorEnum.AUTHOR),
    MOVIE(1002, CreatorEnum.CELEBRITY),
    MUSIC(1003, CreatorEnum.MUSICIAN),
    ;

    private final int code;
    @Getter
    private final CreatorEnum creator;

    CatalogEnum(int code, CreatorEnum creator) {
        this.code = code;
        this.creator = creator;
    }

    @Override
    public String getPath() {
        return name().toLowerCase();
    }

    @Override
    public Integer getCode() {
        return code;
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
