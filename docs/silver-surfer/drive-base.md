# Drive Base

Drive base control in MercurialFTC takes a bit of a different approach.

Control systems are made up of stacks of processors, with the lowest member sending controls to the actual motors.

For mecanum drives this looks like this:

```java
MecanumFollower mecanumFollower = new MecanumFollower(
    motionConstants,
    tracker,
    fl, bl, br, fr
);
// in the context of the mecanum drive base class, this sets some fields that have getters available

this.arbFollower = mecanumFollower;
this.waveFollower = mecanumFollower;
```

The MecanumFollower class is both a WaveFollower and an ArbFollower which mean it can be used to control the robot by either following a path or by following arbitrary inputs

## ArbFollowers

Arb followers have the method `.follow(transltionVector, rotationalVelocity, loopTime)` which takes in a vector with a maximum magnitude of 1, a rotational velocity in the domain \[-1, 1] where positive rotation is anti-clockwise, and loop time (the change in time from one loop to another).

These are perfect for typical driver inputs, as seen in the mecanum drive base default command:

```java
@Override
public void defaultCommandExecute() {
    double currentTime = opModeEX.getElapsedTime().seconds();
    
    Vector2D vector2D = new Vector2D(x.getAsDouble(), y.getAsDouble()).rotate(alliance.getRotationAngle());
    
    arbFollower.follow(
        vector2D,
        t.getAsDouble(),
        currentTime - previousTime
    );
    
    previousTime = currentTime;
}
```

Where x, y, and t are the [#domainsuppliers](../command-based/binding/gamepadex.md#domainsuppliers "mention") used for the x, y and theta (rotation) components of the driver inputs respectively.

## WaveFollowers

Wave followers take in an Output from a Wave, and eventually feed down to an ArbFollower.

```java
public Command followWave(Wave wave) {
    return new LambdaCommand()
        .setRequirements(this)
        .setInit(() -> {
            waveFollower.setWave(wave);
            waveTimer.reset();
        })
        .setExecute(() -> {
            waveFollower.update(waveTimer.seconds());
        })
        .setFinish(waveFollower::isFinished)
        .setInterruptible(true);
}
```

While Wave followers do have the method `followOutput(output)` it shouldn't be called by us, instead `.update(timeSinceStartOfWave)` ensures that the wave gets updated and that all instructions get brought up to date.

## Followers in MercurialFTC

We have already looked at mecanumFollower, which is good for teleop driving as a field-centric drive controller, and can interpret wave outputs, but isn't very good at doing so, as it doesn't correct for error, which is sure to happen.

MercurialFTC offers two more follower wrappers to add additional functionality to your follower stack:

### ObstacleAvoidantFollower

```java
arbFollower = new ObstacleAvoidantFollower(
    mecanumFollower,
    mecanumFollower,
    motionConstants,
    tracker,
    obstacleMap
);
```

This is a generic follower that adjusts for avoiding obstacles included in the obstacle map

### GVFWaveFollower

```java
waveFollower = new GVFWaveFollower(
    (WaveFollower) arbFollower, // we know its a wave follower too, no harm in this
    motionConstants,
    tracker
);
```

This wraps over a lower stacked wave follower to correct for error in the wave following process.

## Final Stack

While testing, its best to not use the obstacle avoidant follower in the stack for teleop, as it requires an accurate starting position, and it will interfere with the tuning process.

The motion constants are solely for wave building and following, so for the moment, they can be set like so:

```java
motionConstants = new MecanumMotionConstants(
    1, // translational y velocity
    1, // translational x velocity
    1, // translational angled velocity
    1, // rotational velocity
    1, // translational y acceleration
    1, // translational x acceleration
    1, // translational angled acceleration
    1 // rotational acceleration
);
```

These values will be found after the tracker is tuned.

Now, the "Tracker Test" OpMode will work, and the robot will drive.
