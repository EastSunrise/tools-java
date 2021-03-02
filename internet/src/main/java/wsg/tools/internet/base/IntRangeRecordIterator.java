package wsg.tools.internet.base;

import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.Repository;

import java.util.NoSuchElementException;

/**
 * Constructs an iterator over an integer range.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class IntRangeRecordIterator<T> implements RecordIterator<T> {

    private final Repository<Integer, T> repository;
    private final int endInclusive;
    private int cursor;

    public IntRangeRecordIterator(Repository<Integer, T> repository, int startInclusive, int endInclusive) {
        this.repository = repository;
        this.endInclusive = endInclusive;
        this.cursor = startInclusive;
    }

    @Override
    public boolean hasNext() {
        return cursor <= endInclusive;
    }

    @Override
    public T next() throws HttpResponseException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T t = repository.findById(cursor);
        cursor++;
        return t;
    }
}
