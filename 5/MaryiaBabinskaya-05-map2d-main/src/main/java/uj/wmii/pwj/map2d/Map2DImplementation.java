package uj.wmii.pwj.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Map2DImplementation<R, C, V> implements Map2D<R, C, V> {

    Map<R, Map<C, V>> Map2D;

    Map2DImplementation() {
        Map2D = new HashMap<>();
    }

    public V put(R rowKey, C columnKey, V value) {
        if (rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> rowMap = Map2D.get(rowKey);
        if (rowMap == null) {
            rowMap = new HashMap<>();
            Map2D.put(rowKey, rowMap);
        }
        return rowMap.put(columnKey, value);
    }

    public V get(R rowKey, C columnKey) {
        Map<C, V> rowMap = Map2D.get(rowKey);
        if (rowMap != null) {
            return rowMap.get(columnKey);
        }
        return null;
    }

    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        Map<C, V> rowMap = Map2D.get(rowKey);
        if (rowMap != null) {
            return rowMap.get(columnKey);
        }
        return defaultValue;
    }

    public V remove(R rowKey, C columnKey) {
        if (Map2D.containsKey(rowKey)) {
            V value = Map2D.get(rowKey).get(columnKey);
            this.put(rowKey,columnKey,null);
            return value;
        }
        else return null;
    }

    public boolean isEmpty() {
        return Map2D.isEmpty();
    }

    public boolean nonEmpty() {
        return !Map2D.isEmpty();
    }

    public int size() {
        int size = 0;
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            size += valueMap.size();
        }
        return size;
    }

    public void clear() {
        Map2D.clear();
    }

    public Map<C, V> rowView(R rowKey) {
        Map<C, V> rowMap = Map2D.getOrDefault(rowKey, Collections.emptyMap());
        return Collections.unmodifiableMap(rowMap);
    }

    public Map<R, V> columnView(C columnKey) {
        Map<R, V> columnMap = new HashMap<>();
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            if (valueMap.containsKey(columnKey)) {
                columnMap.put(key, valueMap.get(columnKey));
            }
        }
        return Collections.unmodifiableMap(columnMap);
    }

    public boolean hasValue(V value) {
        for (Map<C, V> map : Map2D.values()) {
            if (map.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKey(R rowKey, C columnKey) {
        Map<C, V> rowMap = Map2D.get(rowKey);
        return rowMap != null && rowMap.containsKey(columnKey);
    }

    public boolean hasRow(R rowKey) {
        Map<C, V> rowMap = Map2D.get(rowKey);
        return rowMap != null && !rowMap.isEmpty();
    }

    public boolean hasColumn(C columnKey) {
        for (Map<C, V> rowMap : Map2D.values()) {
            if (rowMap.containsKey(columnKey)) {
                return true;
            }
        }
        return false;
    }

    public Map<R, Map<C, V>> rowMapView() {
        if (Map2D.isEmpty()) return Collections.emptyMap();
        else {
            Map<R, Map<C, V>> newMap2D = new HashMap<>();
            for (R key : Map2D.keySet()) {
                Map<C, V> valueMap = Map.copyOf(Map2D.get(key));
                newMap2D.put(key, valueMap);
            }
            return Collections.unmodifiableMap(newMap2D);
        }
    }

    public Map<C, Map<R, V>> columnMapView() {
        Map2DImplementation<C, R, V> newMap2D = new Map2DImplementation<>();
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            for (C key2 : valueMap.keySet()) {
                V value = valueMap.get(key2);
                newMap2D.put(key2, key, value);
            }
        }
        newMap2D.Map2D = Collections.unmodifiableMap(newMap2D.Map2D);
        return newMap2D.Map2D;
    }

    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        Map<C, V> valueMap = Map2D.get(rowKey);
        if(valueMap == null) return null;
        for (C key2 : valueMap.keySet()) {
            V val = valueMap.get(key2);
            target.put(key2, val);
        }
        return this;
    }

    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            V value = valueMap.get(columnKey);
            target.put(key, value);
        }
        return this;
    }

    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        for (R key : source.rowMapView().keySet()) {
            Map<? extends C, ? extends V> valueMap = source.rowMapView().get(key);
            for (C key2 : valueMap.keySet()) {
                V value = valueMap.get(key2);
                this.put(key, key2, value);
            }
        }
        return this;
    }

    public Map2D<R, C, V>  putAllToRow(Map<? extends C, ? extends V> source, R rowKey){
        for (C key : source.keySet()) {
            V value = source.get(key);
            this.put(rowKey, key, value);
        }
        return this;
    }

    public Map2D<R, C, V>  putAllToColumn(Map<? extends R, ? extends V> source, C columnKey){
        for (R key : source.keySet()) {
            V value = source.get(key);
            this.put(key, columnKey, value);
        }
        return this;
    }

    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(
            Function<? super R, ? extends R2> rowFunction,
            Function<? super C, ? extends C2> columnFunction,
            Function<? super V, ? extends V2> valueFunction) {
        Map2DImplementation<R2, C2, V2> NewMap = new Map2DImplementation<>();
        for (R key : Map2D.keySet()) {
            Map<C, V> valueMap = Map2D.get(key);
            for (C key2 : valueMap.keySet()) {
                V value = valueMap.get(key2);
                NewMap.put(rowFunction.apply(key), columnFunction.apply(key2), valueFunction.apply(value));
            }
        }
        NewMap.Map2D = Collections.unmodifiableMap(NewMap.Map2D);
        return NewMap;
    }
}