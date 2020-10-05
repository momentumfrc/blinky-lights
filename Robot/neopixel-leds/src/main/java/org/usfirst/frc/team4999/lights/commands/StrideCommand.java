package org.usfirst.frc.team4999.lights.commands;

import java.util.ArrayList;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Packet;

import static org.usfirst.frc.team4999.lights.Utils.intToByte;

public class StrideCommand extends Command {

    private static final byte SET_STRIDE = 0x04;

    private final int address;
    private final Color color;
    private final int length;
    private final int stride;

    public StrideCommand(int address, Color color, int length, int stride) {
        this.address = address;
        this.color = color;
        this.length = length;
        this.stride = stride;
    }

    public int getAddress() {
        return address;
    }
    public Color getColor() {
        return color;
    }
    public int getLength() {
        return length;
    }
    public int getStride() {
        return stride;
    }

    @Override
    public Packet build() {
        byte[] data = {
            SET_STRIDE,
            intToByte(address), 
            intToByte(color.getRed()),
            intToByte(color.getGreen()),
            intToByte(color.getBlue()),
            intToByte(length),
            intToByte(stride)
    };
    return new Packet(setSizeByte(data));
    }

    @Override
    public Command dim() {
        return new StrideCommand(address, BrightnessFilter.dimColor(color), length, stride);
    }

    @Override
    public Command[] clip(int clipaddress, int cliplength) {
        if(stride == 0) {
            // A stride of 0 is functionally equivalent to a Run command, so there's no need to duplicate the trimming logic
            return new RunCommand(address, color, length).clip(clipaddress, cliplength);
        }

        ArrayList<Command> out = new ArrayList<>();

        int offset = address % stride;

        // If the clipped region starts in the middle of a stride, it breaks the pattern
        //                                0 1 2 3 4 5 6 7 8 9 
        // Imagine this is the pattern: [ X X - - X X - - X X ]
        // Imagine we want to clip here:           |         |
        // We need to somehow paint index 5 without painting index 4
        // So we use a run for index 5, then start the clipped stride at index 8
        int clipoffset = ( clipaddress - offset + stride ) % stride;
        if(clipoffset < length) {
            int runAddr = clipaddress;
            int runEndAddr = clipaddress - clipoffset + length;
            int runLen = runEndAddr - runAddr;

            if(runLen > 0 && runEndAddr > address) {
                out.add(new RunCommand(runAddr, color, runLen));
            }
        }

        // Now we need to get the first index inside the clipped region that corresponds to a repeat of the stride
        int strideAddr = clipaddress - clipoffset + stride;
        // Finally, we calculate the length of the stride so that the stride will end when the clipped region ends
        int strideEnd = clipaddress + cliplength;
        int strideLength = strideEnd - strideAddr;

        if(strideLength > 0) {
            out.add(new FiniteStrideCommand(strideAddr, color, length, stride, strideLength));
        }
        
        return out.toArray(new Command[]{});

    }
    
}
