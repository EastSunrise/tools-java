package wsg.tools.common.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.excel.reader.BaseCellToSetter;
import wsg.tools.common.excel.writer.BaseCellFromGetter;
import wsg.tools.common.function.ValueGetter;
import wsg.tools.common.function.ValueSetter;

import java.util.LinkedHashMap;

/**
 * Utility to create an excel template quickly.
 *
 * @author Kingen
 * @since 2020/7/23
 */
public class ExcelTemplate<T> {
    private final LinkedHashMap<String, BaseCellFromGetter<T, ?>> writers = new LinkedHashMap<>(Constants.DEFAULT_MAP_CAPACITY);
    private final LinkedHashMap<String, BaseCellToSetter<T, ?>> readers = new LinkedHashMap<>(Constants.DEFAULT_MAP_CAPACITY);

    public static <T> ExcelTemplate<T> builder() {
        return new ExcelTemplate<>();
    }

    public <V> ExcelTemplate<T> putWriter(String header, BaseCellFromGetter<T, V> writer) {
        writers.put(header, writer);
        return this;
    }

    public <V> ExcelTemplate<T> putReader(String header, BaseCellToSetter<T, V> reader) {
        readers.put(header, reader);
        return this;
    }

    public <V> ExcelTemplate<T> put(String header, BaseCellFromGetter<T, V> writer, BaseCellToSetter<T, V> reader) {
        return putWriter(header, writer).putReader(header, reader);
    }

    public <V> ExcelTemplate<T> putGetter(String header, ValueGetter<T, V> getter) {
        return putWriter(header, new BaseCellFromGetter<T, V>() {
            @Override
            public V getValue(T t) {
                return getter.getValue(t);
            }
        });
    }

    public <V> ExcelTemplate<T> putSetter(String header, ValueSetter<T, V> setter, Class<V> clazz) {
        return putReader(header, new BaseCellToSetter<>(clazz) {
            @Override
            public void setValue(T t, V v) {
                setter.setValue(t, v);
            }
        });
    }

    public <V> ExcelTemplate<T> putSetter(String header, ValueSetter<T, V> setter, TypeReference<V> typeReference) {
        return putReader(header, new BaseCellToSetter<>(typeReference) {
            @Override
            public void setValue(T t, V v) {
                setter.setValue(t, v);
            }
        });
    }

    public <V> ExcelTemplate<T> put(String header, ValueGetter<T, V> getter, ValueSetter<T, V> setter, Class<V> clazz) {
        return putGetter(header, getter).putSetter(header, setter, clazz);
    }

    public <V> ExcelTemplate<T> put(String header, ValueGetter<T, V> getter, ValueSetter<T, V> setter, TypeReference<V> typeReference) {
        return putGetter(header, getter).putSetter(header, setter, typeReference);
    }

    public LinkedHashMap<String, BaseCellFromGetter<T, ?>> getWriters() {
        return writers;
    }

    public LinkedHashMap<String, BaseCellToSetter<T, ?>> getReaders() {
        return readers;
    }
}