/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import lev.LExporter;

/**
 *
 * @author Justin Swanson
 */
class StringNull extends StringNonNull {

    public StringNull(String in) {
        super(in);
    }

    public StringNull() {
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        super.export(out, srcMod);
        out.write(0, 1);
    }

    @Override
    int getContentLength(Mod srcMod) {
        return super.getContentLength(srcMod) + 1;
    }

}
