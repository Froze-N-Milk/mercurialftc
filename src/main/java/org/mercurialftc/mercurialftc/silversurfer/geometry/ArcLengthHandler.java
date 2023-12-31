package org.mercurialftc.mercurialftc.silversurfer.geometry;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.FollowableCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve.QuinticBezierCurve;

public class ArcLengthHandler {
	private final double[] breakpoints;
	private final double[] arcLengths;
	private final FollowableCurve followableCurve;

	public ArcLengthHandler(@NotNull FollowableCurve followableCurve) {
		this.followableCurve = followableCurve;
		this.breakpoints = new double[followableCurve.getCurves().length + 1];
		breakpoints[0] = 0;
		this.arcLengths = new double[followableCurve.getCurves().length];

		findArcLengths();
	}

	public double[] getBreakpoints() {
		return breakpoints;
	}

	public double getArcLength() {
		return breakpoints[breakpoints.length - 1];
	}

	public double[] getArcLengths() {
		return arcLengths;
	}

	private void findArcLengths() {
		QuinticBezierCurve[] curves = followableCurve.getCurves();

		double previousArcLength = 0.0;
		double previousVelocity = 0.0;

		for (int i = 0; i < curves.length; i++) {
			double arcLength = findArcLength(previousVelocity, curves[i]);

			arcLengths[i] = arcLength;
			breakpoints[i + 1] = previousArcLength + arcLength;
			previousArcLength = breakpoints[i + 1];

			previousVelocity = curves[i].firstDerivative(1).getMagnitude();
		}
	}

	private double findArcLength(double previousVelocity, QuinticBezierCurve curve) {
		double result = 0.0;

		double depth = 1.0E-5;

		for (double i = depth; i <= 1; i += depth) {
			Vector2D firstDerivative = curve.firstDerivative(i); // velocity?
			Vector2D secondDerivative = curve.secondDerivative(i); // acceleration?

			result += (0.5 * secondDerivative.getMagnitude() * depth * depth) + previousVelocity * depth;

			previousVelocity = firstDerivative.getMagnitude();
		}

		return result;
	}

	public ArcLengthRelationship findCurveFromArcLength(double arcLength) {
		QuinticBezierCurve[] curves = followableCurve.getCurves();

		for (int i = 0; i < breakpoints.length - 2; i++) {
			if (arcLength >= breakpoints[i] && arcLength < breakpoints[i + 1]) {
				return new ArcLengthRelationship(curves[i], ((arcLength - breakpoints[i]) / arcLengths[i]), i);
			}
		}
		arcLength = Math.min(arcLength, breakpoints[breakpoints.length - 1]); //ensures that the result is within bounds

		return new ArcLengthRelationship(curves[curves.length - 1], ((arcLength - breakpoints[breakpoints.length - 2]) / arcLengths[arcLengths.length - 1]), curves.length - 1);
	}

	public static class ArcLengthRelationship {
		private final QuinticBezierCurve curve;
		private final double t;
		private final int curveIndex;

		public ArcLengthRelationship(QuinticBezierCurve curve, double t, int curveIndex) {
			this.curve = curve;
			this.t = t;
			this.curveIndex = curveIndex;
		}

		public QuinticBezierCurve getCurve() {
			return curve;
		}

		public double getT() {
			return t;
		}

		public Vector2D getResult() {
			return curve.result(t);
		}

		public Vector2D getFirstDerivative() {
			return curve.firstDerivative(t);
		}

		public Vector2D getSecondDerivative() {
			return curve.secondDerivative(t);
		}

		public double getCurvature() {
			return curve.findCurvature(t);
		}

		public int getCurveIndex() {
			return curveIndex;
		}
	}
}
