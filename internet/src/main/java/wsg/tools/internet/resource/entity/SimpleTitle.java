package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Year;

/**
 * Item obtained by searching, including necessary properties.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Getter
@Setter
public class SimpleTitle {

    private String title;
    private String path;
    private Year year;
    private VideoTypeEnum type;
    private Year endYear;

    @Override
    public String toString() {
        return "TitleItem{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", year=" + year +
                ", type=" + type +
                ", endYear=" + endYear +
                '}';
    }
}
