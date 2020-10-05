package org.usfirst.frc.team4999.lights;

public class Utils {
    public static byte intToByte(int in) {
		if(in > 255 || in < 0) {
			System.out.format("Cannot fit %d in one byte!\n", in);
			throw new IllegalArgumentException("Input must fit in one unsigned byte");
		}
		return (byte) in;
	}
}
