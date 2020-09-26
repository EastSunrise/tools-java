package wsg.tools.boot.pojo.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base entity with an generated id.
 *
 * @author Kingen
 * @since 2020/9/25
 */
@Getter
@Setter
@MappedSuperclass
public class IdentityEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
