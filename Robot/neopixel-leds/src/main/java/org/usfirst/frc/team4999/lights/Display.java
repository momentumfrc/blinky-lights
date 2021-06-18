package org.usfirst.frc.team4999.lights;

/**
 * A generic interface for any component that can render and show the LED output.
 * @author jordan
 */
public interface Display {
	/**
	 * Display the packets
	 * @param commands The packets to show
	 */
	void show(Packet[] commands);
}
