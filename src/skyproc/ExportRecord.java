package skyproc;

import java.io.IOException;
import lev.LExporter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
abstract class ExportRecord {

    abstract void export(LExporter out, Mod srcMod) throws IOException, BadRecord;
}
