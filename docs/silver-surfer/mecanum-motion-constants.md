# Mecanum Motion Constants

Mecanum motion constants contain both velocities and accelerations

Silver Surfer assumes that the velocities are the actual maximum velocities, and thus uses them to determine how much power the motors can be given, so it is important that they are accurate.

The accelerations are not so important, and it is fine if they fall a bit short of the actual maximum accelerations.

As mecanums move fastest at 90 degrees above the positive x-axis (forward and backward), slower at 0 degrees above the positive x-axis (left and right), and slowest at 45 degrees above the positive x-axis (between the two), all three values need to be measured for mercurial to do a better job of calculating robot performance.&#x20;

To find the the maximum translational velocities, plug in a controller as gamepad 1.

Place the robot facing forward, then rotate it by:

0 degrees if you are running a Y tuner

45 degrees if you are running an angled tuner

90 degrees if you are running an X tuner

Then, press init and run, the robot will drive forward relative to you.

Press the 'a' button once you feel the robot is at maximum velocity and has been at it for \~ 1 second.

The robot will stop moving and the three recorded values will be displayed on the screen, use them to form a VoltagePerformanceEnforcer:

```java
// i.e. for the Y translation values:
VoltagePerformanceEnforcer translationalYEnforcer = new VoltagePerformanceEnforcer(
    13.031, // recorded voltage
    0.9747773604750232, // recorded current
    1648.569587035565 // recorded velocity, in millimeters
);
```

Rotation is the same, but the robot spins on the spot

After this, the motion constants should look like this:

```java
voltageSensor = opModeEX.hardwareMap.getAll(VoltageSensor.class).iterator().next();

VoltagePerformanceEnforcer translationalYEnforcer = new VoltagePerformanceEnforcer(
    13.031,
    0.9747773604750232,
    1648.569587035565
);

VoltagePerformanceEnforcer translationalXEnforcer = new VoltagePerformanceEnforcer(
    12.987,
    1.769415503675299,
    1050.9146036238537
);

VoltagePerformanceEnforcer translationalAngledEnforcer = new VoltagePerformanceEnforcer(
    12.983,
    1.096447867768282,
    1222.6698612398357
);

VoltagePerformanceEnforcer rotationalEnforcer = new VoltagePerformanceEnforcer(
    13.096,
    0.9797365668388713,
    5.4708398890705805
);

double currentVoltage = voltageSensor.getVoltage();

// replace accelerations
motionConstants = new MecanumMotionConstants(
    translationalYEnforcer.transformVelocity(currentVoltage), // translational y velocity
    translationalXEnforcer.transformVelocity(currentVoltage), // translational x velocity
    translationalAngledEnforcer.transformVelocity(currentVoltage), // translational angled velocity
    rotationalEnforcer.transformVelocity(currentVoltage), // rotational velocity
    1, // translational y acceleration
    1, // translational x acceleration
    1, // translational angled acceleration
    1 // rotational acceleration
);

```

Once you have recorded each of the velocities, you can download them to the robot, and run the acceleration tuners.

Each of the acceleration tuners work the same as the velocity tuners, but will automatically stop once the robot reaches maximum velocity, and will give you an acceleration number.

Which should finally look like this:

```java
voltageSensor = opModeEX.hardwareMap.getAll(VoltageSensor.class).iterator().next();

VoltagePerformanceEnforcer translationalYEnforcer = new VoltagePerformanceEnforcer(
    13.031,
    0.9747773604750232,
    1648.569587035565
);

VoltagePerformanceEnforcer translationalXEnforcer = new VoltagePerformanceEnforcer(
    12.987,
    1.769415503675299,
    1050.9146036238537
);

VoltagePerformanceEnforcer translationalAngledEnforcer = new VoltagePerformanceEnforcer(
    12.983,
    1.096447867768282,
    1222.6698612398357
);

VoltagePerformanceEnforcer rotationalEnforcer = new VoltagePerformanceEnforcer(
    13.096,
    0.9797365668388713,
    5.4708398890705805
);

double currentVoltage = voltageSensor.getVoltage();

// replace accelerations
motionConstants = new MecanumMotionConstants(
    translationalYEnforcer.transformVelocity(currentVoltage), // translational y velocity
    translationalXEnforcer.transformVelocity(currentVoltage), // translational x velocity
    translationalAngledEnforcer.transformVelocity(currentVoltage), // translational angled velocity
    rotationalEnforcer.transformVelocity(currentVoltage), // rotational velocity
    1406.4491188920347, // translational y acceleration
    1670.8888562062925, // translational x acceleration
    1311.448455610628, // translational angled acceleration
    9.943516004740639 // rotational acceleration
);

```

You are now ready to build waves!
