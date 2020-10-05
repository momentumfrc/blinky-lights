package org.usfirst.frc.team4999.lights.animations;

import java.util.ArrayList;

import org.usfirst.frc.team4999.lights.commands.*;

public class ClippedAnimation implements Animation {

    private Animation animation;
    private int startidx;
    private int totallength;

    private ArrayList<Command> commandBuffer;

    public ClippedAnimation(Animation animation, int startidx, int totallength) {
        this.animation = animation;
        commandBuffer = new ArrayList<>();

        this.startidx = startidx;
        this.totallength = totallength;
    }

    @Override
    public Command[] getNextFrame() {
        commandBuffer.clear();
        
        Command[] animationPackets = animation.getNextFrame();
        for(int i = 0; i < animationPackets.length; i++) {
            Command curr = animationPackets[i];
            if(curr != null) {
                Command[] clipped = curr.clip(startidx, totallength);
                for(int j = 0; j < clipped.length; j++) {
                    commandBuffer.add(clipped[j]);
                }
            }
        }

        return commandBuffer.toArray(new Command[]{});
    }

    @Override
    public int getFrameDelayMilliseconds() {
        return animation.getFrameDelayMilliseconds();
    }

}
