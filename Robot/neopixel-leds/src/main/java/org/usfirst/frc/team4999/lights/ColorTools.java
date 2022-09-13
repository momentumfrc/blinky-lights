package org.usfirst.frc.team4999.lights;

public class ColorTools {
    public static class GradientStop {
        private final int pos;
        private final Color color;

        public GradientStop(int pos, Color color) {
            this.pos = pos;
            this.color = color;
        }

        public int getPos() {
            return pos;
        }

        public Color getColor() {
            return color;
        }
    }

    public static Color[] getGradient(int gradientSize, GradientStop[] stops) {
        if(stops[0].getPos() != 0) {
            throw new IllegalArgumentException("Missing initial gradient stop");
        }
        for(int i = 0; i < stops.length; ++i) {
            GradientStop stop = stops[i];
            if(stop.getPos() < 0 || stop.getPos() >= gradientSize) {
                throw new IllegalArgumentException("Gradient stop pos is out of bounds");
            }
            if(i < stops.length - 1) {
                GradientStop nextStop = stops[i + 1];
                if(stop.getPos() >= nextStop.getPos()) {
                    throw new IllegalArgumentException("Gradient stop positions must always increase");
                }
            }
        }
        if(stops[stops.length - 1].getPos() < gradientSize - 1) {
            // If a final gradient stop is not specified, we guess it
            // by wrapping-around back to the initial gradient stop

            GradientStop[] newStops = new GradientStop[stops.length + 1];
            System.arraycopy(stops, 0, newStops, 0, stops.length);

            GradientStop lastDefinedStop = stops[stops.length - 1];

            Color srcColor = lastDefinedStop.getColor();
            Color targetColor = stops[0].getColor();

            int finalDiffLen = gradientSize - lastDefinedStop.getPos();

            float lastStopFrac = (finalDiffLen - 1) / ((float)finalDiffLen);

            float deltaR, deltaG, deltaB;
            deltaR = (targetColor.getRed() - srcColor.getRed()) * lastStopFrac;
            deltaG = (targetColor.getGreen() - srcColor.getGreen()) * lastStopFrac;
            deltaB = (targetColor.getBlue() - srcColor.getBlue()) * lastStopFrac;

            newStops[newStops.length - 1] = new GradientStop(
                gradientSize - 1,
                new Color(
                    (int)(srcColor.getRed() + deltaR),
                    (int)(srcColor.getGreen() + deltaG),
                    (int)(srcColor.getBlue() + deltaB)
                )
            );

            stops = newStops;
        }
        if(stops[stops.length - 1].getPos() != gradientSize - 1) {
            throw new IllegalArgumentException("Missing final gradient stop");
        }
        Color[] gradientBuff = new Color[gradientSize];
        Color currStopC, nextStopC;
        float deltaR, deltaG, deltaB;
        for(int i = 0; i < stops.length-1; ++i) {
            GradientStop currStop = stops[i];
            GradientStop nextStop = stops[i + 1];
            currStopC = currStop.getColor();
            nextStopC = nextStop.getColor();
            int stopPixelDiff = nextStop.getPos() - currStop.getPos();
            deltaR = ( nextStopC.getRed() - currStopC.getRed() ) / ((float) stopPixelDiff);
            deltaG = ( nextStopC.getGreen() - currStopC.getGreen() ) / ((float) stopPixelDiff);
            deltaB = ( nextStopC.getBlue() - currStopC.getBlue() ) / ((float) stopPixelDiff);
            for(int j = currStop.getPos(); j < nextStop.getPos(); ++j) {
                int pixelDiff = j - currStop.getPos();
                Color currColor = new Color(
                    currStopC.getRed() + (int)(deltaR * pixelDiff),
                    currStopC.getGreen() + (int)(deltaG * pixelDiff),
                    currStopC.getBlue() + (int)(deltaB * pixelDiff)
                );
                gradientBuff[j] = currColor;
            }
        }
        GradientStop finalStop = stops[stops.length - 1];
        gradientBuff[finalStop.getPos()] = finalStop.color;

        return gradientBuff;
    }

    public static Color[] getSmearedColors(Color[] colors, int smear) {
        int gradientSize = colors.length * smear;

        GradientStop[] stops = new GradientStop[colors.length];
        for(int i = 0; i < colors.length; ++i) {
            stops[i] = new GradientStop(i * smear, colors[i]);
        }

        return getGradient(gradientSize, stops);
    }

    public static Color[] getColorTails(Color[] colors, Color background, int tailLen, int spacing) {
        int totalLength = 1;
        int stopsPerColor = 2;
        if(tailLen == 0) {
            tailLen = 1;
        }
        totalLength += tailLen;
        if(spacing > 0) {
            stopsPerColor += 1;
            totalLength += spacing;
        }

        GradientStop[] stops = new GradientStop[colors.length * stopsPerColor];
        for(int i = 0; i < colors.length; ++i) {
            stops[i * stopsPerColor] = new GradientStop(i * totalLength, colors[i]);

            stops[(i * stopsPerColor) + 1] = new GradientStop((i * totalLength) + tailLen, background);

            if(spacing > 0) {
                stops[(i * stopsPerColor) + 2] = new GradientStop((i * totalLength) + tailLen + spacing, background);
            }
        }

        return getGradient(colors.length * (1 + tailLen + spacing), stops);
    }

}
