package org.usfirst.frc.team4999.lights;

/**
 * A collection of global utility methods.
 * @author Jordan
 */
public class Utils {

    /**
     * Safely cast a 4-byte integer into a 1-byte byte.
     * <p>
     * @throws IllegalArgumentException If the cast would cause the value to change.
     * @param in The integer to convert
     * @return The converted byte
     */
    public static byte intToByte(int in) {
        if(in > 255 || in < 0) {
            System.out.format("Cannot fit %d in one byte!\n", in);
            throw new IllegalArgumentException("Input must fit in one unsigned byte");
        }
        return (byte) in;
    }
}
