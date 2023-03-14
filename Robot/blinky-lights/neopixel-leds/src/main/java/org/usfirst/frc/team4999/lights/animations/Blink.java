package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.commands.*;

public class Blink implements Animation {

    private Color[] colors;
    private int[] waittimes;

    private int idx;

    /**
     * Switches between colors
     * @param waittime how long to wait before switching color
     * @param colors colors to switch between
     */
    public Blink(int waittime, Color... colors) {
        int[] waittimes = new int[colors.length];
        for(int i = 0; i < waittimes.length; i++) {
            waittimes[i] = waittime;
        }
        this.colors = colors;
        this.waittimes = waittimes;
        idx = 0;
    }

    /**
     * Switches between colors
     * @param colors colors to switch between
     * @param waittimes how long to wait for each color before switching
     */
    public Blink(Color[] colors, int[] waittimes) {
        if(colors.length != waittimes.length) throw new IllegalArgumentException("Need exactly one waittime for every color");
        this.colors = colors;
        this.waittimes = waittimes;
        idx = 0;
    }

    @Override
    public Command[] getNextFrame() {
        Command out[] =  {new StrideCommand(0, colors[idx], 1, 1).dim()};
        idx = (idx + 1) % waittimes.length;
        return out;
    }

    @Override
    public int getFrameDelayMilliseconds() {
        return waittimes[idx];
    }

}
