package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.commands.*;

import static org.usfirst.frc.team4999.lights.AnimationUtils.*;

public class BounceStack implements Animation {

    int delay;

    Color[] buffer;
    Color currentcolor;

    int stopidx, startidx, currentidx;

    Color background;

    public BounceStack(Color[] colors, Color background, int bgsize, int delay) {
        this.delay = delay;
        this.background = background;

        buffer = new Color[colors.length+bgsize];
        int i = 0;
        for(; i < colors.length; i++) {
            buffer[i] = colors[i];
        }
        for(; i < buffer.length; i++) {
            buffer[i] = background;
        }
        stopidx = colors.length;
        startidx = 0;
        currentidx = startidx;
        startidx = (startidx + 1) % buffer.length;
    }

    public BounceStack(Color[] colors, int bgsize, int delay) {
        this(colors, Color.WHITE, bgsize, delay);
    }

    @Override
    public Command[] getNextFrame() {

        currentcolor = buffer[currentidx];
        buffer[currentidx] = background;

        currentidx = (currentidx + buffer.length - 1) % buffer.length;

        buffer[currentidx] = currentcolor;

        if(currentidx == stopidx) {
            stopidx = (stopidx + 1) % buffer.length;
            currentidx = startidx;
            startidx = (startidx + 1) % buffer.length;
        }

        return displayColorBuffer(buffer);
    }

    @Override
    public int getFrameDelayMilliseconds() {
        return delay;
    }

}
