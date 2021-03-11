package wsg.tools.internet.info.adult.wiki;

import java.util.Objects;
import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;

/**
 * An index pointing to a {@link WikiAlbum}.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class WikiAlbumIndex implements IntIdentifier {

    private final int id;
    private final WikiAlbumType type;
    private final String title;

    WikiAlbumIndex(int id, WikiAlbumType type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WikiAlbumIndex index = (WikiAlbumIndex) o;
        return id == index.id && type == index.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
