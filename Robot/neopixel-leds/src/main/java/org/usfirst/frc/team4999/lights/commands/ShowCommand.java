package org.usfirst.frc.team4999.lights.commands;

import org.usfirst.frc.team4999.lights.Packet;

public class ShowCommand extends Command {

    private static final byte DISPLAY_FRAME = 0x01;

    @Override
    public Packet build() {
        byte[] data = {
            DISPLAY_FRAME
        };
		return new Packet(setSizeByte(data));
    }

    @Override
    public Command dim() {
        throw new UnsupportedOperationException("Show command cannot be dimmed");
    }

    @Override
    public Command[] clip(int clipaddress, int cliplength) {
        throw new UnsupportedOperationException("Show command cannot be clipped");
    }
    
}
