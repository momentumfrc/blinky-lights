# Blinky-Lights
An animation framework for using Addressable LEDs on an FRC robot.


## Overview
Fancy-looking LEDs add that extra level of percieved expertise to any FRC robot.
It doesn't matter if you have no autonomous, if you can't score any points, if
there are more components on the floor than left attached to the robot, so long
as you have blinking LEDS you are considered a "professional" team. This library
is FRC Team 4999's home-grown solution for interfacing with and animating
addressable LEDs.

## Hardware
No software in the world is going to be able to animate addressable LEDs if
they're not attached to the robot. This library provides support for two methods
of attaching the LEDs: direct connection to a PWM port and indirect connection
via I2C and an arduino. However, if you have a home-grown solution that does
not fit into either of those two categories, it is still possible to take
advantage of the animation library by writing a custom adapter.

For more details, see [hardware.md](docs/hardware.md)
