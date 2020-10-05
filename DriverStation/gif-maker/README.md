# GIF-MAKER
Renders an animation from the [neopixel-leds](https://github.com/momentumfrc/Momentum-Tools/tree/master/neopixel-leds) library into a gif.

## Usage
1. Open Main.java
1. Create the animation you would like to render \
  `Animation bounce = new Bounce(Color.WHITE, rainbowcolors, 40, 20);`
1. Create a GifAnimator with the animation, the clock speed, the output file path, and the number of leds to render
  `GifAnimator animator = new GifAnimator(bounce, 40, "gifs\\Bounce.gif", 80); // (animation, clock speed, output filepath, number of leds)`\
  The clock speed should be a multiple of 10. It represents the delay between frames in the output gif. If the animation you chose has a constant
  delay between frames, use that value. If the animation you choose has a variable refresh rate, use the greatest common divisor of all the possible animation delays.
1. Render as many frames as you wish to appear in the gif. Ideally, you'd render enough frames that the gif loops perfectly.\
  `animator.renderFrames(80, true);`\
  Beware that `GifAnimator.renderFrames()` can throw an `IOException` and wrap it in a try-catch block.
1. Open a terminal, cd to the gif-maker directory, and run `mvn exec:java` to generate the gif.
