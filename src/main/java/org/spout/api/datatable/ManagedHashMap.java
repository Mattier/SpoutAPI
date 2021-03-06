/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.datatable;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.map.DefaultedKey;

/**
 * Manages a string keyed, serializable object hashmap that can be serialized easily
 * to an array of bytes and deserialized from an array of bytes, intended for persistence
 * and network transfers.
 */
public class ManagedHashMap implements SerializableMap{
	final GenericDatatableMap map;
	public ManagedHashMap() {
		this.map = new GenericDatatableMap();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return containsKey((String)key);
		}
		return false;
	}

	public boolean containsKey(String key) {
		return map.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (AbstractData o : map.values()) {
			if (o.get() != null && o.get().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Serializable get(Object key) {
		return get(key, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T get(Object key, T defaultValue) {
		if (key instanceof DefaultedKey) {
			return get((DefaultedKey<T>)key);
		}
		if (!(key instanceof String)) {
			return defaultValue;
		}

		final String keyString = (String) key;
		final T value;
		try {
			value = (T)map.get(keyString).get();
		} catch (ClassCastException e) {
			return defaultValue;
		}

		if (value == null) {
			Serializable old = putIfAbsent(keyString, (Serializable) defaultValue);
			if (old != null) {
				return (T) old;
			} else {
				return defaultValue;
			}
		}

		return value;
	}
	
	@Override
	public <T extends Serializable> T get(DefaultedKey<T> key) {
		T defaultValue = key.getDefaultValue();
		String keyString = key.getKeyString();
		return get(keyString, defaultValue);
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		Serializable s = get((Object)key);
		if (s != null) {
			try {
				return clazz.cast(s);
			} catch (ClassCastException ignore) { }
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T putIfAbsent(DefaultedKey<T> key, T value) {
		String keyString = key.getKeyString();
		try {
			return (T) putIfAbsent(keyString, value);
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public Serializable putIfAbsent(String key, Serializable value) {
		int intKey = map.getIntKey(key);
		AbstractData data = map.setIfAbsent(intKey, getAbstractDataValue(intKey, value));
		if (data == null) {
			return null;
		} else {
			return data.get();
		}
	}
	
	@Override
	public Serializable put(String key, Serializable value) {
		int intKey = map.getIntKey(key);
		AbstractData data = map.getAndSet(intKey, getAbstractDataValue(intKey, value));
		if (data == null) {
			return null;
		} else {
			return data.get();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T put(DefaultedKey<T> key, T value) {
		String keyString = key.getKeyString();
		try {
			return (T)put(keyString, value);
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public Serializable remove(Object key) {
		if (key instanceof String) {
			return remove((String)key);
		} else if (key instanceof DefaultedKey) {
			return remove(((DefaultedKey<?>)key).getKeyString());
		}
		return null;
	}
	
	public Serializable remove(String key) {
		return map.remove(key).get();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Serializable> m) {
		 for (Map.Entry<? extends String, ? extends Serializable> e : m.entrySet()) {
			 put(e.getKey(), e.getValue());
		 }
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Serializable> values() {
		return new Values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		return new EntrySet();
	}
	
	private final class EntrySet extends AbstractSet<Map.Entry<String, Serializable>> {
		int size = map.size();
		
		@Override
		public Iterator<java.util.Map.Entry<String, Serializable>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return size;
		}
		
	}
	
	private final class Values extends AbstractCollection<Serializable> {
		@Override
		public Iterator<Serializable> iterator() {
			return new ValueIterator();
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public void clear() {
			map.clear();
		}
	}
	
	private final class EntryIterator implements Iterator<Map.Entry<String, Serializable>> {
		Serializable next, current;
		int index = 0;
		int expectedAmount = map.size();
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		EntryIterator() {
			for (AbstractData o : map.values()) {
				list.add(o.get());
				keys.add(o.getKey());
			}
			current = null;
			if (expectedAmount == 0) {
				next = null;
			} else {
				next = list.get(index);
			}
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Map.Entry<String, Serializable> next() {
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			index++;
			current = next;
			if (index < expectedAmount) {
				next = list.get(index);
			} else {
				next = null;
			}
			return new Entry(map.getStringKey(keys.get(index-1)), current);
		}

		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			current = null;
			map.remove(keys.get(index));
		}
	}
	
	private final class Entry implements Map.Entry<String, Serializable> {
		final String key;
		Serializable value;
		Entry(String key, Serializable value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Serializable getValue() {
			return value;
		}

		@Override
		public Serializable setValue(Serializable value) {
			this.value = value;
			return ManagedHashMap.this.put(key, value);
		}
		
	}
	
	private final class ValueIterator implements Iterator<Serializable> {
		Serializable next, current;
		int index = 0;
		int expectedAmount = map.size();
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		ValueIterator() {
			for (AbstractData o : map.values()) {
				list.add(o.get());
				keys.add(o.getKey());
			}
			if (expectedAmount > 1) {
				current = list.get(index);
				next = list.get(index + 1);
			} else if (expectedAmount > 0) {
				current = list.get(index);
				next = null;
			} else {
				current = next = null;
			}
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Serializable next() {
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			index++;
			current = next;
			if (index < expectedAmount) {
				next = list.get(index);
			} else {
				next = null;
			}
			return current;
		}

		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			if (map.size() != expectedAmount) {
				throw new ConcurrentModificationException();
			}
			current = null;
			map.remove(keys.get(index));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder("DataMap {");
		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			toString.append("(");
			toString.append(e.getKey());
			toString.append(", ");
			toString.append(e.getValue());
			toString.append("), ");
		}
		toString.delete(toString.length() - 3, toString.length());
		toString.append("}");
		return toString.toString();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			builder.append(e.getKey());
			builder.append(e.getValue());
		}
		return builder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ManagedHashMap)) {
			return false;
		}

		ManagedHashMap other = (ManagedHashMap)obj;
		if (isEmpty() && other.isEmpty()) {
			return true;
		}

		for (Map.Entry<? extends String, ? extends Serializable> e : entrySet()) {
			Serializable value = e.getValue();
			Serializable otherValue = other.get(e.getKey());
			if (value != null) {
				if (!value.equals(otherValue)) {
					return false;
				}
			} else if (otherValue != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public byte[] serialize() {
		return map.compress();
	}

	@Override
	public void deserialize(byte[] data) throws IOException {
		map.decompress(data);
	}

	@Override
	public void deserialize(byte[] data, boolean wipe) throws IOException {
		map.decompress(data, wipe);
	}

	@Override
	public SerializableMap deepCopy() {
		SerializableMap map = new ManagedHashMap();
		try {
			map.deserialize(serialize(), true);
		} catch (IOException e) {
			throw new RuntimeException("Unable to create a deep copy", e);
		}
		return map;
	}

	private final AbstractData getAbstractDataValue(int intKey, Serializable value) {
		if (value instanceof Boolean) {
			return new BooleanData(intKey, (Boolean)value);
		}  else if (value instanceof Double) {
			return new DoubleData(intKey, (Double)value);
		} else if (value instanceof Float) {
			return new FloatData(intKey, (Float)value);
		} else if (value instanceof Long) {
			return new LongData(intKey, (Long)value);
		} else if (value instanceof Integer) {
			return new IntegerData(intKey, (Integer)value);
		}  else if (value instanceof Short) {
			return new IntegerData(intKey, (Short)value);
		}  else if (value instanceof Byte) {
			return new IntegerData(intKey, (Byte)value);
		} else if (value instanceof String) {
			return new StringData(intKey, (String)value);
		} else if (value instanceof Serializable) {
			return new SerializableData(intKey, value);
		} else {
			return map.niltype;
		}
	}
}
