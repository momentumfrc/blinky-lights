package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Packet;

import static org.usfirst.frc.team4999.lights.Utils.intToByte;

import org.usfirst.frc.team4999.lights.BrightnessFilter;

public class SingleCommand extends Command {

    private static final byte SET_SINGLE = 0x02;

    private final int address;
    private final Color color;

    public SingleCommand(int address, Color color) {
        this.address = address;
        this.color = color;
    }

    public int getAddress() {
        return address;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public Packet build() {
        byte[] data = {
            SET_SINGLE,
            intToByte(address),
            intToByte(color.getRed()),
            intToByte(color.getGreen()),
            intToByte(color.getBlue())
        };
        return new Packet(setSizeByte(data));
    }

    @Override
    public Command dim() {
        return new SingleCommand(address, BrightnessFilter.dimColor(color));
    }

    @Override
    public Command[] clip(int startaddress, int totallength) {
        if(address < startaddress || address >= startaddress + totallength) {
            return new Command[] {};
        } else {
            return new Command[] {this};
        }
    }

}
