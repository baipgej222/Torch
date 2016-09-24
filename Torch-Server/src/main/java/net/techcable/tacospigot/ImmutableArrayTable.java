package net.techcable.tacospigot;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;

import net.techcable.tacospigot.function.ObjIntFunction;

public class ImmutableArrayTable<R, C, V> implements Table<R, C, V> {
    private final Indexer<R> rowIndexer;
    private final IntFunction<R> rowById;
    private final ToIntBiFunction<R, C> columnGetId;
    private final ObjIntFunction<R, C> columnById;
    private final ImmutableArrayMap<R, Map<C, V>> rowMap;
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int size;

    public ImmutableArrayTable(Indexer<R> rowIndexer, IntFunction<R> rowById, ToIntBiFunction<R, C> columnGetId, ObjIntFunction<R, C> columnById, Table<R, C, V> table) {
        this.rowIndexer = Preconditions.checkNotNull(rowIndexer, "Null indexer for row");
        this.rowById = Preconditions.checkNotNull(rowById, "Null byId function for row");
        this.columnGetId = Preconditions.checkNotNull(columnGetId, "Null getId function for column");
        this.columnById = Preconditions.checkNotNull(columnById, "Null byId function for column");
        Preconditions.checkNotNull(table, "Null table");
        ImmutableMap.Builder<R, Map<C, V>> rowMapBuilder = ImmutableMap.builder();
        for (Map.Entry<R, Map<C, V>> rowEntry : table.rowMap().entrySet()) {
            R row = rowEntry.getKey();
            Preconditions.checkNotNull(row, "Null row");
            ImmutableMap.Builder<C, V> rowMapEntryBuilder = ImmutableMap.builder();
            for (Map.Entry<C, V> rowEntryEntry : rowEntry.getValue().entrySet()) {
                rowMapEntryBuilder.put(rowEntryEntry);
            }
            rowMapBuilder.put(row, new ImmutableArrayMap<>((c) -> columnGetId.applyAsInt(row, c), (id) -> columnById.apply(row, id), rowMapEntryBuilder.build()));
        }
        this.rowMap = new ImmutableArrayMap<>(rowIndexer, rowById, rowMapBuilder.build());
        Map<C, Map<R, V>> columnMapBuilder = new HashMap<>();
        int size = 0;
        for (Cell<R, C, V> cell : cellSet()) {
            R row = cell.getRowKey();
            C column = cell.getColumnKey();
            V value = cell.getValue();
            Preconditions.checkNotNull(column, "Null column");
            Preconditions.checkNotNull(value, "Null value");
            Map<R, V> columnEntry = columnMapBuilder.computeIfAbsent(column, (c) -> new HashMap<>());
            columnEntry.put(cell.getRowKey(), cell.getValue());
            size++;
        }
        this.size = size;
        this.columnMap = ImmutableMap.copyOf(columnMapBuilder);
    }

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        Map<C, V> rowEntry = rowMap.get(rowKey);
        return rowEntry != null && rowEntry.containsKey(columnKey);
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        return rowMap.containsKey(rowKey);
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        return columnMap.containsKey(columnKey);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        Preconditions.checkNotNull(value, "Null value");
        for (V v : values()) {
            if (v.equals(value)) return true;
        }
        return false;
    }

    @Override
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        Map<C, V> rowEntry = rowMap.get(rowKey);
        return rowEntry != null ? rowEntry.get(columnKey) : null;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Map<C, V> row(R rowKey) {
        return rowMap.get(rowKey);
    }

    @Override
    public Map<R, V> column(C columnKey) {
        return columnMap.get(columnKey);
    }

    @Override
    public Set<Cell<R, C, V>> cellSet() {
        return new AbstractSet<Cell<R, C, V>>() {
            @Override
            public Iterator<Cell<R, C, V>> iterator() {
                Iterator<Map.Entry<R, Map<C, V>>> rowMapIterator = rowMap().entrySet().iterator();
                if (!rowMapIterator.hasNext()) return Collections.emptyIterator();
                return new Iterator<Cell<R, C, V>>() {
                    private R row;
                    private Iterator<Map.Entry<C, V>> rowMapEntryIterator;

                    {
                        Map.Entry<R, Map<C, V>> firstEntry = rowMapIterator.next();
                        row = firstEntry.getKey();
                        rowMapEntryIterator = firstEntry.getValue().entrySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        if (rowMapEntryIterator.hasNext()) {
                            return true;
                        } else {
                            while (rowMapIterator.hasNext()) {
                                Map.Entry<R, Map<C, V>> rowMapEntry = rowMapIterator.next();
                                row = rowMapEntry.getKey();
                                rowMapEntryIterator = rowMapEntry.getValue().entrySet().iterator();
                                if (rowMapEntryIterator.hasNext()) return true;
                            }
                            return false;
                        }
                    }

                    @Override
                    public Cell<R, C, V> next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        Map.Entry<C, V> rowMapEntryEntry = rowMapEntryIterator.next();
                        return new SimpleCell<>(row, rowMapEntryEntry.getKey(), rowMapEntryEntry.getValue());
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public Set<R> rowKeySet() {
        return rowMap.keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return columnMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>() {
            @Override
            public Iterator<V> iterator() {

                Iterator<Cell<R, C, V>> cellIterator = cellSet().iterator();
                return new Iterator<V>() {
                    @Override
                    public boolean hasNext() {
                        return cellIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        return cellIterator.next().getValue();
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        return rowMap;
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        return columnMap;
    }

    private static class SimpleCell<R, C, V> implements Cell<R, C, V> {
        private final R row;
        private final C column;
        private final V value;

        private SimpleCell(R row, C column, V value) {
            this.row = row;
            this.column = column;
            this.value = value;
        }

        @Override
        public R getRowKey() {
            return row;
        }

        @Override
        public C getColumnKey() {
            return column;
        }

        @Override
        public V getValue() {
            return value;
        }
    }

    // Mutators
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }


    @Override
    public V put(R rowKey, C columnKey, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
        throw new UnsupportedOperationException();
    }
}
