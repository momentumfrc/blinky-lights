package org.usfirst.frc.team4999.tools;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.lights.BufferDisplay;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.tools.gui.BufferShower;

public class CommonTests {

    private static interface TestSteps {
        public void setup(Test test);
        public void animate(Test test);
        public void tearDown(Test test);
    }

    private static class Test {
        public final BufferDisplay display;
        public final TestAnimator animator;
        private List<TestSteps> steps;

        public Test(List<TestSteps> steps) {
            this.steps = steps;
            display = new BufferDisplay(80);
            animator = new TestAnimator(display);
        }

        public void run() {
            for (TestSteps step : steps) {
                step.setup(this);
            }
            for (TestSteps step : steps) {
                step.animate(this);
            }
            for (TestSteps step : steps) {
                step.tearDown(this);
            }
        }
    }

    private static TestSteps makeHeadlessTestSteps(Animation animation, int frames) {
        return new TestSteps() {
            @Override
            public void setup(Test test) {
                BrightnessFilter.setBrightness(1);
                test.animator.setAnimation(animation);
            }
            @Override
            public void animate(Test test) {
                test.animator.displayFrames(frames);
            }
            @Override
            public void tearDown(Test test) { }
        };
    }

    private static TestSteps makeCompareToFileTestSteps(String file) {
        return new TestSteps() {
            PixelComparator comparator = new PixelComparator();
            @Override
            public void setup(Test test) {
                test.display.addBufferListener(comparator);
            }
            @Override
            public void animate(Test test) { }
            @Override
            public void tearDown(Test test) {
                comparator.compareToFile(file, isHeadless());
            }
        };
    }

    private static TestSteps makeShowGUITestSteps(Animation animation, int frames) {
        return new TestSteps() {
            BufferShower gui = new BufferShower();
            @Override
            public void setup(Test test) {
                BrightnessFilter.setBrightness(1);
                test.animator.setAnimation(animation);
                test.display.addBufferListener(gui);
            }
            @Override
            public void animate(Test test) {
                Object framesLock = gui.getFramesLock();

                for (int i = 0; i < frames; i++) {
                    if (gui.getShouldStepFrames()) {
                        test.animator.displayFrames(1, false);
                        synchronized (framesLock) {
                            try {
                                framesLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        test.animator.displayFrames(1, !gui.getWindowIsClosed());
                    }
                }
            }
            @Override
            public void tearDown(Test test) {
                gui.close();
            }
        };
    }

    public static void headlessCompareToFile(Animation animation, int frames, String file) {
        List<TestSteps> steps = new ArrayList<>();

        steps.add(makeHeadlessTestSteps(animation, frames));
        steps.add(makeCompareToFileTestSteps(file));

        Test test = new Test(steps);
        test.run();
    }

    public static void guiCompareToFile(Animation animation, int frames, String file) {
        if(isHeadless()) {
            headlessCompareToFile(animation, frames, file);
        }

        List<TestSteps> steps = new ArrayList<>();

        steps.add(makeShowGUITestSteps(animation, frames));
        steps.add(makeCompareToFileTestSteps(file));

        Test test = new Test(steps);
        test.run();
    }

    public static void guiShowAnimation(Animation animation, int frames) {
        if(isHeadless()) {
            return;
        }

        List<TestSteps> steps = new ArrayList<>();

        steps.add(makeShowGUITestSteps(animation, frames));

        Test test = new Test(steps);
        test.run();
    }


    private static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

}
