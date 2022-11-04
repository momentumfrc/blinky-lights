package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.Packet;

import static org.usfirst.frc.team4999.lights.Utils.intToByte;

public class SyncCommand extends Command {

    private static final byte SYNC_BYTE = intToByte(0xFF);

    @Override
    public Packet build() {
        byte[] data = new byte[16];
        for(int i = 0; i < data.length; i++) {
            data[i] = SYNC_BYTE;
        }
        return new Packet(data);
    }

    @Override
    public Command dim() {
        throw new UnsupportedOperationException("Sync commands cannot be dimmed");
    }

    @Override
    public Command[] clip(int clipaddress, int cliplength) {
        throw new UnsupportedOperationException("Sync commands cannot be clipped");
    }

}
