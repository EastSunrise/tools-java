package wsg.tools.boot.pojo.entity.base;

import lombok.Setter;
import wsg.tools.common.lang.Identifier;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * Base entity with an generated id.
 *
 * @author Kingen
 * @since 2020/9/25
 */
@Setter
@MappedSuperclass
public class IdentityEntity extends BaseEntity implements Identifier<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public Long getId() {
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
        IdentityEntity that = (IdentityEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
