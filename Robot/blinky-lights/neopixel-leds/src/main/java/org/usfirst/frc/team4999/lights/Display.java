package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.commands.Command;

/**
 * A generic interface for any component that can render a sequence of Commands.
 */
public interface Display {
    /**
     * Render a sequence of LED commands.
     * @param commands the commands to render
     */
    void show(Command[] commands);
}
