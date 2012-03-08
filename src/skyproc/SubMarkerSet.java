/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

class SubMarkerSet<T extends SubRecord> extends SubRecord {

    Map<Type, SubRecord> set = new EnumMap<Type, SubRecord>(Type.class);
    ArrayList<Type> markers;
    SubRecord active;
    T prototype;
    boolean forceMarkers = false;

    SubMarkerSet(T prototype) {
        super(prototype.getTypes());
    }

    SubMarkerSet(T prototype, Type... markers) {
        super(markers);
        Set<Type> types = new HashSet<Type>(Arrays.asList(markers));
        types.addAll(Arrays.asList(prototype.type));
        type = types.toArray(type);
        this.markers = new ArrayList<Type>(Arrays.asList(markers));
        this.prototype = prototype;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        for (Type t : markers) {
            if (set.containsKey(t)) {
                if (set.get(t).isValid()) {
                    SubData marker = new SubData(t);
                    marker.forceExport(true);
                    marker.export(out, srcMod);
                    set.get(t).export(out, srcMod);
                    continue;
                }
            }
            if (forceMarkers) {
                SubData marker = new SubData(t);
                marker.forceExport(true);
                marker.export(out, srcMod);
            }
        }
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        Type next = Record.getNextType(in);
        if (markers.contains(next)) {
            logSync("", "Loaded Marker " + next);
            active = prototype.getNew(next);
            set.put(next, active);
        } else {
            active.parseData(in);
        }
    }

    @Override
    int getHeaderLength() {
        return 0;
    }

    @Override
    SubRecord getNew(Type type) {
        throw new UnsupportedOperationException("Not supported.");
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

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    Boolean isValid() {
        return true;
    }
}
