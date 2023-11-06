# Angle

Angles take the form of either AngleDegrees or AngleRadians.

Each angle form its type internally, and they are fully interoperable.

You can call `.toAngleDegrees()` or `.toAngleRandians()` to convert an angle to one of these.

The Angle class provides an absolute angle, which wraps at 360° or 2π

The methods that return a scalar value will return a value in radians if the subject of the method was an AngleRadians, and degrees if the subject was an AngleDegrees.
