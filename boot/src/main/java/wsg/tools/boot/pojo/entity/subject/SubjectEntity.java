package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Duration;
import java.time.Year;
import java.util.List;

/**
 * Base entity of a subject.
 *
 * @author Kingen
 * @since 2020/9/29
 */
@Getter
@Setter
@MappedSuperclass
public class SubjectEntity extends IdentityEntity {

    @Column(nullable = false, length = 63)
    private String title;

    @Column(nullable = false)
    private Year year;

    @Column(nullable = false, length = 63)
    private List<LanguageEnum> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;
}
