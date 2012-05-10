/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.util.Map.Entry;
import java.util.*;

/**
 *
 * @author Justin Swanson
 */
public class LMergeMap<K, V> {

    Map<K, ArrayList<V>> map;
    boolean sorted;
    boolean unique;

    public LMergeMap(Boolean sorted) {
	this(sorted, true);
    }

    public LMergeMap(Boolean sorted, Boolean unique) {
	if (sorted) {
	    map = new TreeMap<K, ArrayList<V>>();
	} else {
	    map = new HashMap<K, ArrayList<V>>();
	}
	this.sorted = sorted;
	this.unique = unique;
    }

    public void clear() {
	map.clear();
    }

    public void addAll (LMergeMap<K,V> in) {
	for (K k : in.keySet()) {
	    map.put(k, in.get(k));
	}
    }

    public boolean containsKey(K key) {
	return map.containsKey(key);
    }

    public boolean containsValue(V value) {
	for (ArrayList<V> vals : map.values()) {
	    for (V v : vals) {
		if (v.equals(value)) {
		    return true;
		}
	    }
	}
	return false;
    }

    public Set<Entry<K, ArrayList<V>>> entrySet() {
	return map.entrySet();
    }

    public ArrayList<V> get(K key) {
	return map.get(key);
    }

    public int hashcode() {
	return map.hashCode();
    }

    public boolean isEmpty() {
	return map.isEmpty();
    }

    public Set<K> keySet() {
	return map.keySet();
    }

    public void put(K key, V value) {
	if (!map.containsKey(key)) {
	    map.put(key, new ArrayList<V>());
	}
	if (!unique || !map.get(key).contains(value)) {
	    map.get(key).add(value);
	}
    }

    public void put(K key, ArrayList<V> value) {
	if (!map.containsKey(key)) {
	    map.put(key, value);
	} else {
	    for (V v : value) {
		put(key, v);
	    }
	}
    }

    public void remove(K key) {
	if (map.containsKey(key)) {
	    map.get(key).clear();
	}
    }

    public int size() {
	return map.size();
    }

    public int numVals() {
	int sum = 0;
	for (ArrayList<V> vals : map.values()) {
	    sum += vals.size();
	}
	return sum;
    }

    public Collection<ArrayList<V>> values() {
	return map.values();
    }

    public ArrayList<V> valuesFlat() {
	ArrayList<V> out = new ArrayList<V>();
	for (ArrayList<V> vals : map.values()) {
	    out.addAll(vals);
	}
	return out;
    }

    public Map<K,V> flatten() {
	Map<K,V> out;
	if (sorted) {
	    out = new TreeMap<K,V>();
	} else {
	    out = new HashMap<K,V>();
	}
	for (K key : map.keySet()) {
	    ArrayList<V> val = map.get(key);
	    if (val.size() > 0) {
		out.put(key, val.get(0));
	    }
	}
	return out;
    }

    public LMergeMap<V, K> flip() {
	LMergeMap<V, K> flip = new LMergeMap<V, K>(sorted, unique);
	for (K key : map.keySet()) {
	    for (V val : map.get(key)) {
		flip.put(val, key);
	    }
	}
	return flip;
    }

    public void print() {
	for (K key : map.keySet()) {
	    System.out.println(key.toString());
	    for (V vals : get(key)) {
		System.out.println("   " + vals.toString());
	    }
	}
    }

}
