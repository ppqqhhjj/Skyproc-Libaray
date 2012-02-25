/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import lev.LExportParser;

/**
 *
 * @author Justin Swanson
 */
class StringNonNull extends Record {

    String data;
    private static Type[] type = {Type.NULL};

    StringNonNull() {
    }

    StringNonNull(String in) {
        data = in;
    }

    public void set (String in) {
        data = in;
    }

    @Override
    void export(LExportParser out, Mod srcMod) throws IOException {
        out.write(getContentLength(srcMod), 2);
        out.write(data);
    }

    @Override
     Boolean isValid() {
        return data != null;
    }

    public boolean equalsIgnoreCase (StringNull in) {
        return equalsIgnoreCase(in.data);
    }

    public boolean equalsIgnoreCase (String in) {
        return data.equalsIgnoreCase(in);
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public String print() {
        return toString();
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new StringNonNull();
    }

    @Override
    int getHeaderLength() {
        return 2;
    }

    @Override
    int getFluffLength() {
        return 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
        return data.length();
    }

    @Override
    int getSizeLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringNonNull other = (StringNonNull) obj;
        if ((this.data == null) ? (other.data != null) : !this.data.equals(other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }


}
