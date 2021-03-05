package wsg.tools.internet.info.adult.mr;

import java.util.Objects;
import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;

/**
 * An album with simple information.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class SimpleAlbum implements IntIdentifier {

    private final int id;
    private final AlbumType type;
    private final String title;

    SimpleAlbum(int id, AlbumType type, String title) {
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
        SimpleAlbum that = (SimpleAlbum) o;
        return id == that.id && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
