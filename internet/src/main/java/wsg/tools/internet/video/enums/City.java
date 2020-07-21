package wsg.tools.internet.video.enums;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Enum for cities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
public class City extends AbstractLocale<City> {

    public static final City BEIJING = new City(1000, "PEK", "Beijing", "北京");
    public static final City SHANGHAI = new City(2900, "SHA", "Shanghai", "上海");
    private static final Set<City> CITIES;

    static {
        CITIES = new HashSet<>(4096);
        CITIES.add(BEIJING);
        CITIES.add(SHANGHAI);
    }

    @Getter
    private final int no;

    protected City(int no, String code, String text, String title) {
        super(code, text, title, null);
        this.no = no;
    }

    protected City(int no, String code, String text, String title, String[] aka) {
        super(code, text, title, aka);
        this.no = no;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof City) {
            City other = (City) obj;
            return code.equals(other.code);
        }
        return false;
    }
}
