package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

/**
 * A part of a tournament round, like a week's matches in the tournament round.
 *
 * @author Kingen
 * @since 2021/3/13
 */
@Getter
public class RoundItem {

    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("is_now_week")
    @JsonDeserialize(using = IntStringBooleanDeserializer.class)
    private boolean nowWeek;
    @JsonProperty("name_en")
    private String nameEn;

    RoundItem() {
    }
}