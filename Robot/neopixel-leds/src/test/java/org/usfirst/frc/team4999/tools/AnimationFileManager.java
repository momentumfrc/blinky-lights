package org.usfirst.frc.team4999.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.usfirst.frc.team4999.lights.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class AnimationFileManager {

    @SuppressWarnings("unused")
    private static class JSONColor {
        public int r;
        public int g;
        public int b;

        public JSONColor() {}
        public JSONColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    @SuppressWarnings("unused")
    private static class AnimationFrame {
        public List<JSONColor> frame;

        public AnimationFrame() {}
        public AnimationFrame(List<JSONColor> frame) {
            this.frame = frame;
        }
    }

    @SuppressWarnings("unused")
    private static class Animation {
        public List<AnimationFrame> frames;

        public String version = ANIMATION_FILE_VERSION;

        public Animation() {}
        public Animation(List<AnimationFrame> frames) {
            this.frames = frames;
            this.version = ANIMATION_FILE_VERSION;
        }
    }

    @FunctionalInterface
    private static abstract interface Migration {
        public abstract void migrate(String name);
    }

    private static class BinMigration implements Migration{
        private static final String ANIMATION_FILE_LOCATION = "animationFiles";
        private static final String EXTENSION = "bin";
        @SuppressWarnings("unchecked")
        public void migrate(String name) {
            String filename = String.format("%s.%s", name, EXTENSION);
            File file = Path.of(ANIMATION_FILE_LOCATION, filename).toFile();

            if(!file.exists()) {
                return;
            }

            FileInputStream fileIn = null;
            ObjectInputStream objectIn = null;
            Vector<java.awt.Color[]> readHistory = null;
            try {
                fileIn = new FileInputStream(file);
                objectIn = new ObjectInputStream(fileIn);
                
                Object obj = objectIn.readObject();
                readHistory = (Vector<java.awt.Color[]>) obj;        
            } catch (Exception e) {
                return;
            } finally {
                if(objectIn != null) {
                    try {
                        objectIn.close();
                    } catch (IOException e) {}
                }
                if(fileIn != null) {
                    try {
                        fileIn.close();
                    } catch (IOException e) {}
                }
            }

            if(readHistory == null) {
                return;
            }
            try {
                List<Color[]> convertedHistory = readHistory.stream().map(
                    buff -> Arrays.stream(buff).map(
                        color -> new Color(color.getRed(), color.getGreen(), color.getBlue())
                    ).toArray(Color[]::new)
                ).collect(Collectors.toList());
                saveFile(convertedHistory, name);
            } catch(IOException e) {
                return;
            }

            file.delete();
        }
    }

    private static final String ANIMATION_FILE_LOCATION = "animationFiles";
    private static final String ANIMATION_FILE_VERSION = "v1.0";
    private static final String ANIMATION_FILE_EXTENSION = "json";

    private static final List<Migration> migrations = new ArrayList<Migration>();

    static {
        migrations.add(new BinMigration());
    }

    private static ObjectMapper _mapper;

    private static List<Color[]> animationToBufferList(Animation an) {
        return an.frames.stream().map(
            frame -> frame.frame.stream().map(
                color -> new Color(color.r, color.g, color.b)
            ).toArray(Color[]::new)
        ).collect(Collectors.toList());
    }

    private static Animation bufferListToAnimation(List<Color[]> an) {
        return new Animation(
            an.stream().map(
                buff -> new AnimationFrame(
                    Arrays.stream(buff).map(
                        color -> new JSONColor(color.getRed(), color.getGreen(), color.getBlue())
                    ).collect(Collectors.toList())
                )
            ).collect(Collectors.toList())
        );
    }
    
    private static Path animationPathFromName(String name) {
        return Path.of(ANIMATION_FILE_LOCATION, String.format("%s.%s", name, ANIMATION_FILE_EXTENSION));
    }

    private static void runMigrations(String name) {
        for (Migration migration : migrations) {
            migration.migrate(name);
        }
    }

    private static ObjectMapper getObjectMapper() {
        if(_mapper == null) {
            _mapper = new ObjectMapper();
        }
        return _mapper;
    }

    public static List<Color[]> loadFile(String name) throws IOException {
        runMigrations(name);
        ObjectMapper mapper = getObjectMapper();
        File jsonFile = animationPathFromName(name).toFile();
        Animation value = mapper.readValue(jsonFile, Animation.class);
        if(!value.version.equals(ANIMATION_FILE_VERSION)){
            throw new IOException(String.format("No migration from %s to %s", value.version, ANIMATION_FILE_VERSION));
        }
        return animationToBufferList(value);
    } 

    public static void saveFile(List<Color[]> animation, String name) throws IOException {
        ObjectMapper mapper = getObjectMapper();
        File jsonFile = animationPathFromName(name).toFile();
        Animation value = bufferListToAnimation(animation);
        mapper.writeValue(jsonFile, value);
    }
}
