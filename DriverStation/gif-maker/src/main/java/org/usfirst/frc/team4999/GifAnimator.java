package org.usfirst.frc.team4999;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.usfirst.frc.team4999.lights.Packet;
import org.usfirst.frc.team4999.lights.animations.Animation;

public class GifAnimator {

    private static final int PIXEL_SIZE = 20;

    private Animation animation;
    private int clockSpeedMs;
    private ImageOutputStream output;
    private Color[] pixels;

    public GifAnimator(Animation animation, int clockSpeedMs, String path, int numPixels) {
        this.animation = animation;
        this.clockSpeedMs = clockSpeedMs;
        this.pixels = new Color[numPixels];
        try {
            this.output = new FileImageOutputStream(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
		}
    }

    private int unsignedByteValue(byte b) {
        // See https://stackoverflow.com/questions/11380062/what-does-value-0xff-do-in-java
        // Basically, when the byte is cast to an int, it is sign-extended.
        // By masking only the last byte, we remove the sign-extended bits.
        return ((int) b) & 0xff;
    }

    private void renderColors(Packet[] packets) {

        int start, length, repeat, totallength;
        
        for(Packet packet : packets) {
            byte[] b = packet.getData();
            Color c;
            switch(b[1]) {
            case 0x02:
                this.pixels[unsignedByteValue(b[2])] = new Color(unsignedByteValue(b[3]), unsignedByteValue(b[4]), unsignedByteValue(b[5]));
                break;
            case 0x03:
                c = new Color(unsignedByteValue(b[3]), unsignedByteValue(b[4]), unsignedByteValue(b[5]));
                for(int i = unsignedByteValue(b[2]); i < Math.min(unsignedByteValue(b[2])+unsignedByteValue(b[6]), this.pixels.length); i++) {
                    this.pixels[i] = c;
                }
                break;
            case 0x04:
                c = new Color(unsignedByteValue(b[3]), unsignedByteValue(b[4]), unsignedByteValue(b[5]));
                start = unsignedByteValue(b[2]);
                length = unsignedByteValue(b[6]);
                repeat = unsignedByteValue(b[7]);
                for(int r = 0; r < this.pixels.length; r += repeat) {
                    for(int i = r+start; i < r+start+length && i < this.pixels.length; i++) {
                        this.pixels[i] = c;
                    }
                }
                
                break;
            case 0x05:
                c = new Color(unsignedByteValue(b[3]), unsignedByteValue(b[4]), unsignedByteValue(b[5]));
                start = unsignedByteValue(b[2]);
                length = unsignedByteValue(b[6]);
                repeat = unsignedByteValue(b[7]);
                totallength = unsignedByteValue(b[8]);
                for(int r = 0; r < start + totallength && r < this.pixels.length; r += repeat) {
                    for(int i = r+start; i < start + totallength && i < r+start+length && i < this.pixels.length; i++) {
                        this.pixels[i] = c;
                    }
                }
                break;
            default:
                break;
            }
        }
    }

    private BufferedImage renderImage() {
        BufferedImage img = new BufferedImage(pixels.length * PIXEL_SIZE, PIXEL_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        for(int i = 0; i < pixels.length; i++) {
            Rectangle rect = new Rectangle(i * PIXEL_SIZE, 0, PIXEL_SIZE, PIXEL_SIZE);
            g.setPaint(pixels[i]);
            g.fill(rect);
        }

        return img;
    }

    public void renderFrames(int numFrames, boolean showProgress) throws IOException {
        ImageShower shower = null;
        if(showProgress) {
            shower = new ImageShower();
        }
        GifSequenceWriter gifMaker = null;
        gifMaker = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, clockSpeedMs, true);
        int delay = 0;
        BufferedImage curr = null;
        for(int i = 0; i < numFrames; i++) {
            if(delay <= 0) {
                delay = animation.getFrameDelayMilliseconds();
                renderColors(animation.getNextFrame());
                curr = renderImage();
            }
            gifMaker.writeToSequence(curr);
            delay -= clockSpeedMs;

            if(shower != null) {
                shower.showImage(curr);
                try {
                    Thread.sleep(clockSpeedMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        gifMaker.close();

        if(shower != null)
            shower.dispose();
    }
}
