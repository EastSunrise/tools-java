package wsg.tools.internet.info.adult.midnight;

import lombok.Getter;
import wsg.tools.common.util.function.TitleSupplier;

import java.util.Map;

/**
 * An actress in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Getter
public class MidnightActress implements TitleSupplier {

    private final String title;
    /**
     * Map of code-cover of adult videos, the cover may be null.
     */
    private Map<String, String> works;

    MidnightActress(String title) {
        this.title = title;
    }

    void setWorks(Map<String, String> works) {
        this.works = works;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
