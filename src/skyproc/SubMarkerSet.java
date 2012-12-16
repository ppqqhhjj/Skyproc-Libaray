/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

class SubMarkerSet<T extends SubRecord> extends SubRecord {

    Map<Type, T> set = new EnumMap<>(Type.class);
    ArrayList<Type> markers;
    T prototype;
    boolean forceMarkers = false;
    static Type loadedMarker;

    SubMarkerSet(T prototype) {
	super();
	this.prototype = prototype;
    }

    SubMarkerSet(T prototype, Type... markers) {
	super();
	this.markers = new ArrayList<>(Arrays.asList(markers));
	this.prototype = prototype;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	for (Type t : markers) {
	    if (set.containsKey(t)) {
		if (set.get(t).isValid()) {
		    SubData marker = new SubData(t);
		    marker.export(out, srcMod);
		    set.get(t).export(out, srcMod);
		    continue;
		}
	    }
	    if (forceMarkers) {
		SubData marker = new SubData(t);
		marker.export(out, srcMod);
	    }
	}
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	Type next = Record.getNextType(in);
	if (markers.contains(next)) {
	    logSync("", "Loaded Marker " + next);
	    loadedMarker = next;
	} else {
	    if (!set.containsKey(loadedMarker)) {
		set.put(loadedMarker, (T) prototype.getNew(next));
	    }
	    set.get(loadedMarker).parseData(in);
	}
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    SubRecord getNew(Type t) {
	SubMarkerSet out = new SubMarkerSet(prototype);
	out.markers = markers;
	out.forceMarkers = forceMarkers;
	return out;
    }

    @Override
    int getContentLength(Mod srcMod) {
	int out = 0;
	for (Type t : markers) {
	    if (set.containsKey(t)) {
		if (set.get(t).isValid()) {
		    out += 6 + set.get(t).getTotalLength(srcMod);
		    continue;
		}
	    }
	    if (forceMarkers) {
		out += 6;
	    }
	}
	return out;
    }

    public void clear() {
	set.clear();
    }

    public T get(Type marker) {
	if (!set.containsKey(marker)) {
	    T newItem = (T) prototype.getNew();
	    set.put(marker, newItem);
	}
	return set.get(marker);
    }

    @Override
    Boolean isValid() {
	return true;
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (T s : set.values()) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    ArrayList<Type> getTypes() {
	ArrayList<Type> out = new ArrayList<>(prototype.getTypes());
	out.addAll(markers);
	return out;
    }
}
