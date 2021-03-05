package wsg.tools.boot.dao.jpa.converter;

import javax.persistence.AttributeConverter;

/**
 * Base converter to pre-handle null value when converting.
 *
 * @author Kingen
 * @since 2020/7/12
 */
abstract class BaseNonNullConverter<X, Y> implements AttributeConverter<X, Y> {

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        if (attribute == null) {
            return null;
        }
        return serialize(attribute);
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        if (dbData == null) {
            return null;
        }
        return deserialize(dbData);
    }

    /**
     * Deserialize data of db to a Java object
     *
     * @param dbData data of db, not null
     * @return target object
     */
    protected abstract X deserialize(Y dbData);

    /**
     * Serialize a non-null object to database column
     *
     * @param attribute source object, not null
     * @return value to store in db
     */
    protected abstract Y serialize(X attribute);

}
