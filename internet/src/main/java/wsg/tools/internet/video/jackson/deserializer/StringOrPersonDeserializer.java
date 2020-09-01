package wsg.tools.internet.video.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import wsg.tools.internet.video.entity.gen.object.GenPerson;

import java.io.IOException;

/**
 * Deserialize a string or an object to {@link GenPerson}.
 *
 * @author Kingen
 * @since 2020/9/1
 */
public class StringOrPersonDeserializer extends StdDeserializer<GenPerson> {

    protected StringOrPersonDeserializer() {
        super(GenPerson.class);
    }

    @Override
    public GenPerson deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            GenPerson person = new GenPerson();
            person.setName(p.getText());
            return person;
        }
        return p.readValueAs(GenPerson.class);
    }
}
