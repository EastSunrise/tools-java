package wsg.tools.internet.video.entity.douban.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.object.AuthorItem;
import wsg.tools.internet.video.entity.douban.object.BookItem;
import wsg.tools.internet.video.entity.douban.object.MovieItem;

/**
 * Item of suggested.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovieItem.class, name = "movie"),
        @JsonSubTypes.Type(value = BookItem.class, name = "b"),
        @JsonSubTypes.Type(value = AuthorItem.class, name = "a")
})
public abstract class BaseSuggestItem {
}