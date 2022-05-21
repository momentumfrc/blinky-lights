package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.commands.Command;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class AddressableLEDDisplay implements Display {
    private final AddressableLED leds;
    private final AddressableLEDBuffer buffer;

    private final static int TEST_PATTERN_BRIGHTNESS = 200;

    private BufferDisplay renderer;

    public AddressableLEDDisplay(int pwm, int length) {
        leds = new AddressableLED(pwm);
        buffer = new AddressableLEDBuffer(length);
        renderer = new BufferDisplay(length);

        leds.setLength(length);

        showTestPattern();

        leds.start();
    }

    private void showTestPattern() {
        int length = buffer.getLength();
        for(int i = 0; i < length; i++) {
            buffer.setHSV(i, (i * 180) / length, 255, TEST_PATTERN_BRIGHTNESS);
        }
        leds.setData(buffer);
    }

    @Override
    public void show(Command[] commands) {
        renderer.show(commands);

        Color[] renderedBuffer = renderer.getFrontBuffer();
        for(int i = 0; i < renderedBuffer.length; ++i) {
            Color curr = renderedBuffer[i];
            buffer.setRGB(i, curr.getRed(), curr.getGreen(), curr.getBlue());
        }

        leds.setData(buffer);
    }

    public void stop() {
        leds.stop();
    }
}
