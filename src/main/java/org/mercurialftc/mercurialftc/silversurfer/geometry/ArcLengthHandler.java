package org.mercurialftc.mercurialftc.silversurfer.geometry;

import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.FollowableCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve.QuinticBezierCurve;

public class ArcLengthHandler {
	public double[] getBreakpoints() {
		return breakpoints;
	}

	public double getArcLength() {
		return breakpoints[breakpoints.length - 1];
	}

	public double[] getArcLengths() {
		return arcLengths;
	}

	private final double[] breakpoints;
	private final double[] arcLengths;

	private final FollowableCurve followableCurve;

	public ArcLengthHandler(FollowableCurve followableCurve) {
		this.followableCurve = followableCurve;
		this.breakpoints = new double[followableCurve.getCurves().length];
		this.arcLengths = new double[followableCurve.getCurves().length];

		findArcLengths();
	}

	private void findArcLengths() {
		QuinticBezierCurve[] curves = followableCurve.getCurves();

		double previousArcLength = 0.0;
		double previousVelocity = 0.0;

		for (int i = 0; i < curves.length; i++) {
			double arcLength = findArcLength(previousVelocity, curves[i]);

			arcLengths[i] = arcLength;
			breakpoints[i] = previousArcLength + arcLength;
			previousArcLength = breakpoints[i];

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

		for (int i = 0; i < breakpoints.length - 1; i++) {
			if (arcLength >= breakpoints[i] && arcLength < breakpoints[i + 1]) {
				return new ArcLengthRelationship(curves[i], ((arcLength - breakpoints[i]) / arcLengths[i]), i);
			}
		}
		arcLength = Math.max(arcLength, breakpoints[breakpoints.length - 1]); //ensures that the result is within bounds

		return new ArcLengthRelationship(curves[curves.length - 1], ((arcLength - breakpoints[breakpoints.length - 1]) / arcLengths[arcLengths.length - 1]), curves.length - 1);
	}

	public static class ArcLengthRelationship {
		public QuinticBezierCurve getCurve() {
			return curve;
		}

		public double getT() {
			return t;
		}

		public Vector2D getResult() {
			return curve.result(t);
		}

		public double getCurvature() {
			return curve.findCurvature(t);
		}

		public int getCurveIndex() {
			return curveIndex;
		}

		private final QuinticBezierCurve curve;
		private final double t;
		private final int curveIndex;

		public ArcLengthRelationship(QuinticBezierCurve curve, double t, int curveIndex) {
			this.curve = curve;
			this.t = t;
			this.curveIndex = curveIndex;
		}
	}
}
