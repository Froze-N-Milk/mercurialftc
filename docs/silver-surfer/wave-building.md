# Wave Building

The methods on WaveBuilder are fairly obvious to use, so this wont cover how to use them, just some not so obvious behaviour.

Most variables passed to the WaveBuilder constructor should be retrieved from the MecanumDriveBase.

```java
WaveBuilder waveBuilder = new WaveBuilder(
    startPose,
    Units.MILLIMETER, // the unit type you wish to use for your translations
    mecanumDriveBase.getMotionConstants(),
    mecanumDriveBase.getObstacleMap()
);
```

It is a good idea to write your translation relative to the size of a field tile.

## Methods:

```java
Wave wave = waveBuilder
    .scaleTranslationVelocity(1)
    .scaleTranslationAcceleration(1)
    .scaleRotationAcceleration(1)
    .scaleRotationVelocity(1)
    .splineTo(X, Y, new AngleDegrees(0))
    .lineTo(X, Y, new AngleDegrees(0))
    .turnTo(new AngleDegrees(0))
    .turn(new AngleDegrees(60))
    .waitFor(0.5)
    .addOffsetActionMarker(0, // offsets when this command gets queued, relative to the end of the instruction before 
        new LambdaCommand()
    )
    .build(); // gives us the wave
```

## Turning During Translation

Silver Surfer currently constructs both of its translations, splines and lines, with the priority on moving as fast as possible, and de-prioritises turning, which means that at the end of a translation the robot may not be at the angle you specified.

You can guarantee that it is by adding a `.turnTo(angle)` after the motion.

## Wave Concatenation

Waves can be concatenated together:

```java
Wave result = wave1.concat(wave2);
```

This is non-mutating, which means:

```java
wave1.concat(wave2);
```

Does not change the contents of wave1 or wave2.

This operation is good for pre-building all the waves in `init()` and then joining them together once a decision needs to be made, i.e., observing the position of the prop for the CenterStage season.

## Full Example:

{% @github-files/github-code-block url="https://github.com/Froze-N-Milk/mercurialftcsample/blob/testing/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/mercurialftc/examples/drive/DemoWaveFollowing.java" fullWidth="true" %}
