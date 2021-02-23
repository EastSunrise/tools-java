package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity of adult video.
 *
 * @author Kingen
 * @since 2021/2/23
 */
@Getter
@Setter
@Entity
@Table(name = "adult_video")
public class AdultVideoEntity extends IdentityEntity {

    @Column(nullable = false)
    private Integer sid;
    @Column(length = 16)
    private String code;
    @Column(length = 83)
    private String image;
}
