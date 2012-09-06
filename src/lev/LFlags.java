package lev;

import java.io.Serializable;

/**
 * An object that is meant to hold a set of boolean flags.
 * Takes in byte arrays and converts each bit to its own flag.
 * @author Justin Swanson
 */
public class LFlags implements Serializable {

    boolean[] flags;
    private final int bitsPerInt = 8;

    /**
     *
     * @param size number of bytes-worth of flags to initialize.
     */
    public LFlags(int size) {
        flags = new boolean[size * bitsPerInt];
    }

    /**
     *
     * @param inFlags bytes to initialize flags to.
     */
    public LFlags(byte[] inFlags) {
        set(inFlags);
    }

    /**
     * Resizes LFlags to contain bytes and their associated flags
     * @param inFlags bytes to set LFlags to.
     */
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

    /**
     *
     * @param bit Bit/Flag to check
     * @return True if bit/Flag is on
     */
    public final boolean get(int bit) {
        return flags[bit];
    }

    /**
     *
     * @param bit Bit/Flag to check
     * @param on Sets the bit/flag on/off
     */
    public final void set(int bit, boolean on) {
        flags[bit] = on;
    }

    /**
     * Converts the boolean flags to a byte array.
     * @return Byte array containing all the flags as bits.
     */
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

    /**
     *
     * @return Length of the byte array representation
     */
    public final int length() {
        return flags.length / bitsPerInt;
    }

    /**
     * Sets all flags to false.
     */
    public final void clear() {
        for (int i = 0; i < flags.length; i++) {
            flags[i] = false;
        }
    }

    /**
     *
     * @return True if all flags are set to false
     */
    public boolean isZeros() {
	for (boolean b : flags) {
	    if (b) {
		return false;
	    }
	}
	return true;
    }

    /**
     *
     * @return String of 1's and 0's.  Beep boop beep.
     */
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
