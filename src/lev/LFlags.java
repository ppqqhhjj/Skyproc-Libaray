package lev;

import java.io.Serializable;

/**
 *
 * @author Justin Swanson
 */
public class LFlags implements Serializable {

    boolean[] flags;
    private final int bitsPerInt = 8;

    public LFlags(int size) {
        flags = new boolean[size * bitsPerInt];
    }

    public LFlags(byte[] inFlags) {
        set(inFlags);
    }

    public final void set(byte[] inFlags) {
        flags = new boolean[inFlags.length * bitsPerInt];
        for (int i = 0; i < inFlags.length; i++) {
            int integer = Ln.bToUInt(inFlags[i]);
            for (int j = 0; j < bitsPerInt; j++) {
                flags[j + i * bitsPerInt] = (integer % 2) == 1;
                integer /= 2;
            }
        }
    }

    public final boolean is(int bit) {
        return flags[bit];
    }

    public final void set(int bit, boolean on) {
        flags[bit] = on;
    }

    public final byte[] export() {
        byte[] out = new byte[flags.length / bitsPerInt];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < bitsPerInt; j++) {
                if (flags[i * bitsPerInt + j]) {
                    out[i] += 1 * Math.pow(2, j);
                }
            }
        }
        return out;
    }

    public final int length() {
        return flags.length / bitsPerInt;
    }

    public final void clear() {
        for (int i = 0; i < flags.length; i++) {
            flags[i] = false;
        }
    }

    @Override
    public final String toString() {
        String out = "";
        for (boolean b : flags) {
            if (b) {
                out += "1";
            } else {
                out += "0";
            }
        }
        return out;
    }
}
