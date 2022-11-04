package org.usfirst.frc.team4999.lights;

import java.util.Vector;

import org.usfirst.frc.team4999.lights.commands.StrideCommand;
import org.usfirst.frc.team4999.lights.commands.*;

public class AnimationUtils {

    private static Vector<Command> commandBuffer = new Vector<>();
    public static Command[] displayColorBuffer(Color[] buffer) {
        commandBuffer.clear();

        Color curr = buffer[0];
        int addr = 0;
        int len = 1;
        for(int i = 1; i < buffer.length; i++) {
            if(!curr.equals(buffer[i])){
                commandBuffer.add(new StrideCommand(addr, curr, len, buffer.length).dim());
                curr = buffer[i];
                addr = i;
                len = 1;
            } else {
                len++;
            }
        }
        commandBuffer.add(new StrideCommand(addr, curr, len, buffer.length).dim());

        return commandBuffer.toArray(new Command[]{});
    }
}
