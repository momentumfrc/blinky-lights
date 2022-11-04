package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.Packet;
import static org.usfirst.frc.team4999.lights.Utils.intToByte;


public abstract class Command {

    public abstract Packet build();
    public abstract Command dim();
    public abstract Command[] clip(int clipaddress, int cliplength);

    protected static byte[] setSizeByte(byte[] data) {
        byte[] out = new byte[data.length + 1];
        out[0] = intToByte(data.length);
        for(int i = 0; i < data.length; i++) {
            out[i+1] = data[i];
        }
        return out;
    }
}
