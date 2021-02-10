package wsg.tools.boot.dao.jpa.converter;

import javax.persistence.AttributeConverter;

/**
 * Base converter to pre-handle null value when converting.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public abstract class BaseNonNullConverter<JavaType, JdbcType> implements AttributeConverter<JavaType, JdbcType> {
    @Override
    public JdbcType convertToDatabaseColumn(JavaType attribute) {
        if (attribute == null) {
            return null;
        }
        return serialize(attribute);
    }

    @Override
    public JavaType convertToEntityAttribute(JdbcType dbData) {
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
    protected abstract JavaType deserialize(JdbcType dbData);

    /**
     * Serialize a non-null object to database column
     *
     * @param attribute source object, not null
     * @return value to store in db
     */
    protected abstract JdbcType serialize(JavaType attribute);

}
