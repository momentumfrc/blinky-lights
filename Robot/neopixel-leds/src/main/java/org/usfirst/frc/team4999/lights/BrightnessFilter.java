package org.usfirst.frc.team4999.lights;

/**
 * Reduces the brightness of a color.
 * <p>
 * This class allows all colors to be dimmed by a fixed amount. As a result, the overall brightness
 * of the LEDs can be adjusted using {@link setBrightness}.
 * @author Jordan
 */
public class BrightnessFilter {
	
	private static double brightness = 0.4;

	/**
	 * Set the brightness.
	 * <p>
	 * This function will clip input values to the range [0,1].
	 * @param brightness The new brightness
	 */
	public static void setBrightness(double brightness) {
		BrightnessFilter.brightness = truncate(brightness);
	}
	
	private static double truncate(double in) {
		if(in > 1) return 1;
		if(in < 0) return 0;
		return in;
	}
	
	/**
	 * Dim the input value according to the currently configured brightness.
	 * @param value The value to dim
	 * @return The dimmed value
	 */
	public static int dimValue(int value) {
		return (int)(value * brightness);
	}
	
	/**
	 * Dim the input color according to the currently configured brightness.
	 * @param in The color to dim
	 * @return The dimmed color
	 */
	public static Color dimColor(Color in) {
		return new Color(dimValue(in.getRed()), dimValue(in.getGreen()), dimValue(in.getBlue()));
	}

}
