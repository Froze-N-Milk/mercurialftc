# Caching Hardware Devices

Mercurial offers drop in place configurable caching hardware wrappers that prevent you from performing hardware writes too often to motors and servos.

This means its super easy to use these and not change anything else about your code, for example:

```java
DcMotorEx motor = hardwaremap.get(DcMotorEx.class, "motor");
```

Becomes:

```java
DcMotorEx motor = new CachingDcMotorEX(hardwaremap.get(DcMotorEx.class, "motor"));
```

and performs the exact same as a motor.

All caching hardware devices:

```java
DcMotorEx motorex = new CachingDcMotorEX(hardwaremap.get(DcMotorEx.class, "motorex"));

DcMotor motor = new CachingDcMotor(hardwaremap.get(DcMotor.class, "motor"));

DcMotorSimple motorsimple = new CachingDcMotorSimple(hardwaremap.get(DcMotorSimple.class, "motorsimple"));

Servo servo = new CachingServo(hardwaremap.get(Servo.class, "servo"));

CRServo crservo = new CachingCRServo(hardwaremap.get(Servo.class, "crservo"));
```

Some of these offer additional configuration parameters to specify change write thresholds.
