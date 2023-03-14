package org.usfirst.frc.team4999.lights.animations;

import java.util.Vector;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.commands.Command;

import static org.usfirst.frc.team4999.lights.AnimationUtils.*;

public class Stack implements Animation {

    Color[] colors;
    int delay;

    Color[] buffer, lastbuffer;
    int coloridx;
    int width, idx;

    Color background = Color.WHITE;

    Vector<Command> packetBuffer;


    public Stack(int size, int delay, Color... colors) {
        this.colors = colors;
        this.delay = delay;

        buffer = new Color[size];
        for(int i = 0; i < size; i++) {
            buffer[i] = background;
        }

        lastbuffer = buffer.clone();

        width = 0;
        idx = buffer.length;

        packetBuffer = new Vector<>();
    }

    @Override
    public Command[] getNextFrame() {
        if(idx < buffer.length) {
            buffer[idx] = background;
        }
        idx--;
        buffer[idx] = colors[coloridx];
        if(idx == width) {
            for(int i = 0; i < width; i++ ) {
                buffer[i] = colors[coloridx];
            }
            for(int i = width+1; i < buffer.length; i++) {
                buffer[i] = background;
            }
            idx = buffer.length;
            coloridx = (coloridx + 1) % colors.length;
            width++;
            if(width == buffer.length) {
                width = 0;
                for(int i = 0; i < buffer.length; i++) {
                    buffer[i] = background;
                }
            }
        }

        return displayColorBuffer(buffer);
    }

    @Override
    public int getFrameDelayMilliseconds() {
        return delay;
    }

}
