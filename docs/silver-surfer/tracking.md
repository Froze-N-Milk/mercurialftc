# Tracking

Silver Surfer needs a tracker to be tuned first in order to accurately measure motion the characteristic constants of the drive base.

Silver Surfer offers three built-in tracker options that all require deadwheels.

All three built in trackers require `.updatePose();` to be called on them regularly in order to accurately track position.

Tracking is done using the FTC field coordinate system.

## Trackers

### Two Wheel Tracker

Uses one parallel and one perpendicular encoders, with the imu for heading.

### Three Wheel Tracker

Uses two parallel and one perpendicular encoders, more vulnerable to having issues if it comes off the ground, but reduces loop time due to not using the imu.

### Insistent Three Wheel Tracker

The three wheel tracker but consults the imu every 10 cycles by default (configurable) and adjusts its heading to be that of the imu.

## Tuning

### Final Outcomes:

```java
// two wheel:
tracker = new TwoWheelTracker(
    startPose,
    new WheeledTrackerConstants.TwoWheeledTrackerConstants(
        new Vector2D(-302.2 / 2.0, 416.9 / 2.0),
        (3000.0 / 2953.6417571405955),
        (3000.0 / 2961.3925604638556),
        new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER),
        new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER)
    new Encoder(fl).setDirection(Encoder.Direction.FORWARD), // check that each encoder increases in the positive direction, if not change their directions!
    new Encoder(bl).setDirection(Encoder.Direction.REVERSE),
    imu_ex
);

// three wheels
tracker = new ThreeWheelTracker(
    startPose,
    new WheeledTrackerConstants.ThreeWheeledTrackerConstants(
        new Vector2D(-319.2 / 2.0, 0.0),
        (3000.0 / 2957.865463440085),
        (3000.0 / 2950.4555205066968),
        new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER),
        new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER),
        new EncoderTicksConverter(8192 / (Math.PI * 35), Units.MILLIMETER),
        392.93438
    ),
    new Encoder(fl).setDirection(Encoder.Direction.FORWARD), // check that each encoder increases in the positive direction, if not change their directions!
    new Encoder(fr).setDirection(Encoder.Direction.REVERSE),
    new Encoder(bl).setDirection(Encoder.Direction.REVERSE)
);
```

### Constants

* [ ] Configure your IMU\_EX

```java
IMU_EX imu_ex = new IMU_EX(opModeEX.hardwareMap.get(IMU.class, "imu"), AngleUnit.RADIANS);
imu_ex.initialize(new IMU.Parameters(
    new RevHubOrientationOnRobot(
        RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
        RevHubOrientationOnRobot.UsbFacingDirection.UP
    )
));
```

A wrapper for the imu that provides values as angles and can be updated and read from as the tracker sees fit. You should configure this as you would the standard imu. This needs to be done for all three of the trackers, but can be removed later for the non-insistent three wheel tracker.

* [ ] Give constants to Encoder Tick Converters

```java
new EncoderTicksConverter(ticksPerUnit, unit);
```

The number of ticks in one of the unit selected, should be (resolution / circumference)

* [ ] Set the center of rotation offset vector to (0, 0) and set the x and y multipliers to be 1

```java
new WheeledTrackerConstants.ThreeWheeledTrackerConstants(
        new Vector2D(),
        1,
        1,
        // ... snipped
```

* [ ] If using a three wheel tracker, set the track width to be the measured track width

The distance between the two parallel sensors, later this will be tuned to be more accurate, but for now this should be somewhat accurate

### Tuned Values

* [ ] Check that the encoders are the right direction

```java
new Encoder(fl).setDirection(Encoder.Direction.FORWARD);
```

Run the "Tracker Test" OpMode in the samples, this will allow you to drive the robot and allow you to see where it is.

As you move it forward the y value should increase, backward, it should decrease. If not, try changing the directions of the parallel sensors.

As you move to the right the x value should increase, to the left it should decrease. If not, try changing the direction of the perpendicular sensors.

* [ ] If using a three wheel tracker, empirically tune the track width

Run "Track Width Tuner (Automated)". This will spin your robot around \~5 times. The imu is likely to drift, so wait for the process to finish (the driver hub will print out your results) and then manually adjust the robot to be facing the same direction it started. Then, replace the track width with the new track width.

Download you new changes and run "Track Width Tuner (Automated Test)" to check that the value is accurate, the robot will turn 5 times clock wise, wait (allowing you to check that it is in deed accurate) and then turn back to the start (to show that it didn't drift).

Don't worry about any translation drift that happens during this process.

* [ ] Find the X and Y multipliers

Run "Tracker Test" and push the robot along the Y axis a set distance, I recommend \~3 meters. Use a measuring tape to ensure that this motion is accurate.

Set the Y multiplier to (actual distance travelled / distance travelled as reported by the tracker)

Repeat this for X.

```java
new WheeledTrackerConstants.ThreeWheeledTrackerConstants(
        new Vector2D(-302.2 / 2.0, 416.9 / 2.0),
        (3000.0 / 2957.865463440085), // x multiplier
        (3000.0 / 2950.4555205066968), // y multiplier
        // ... snipped
```

* [ ] Tune the center of rotation offset vector

Run "Tracker Test"

Rotate the robot 180 degrees and align it back to its starting position

Your center of rotation offset vector is: (-Y / 2, X / 2) where X and Y are from the pose of the robot.

Download the new center of rotation offset and check that as the robot rotates on the spot the position doesn't change too much.

```java
new WheeledTrackerConstants.ThreeWheeledTrackerConstants(
        new Vector2D(-302.2 / 2.0, 416.9 / 2.0),
        1,
        1,
        // ... snipped
```

All done!
