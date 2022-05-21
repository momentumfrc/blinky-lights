package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Packet;

import static org.usfirst.frc.team4999.lights.Utils.intToByte;

public class RunCommand extends Command {

    private static final byte SET_RUN = 0x03;

    private final int address;
    private final int length;
    private final Color color;

    public RunCommand(int address, Color color, int length) {
        this.address = address;
        this.color = color;
        this.length = length;
    }

    public int getAddress() {
        return address;
    }
    public int getLength() {
        return length;
    }
    public Color getColor() {
        return color;
    }

    @Override
    public Packet build() {
        byte[] data = {
            SET_RUN,
            intToByte(address),
            intToByte(color.getRed()),
            intToByte(color.getGreen()),
            intToByte(color.getBlue()),
            intToByte(length)
        };
        return new Packet(setSizeByte(data));
    }

    @Override
    public Command dim() {
        return new RunCommand(address, BrightnessFilter.dimColor(color), length);
    }

    @Override
    public Command[] clip(int clipstart, int cliplength) {
        int outstart, outend, outlength;

        if(this.address < clipstart) {
            outstart = clipstart;
        } else {
            outstart = this.address;
        }

        if(this.address + this.length > clipstart + cliplength) {
            outend = clipstart + cliplength;
        } else {
            outend = this.address + this.length;
        }

        outlength = outend - outstart;

        if(outlength > 0) {
            return new Command[] {new RunCommand(outstart, this.color, outlength)};
        } else {
            return new Command[] {};
        }
    }

}
