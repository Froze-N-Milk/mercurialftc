# What is Silver Surfer?

Silver Surfer is MercurialFTC's path building and following library, currently built only for mecanum drive bases.

Silver Surfer consists of a few disconnected components:

* A position tracker (using deadwheels)
* A drive base controller
* A path generator

Silver surfer is fast and easy to tune and use, but lacks the tooling of road runner.

Silver Surfer relies much more on measured constants to generate paths than road runner does, and also offers easy to use build time and real time obstacle avoidance.

These instructions assume that you are somewhat familiar with tuning tracking in road runner.

## Terminology

Wave | Path/Trajectory (gotta have some theming)

WaveBuilder | Path/Trajectory Builder (same reason as above)

## References

Tuners designed to use the following mecanum drive base are found at the tuners directory of the sample repository ([https://github.com/Froze-N-Milk/mercurialftcsample/tree/testing/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/mercurialftc/examples/drive](https://github.com/Froze-N-Milk/mercurialftcsample/tree/testing/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/mercurialftc/examples/drive)).

It is recommended that you make copies of the drive directory in your project, and then edit as you see fit.

An example of a mecanum drive base tuned to do path following is available. Snippets of this are used throughout this guide.

{% @github-files/github-code-block url="https://github.com/Froze-N-Milk/mercurialftcsample/blob/testing/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/mercurialftc/examples/drive/MecanumDriveBase.java" fullWidth="true" %}
