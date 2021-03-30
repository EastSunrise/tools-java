package wsg.tools.boot.pojo.entity.base;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Setter;

/**
 * Base entity with an generated id.
 *
 * @author Kingen
 * @since 2020/9/25
 */
@Setter
@MappedSuperclass
public class IdentityEntity extends BaseEntity {

    private static final long serialVersionUID = -7472130300615133460L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IdentityEntity entity = (IdentityEntity) obj;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
