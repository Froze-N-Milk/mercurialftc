# Setting up and Tuning your Mecanum Drive Base

Copy the contents of this directory into your teamcode folder, and remove the `@disabled` annotations from the tuners,
then change the values in `DemoMecanumDriveBase.java` according to these instructions:

1. Pick a tracker: <br /><br />
   I recommend choosing the insistent three wheeled tracker or the two wheeled tracker<br /><br />
2. Measure and set the constants you can: <br /><br />
    1. names of motors
    2. motors corresponding to encoders
    3. lateral distance (three wheel tracker only) + forward offset for the tracker
    4. calculate the encoder ticks converter ratios
    5. trackwidth, wheelbase and wheelradius
3. Use tuners to fine tune these values and find ones you couldn't determine yet
4. First, tune the tracker
    1. lateral distance tuner if using a three wheeled tracker
    2. x and y multipliers to ensure that each value is tracked accurately

## Tuning the lateral distance

## Tuning the x and y multipliers

run the tracker test, and move the robot a measured (long, ~1-2 m) distance along one axis, check to see how far the
tracker recorded, replace the multiplier with (actualDistance / trackedDistance), repeat for the other axis, repeat
several times for more accuracy if desired.
