package org.jboss.as.quickstarts.datagrid.securecache.rest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class CacheEntry<K extends Comparable<K>,V extends Comparable<V>> implements Comparable<CacheEntry<K,V>> {
	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	private K key; 
	private V value; 
	
	public CacheEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public int compareTo(CacheEntry<K, V> o) {
		return this.key.compareTo(o.key);
	}
	
}