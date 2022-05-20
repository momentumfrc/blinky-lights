package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Packet;

import static org.usfirst.frc.team4999.lights.Utils.intToByte;

public class FiniteStrideCommand extends Command {

    private static final byte SET_STRIDE_RANGED = 0x05;

    private final int address;
    private final Color color;
    private final int length;
    private final int stride;
    private final int totalLength;

    public FiniteStrideCommand(int address, Color color, int length, int stride, int totalLength) {
        this.address = address;
        this.color = color;
        this.length = length;
        this.stride = stride;
        this.totalLength = totalLength;
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

    public int getTotalLength() {
        return totalLength;
    }

    @Override
    public Packet build() {
        byte[] data = {
            SET_STRIDE_RANGED,
            intToByte(address),
            intToByte(color.getRed()),
            intToByte(color.getGreen()),
            intToByte(color.getBlue()),
            intToByte(length),
            intToByte(stride),
            intToByte(totalLength)
    };
    return new Packet(setSizeByte(data));
    }

    @Override
    public Command dim() {
        return new FiniteStrideCommand(address, BrightnessFilter.dimColor(color), length, stride, totalLength);
    }

    @Override
    public Command[] clip(int clipaddress, int cliplength) {
        int endAddress = address + totalLength;
        int clipEndAddress = clipaddress + cliplength;
        int outEndAddress = Math.min(endAddress, clipEndAddress);
        int outLength = outEndAddress - clipaddress;

        return new StrideCommand(address, color, length, stride).clip(clipaddress, outLength);
    }
    
}
