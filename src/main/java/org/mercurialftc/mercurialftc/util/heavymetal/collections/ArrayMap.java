package org.mercurialftc.mercurialftc.util.heavymetal.collections;

import java.util.ArrayList;

public class ArrayMap<K, V> extends ArrayList<ArrayMap.Entry<K, V>> {
	public boolean add(K key, V value) {
		return add(new Entry<>(key, value));
	}

	public K getKey(int index) {
		return get(index).getKey();
	}

	public V getValue(int index) {
		return get(index).getValue();
	}

	public static class Entry<K, V> {
		private final K key;
		private final V value;
		public Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}
}
