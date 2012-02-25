package skyproc;

import java.io.IOException;
import lev.LExportParser;

/**
 *
 * @author Justin Swanson
 */
abstract class ExportRecord {

    abstract void export(LExportParser out, Mod srcMod) throws IOException;
}
