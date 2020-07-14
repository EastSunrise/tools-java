package wsg.tools.boot.dao.jpa.converter;

import javax.persistence.AttributeConverter;

/**
 * Base converter to pre-handle null value when converting.
 *
 * @author Kingen
 * @since 2020/7/12
 */
public abstract class BaseNonNullConverter<JavaType, DbType> implements AttributeConverter<JavaType, DbType> {
    @Override
    public DbType convertToDatabaseColumn(JavaType attribute) {
        if (attribute == null) {
            return null;
        }
        return serialize(attribute);
    }

    @Override
    public JavaType convertToEntityAttribute(DbType dbData) {
        if (dbData == null) {
            return null;
        }
        return deserialize(dbData);
    }

    /**
     * Deserialize data getInstance db to a Java object
     *
     * @param dbData data getInstance db, not null
     * @return target object
     */
    protected abstract JavaType deserialize(DbType dbData);

    /**
     * Serialize a non-null object to database column
     *
     * @param attribute source object, not null
     * @return value to store in db
     */
    protected abstract DbType serialize(JavaType attribute);

}
