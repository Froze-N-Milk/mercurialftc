# Demonstration of paired auto and teleop

This is a demonstration of how to carry over subsystems from an auto to a teleop, and not wipe the scheduler inbetween
This works with any number of autos or teleops as long as the subsystems and the interoperation code used in the middle is 
consistent.

These files include only the basics of the interoperation necessities, and suggestions of where to add your own code.

## Why store your subsystems across auto and into teleop?

We do so in order to ensure that our tracking of positions and states is kept between the two modes.
This means we can retain our currently tracked positions for our localiser, our our tracked arm angle, or some other state
The main target of this is to ease the use of localisation and complex drive control using it across auto and into teleop.