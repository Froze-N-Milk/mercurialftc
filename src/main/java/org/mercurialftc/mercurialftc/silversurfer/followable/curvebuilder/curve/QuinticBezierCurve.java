package org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public class QuinticBezierCurve {
	private final Pose2D startPose;
	private final Pose2D endPose;
	private final Vector2D p1, p2, p3, p4;

	public QuinticBezierCurve(@NotNull Pose2D startPose, @NotNull Vector2D ts, @NotNull Vector2D as, @NotNull Vector2D te, @NotNull Vector2D ae, @NotNull Pose2D endPose) {
		this.startPose = startPose;
		this.endPose = endPose;
		Vector2D p0 = new Vector2D(startPose.getX(), startPose.getY());
		Vector2D p5 = new Vector2D(endPose.getX(), endPose.getY());

		this.p1 = new Vector2D(0.2 * ts.getX() + p0.getX(), 0.2 * ts.getY() + p0.getY());
		this.p2 = new Vector2D(0.05 * as.getX() + 2 * p1.getX() - p0.getX(), 0.05 * as.getY() + 2 * p1.getY() - p0.getY());

		this.p4 = new Vector2D(p5.getX() - 0.2 * te.getX(), p5.getY() - 0.2 * te.getY());
		this.p3 = new Vector2D(0.05 * ae.getX() + 2 * p4.getX() - p5.getX(), 0.05 * ae.getY() + 2 * p4.getY() - p5.getY());
	}

	public Pose2D getStartPose() {
		return startPose;
	}

	public Pose2D getEndPose() {
		return endPose;
	}

	/**
	 * @param t the internal position within the curve in the domain [0, 1]
	 * @return the resulting vector at this point in the curve
	 */
	public Vector2D result(double t) {
		double[] ts = new double[5];
		ts[0] = t;
		for (int i = 1; i < ts.length; i++) {
			ts[i] = ts[i - 1] * t;
		}

		double[] xComponents = new double[]{
				startPose.getX(),
				p1.getX(),
				p2.getX(),
				p3.getX(),
				p4.getX(),
				endPose.getX()
		};
		double[] yComponents = new double[]{
				startPose.getY(),
				p1.getY(),
				p2.getY(),
				p3.getY(),
				p4.getY(),
				endPose.getY()
		};

		double x = xComponents[0];
		double y = yComponents[0];

		// −5P0 + 5P1
		x += ts[0] * (-5 * xComponents[0] + 5 * xComponents[1]);
		y += ts[0] * (-5 * yComponents[0] + 5 * yComponents[1]);

		// 10P0 − 20P1 + 10P2
		x += ts[1] * (10 * xComponents[0] - 20 * xComponents[1] + 10 * xComponents[2]);
		y += ts[1] * (10 * yComponents[0] - 20 * yComponents[1] + 10 * yComponents[2]);

		// −10P0 + 30P1 − 30P2 + 10P3
		x += ts[2] * (-10 * xComponents[0] + 30 * xComponents[1] - 30 * xComponents[2] + 10 * xComponents[3]);
		y += ts[2] * (-10 * yComponents[0] + 30 * yComponents[1] - 30 * yComponents[2] + 10 * yComponents[3]);

		// 5P0 − 20P1 + 30P2 − 20P3 + 5P4
		x += ts[3] * (5 * xComponents[0] - 20 * xComponents[1] + 30 * xComponents[2] - 20 * xComponents[3] + 5 * xComponents[4]);
		y += ts[3] * (5 * yComponents[0] - 20 * yComponents[1] + 30 * yComponents[2] - 20 * yComponents[3] + 5 * yComponents[4]);

		// −P0 + 5P1 − 10P2 + 10P3 − 5P4 + P5
		x += ts[4] * (-xComponents[0] + 5 * xComponents[1] - 10 * xComponents[2] + 10 * xComponents[3] - 5 * xComponents[4] + xComponents[5]);
		y += ts[4] * (-yComponents[0] + 5 * yComponents[1] - 10 * yComponents[2] + 10 * yComponents[3] - 5 * yComponents[4] + yComponents[5]);

		return new Vector2D(x, y);
	}

	public Vector2D firstDerivative(double t) {
		double[] ts = new double[4];
		ts[0] = t;
		for (int i = 1; i < ts.length; i++) {
			ts[i] = ts[i - 1] * t;
		}

		double[] xComponents = new double[]{
				startPose.getX(),
				p1.getX(),
				p2.getX(),
				p3.getX(),
				p4.getX(),
				endPose.getX()
		};
		double[] yComponents = new double[]{
				startPose.getY(),
				p1.getY(),
				p2.getY(),
				p3.getY(),
				p4.getY(),
				endPose.getY()
		};

		// −5P0 + 5P1
		double x = (-5 * xComponents[0] + 5 * xComponents[1]);
		double y = (-5 * yComponents[0] + 5 * yComponents[1]);

		// 10P0 − 20P1 + 10P2
		x += 2 * ts[0] * (10 * xComponents[0] - 20 * xComponents[1] + 10 * xComponents[2]);
		y += 2 * ts[0] * (10 * yComponents[0] - 20 * yComponents[1] + 10 * yComponents[2]);

		// −10P0 + 30P1 − 30P2 + 10P3
		x += 3 * ts[1] * (-10 * xComponents[0] + 30 * xComponents[1] - 30 * xComponents[2] + 10 * xComponents[3]);
		y += 3 * ts[1] * (-10 * yComponents[0] + 30 * yComponents[1] - 30 * yComponents[2] + 10 * yComponents[3]);

		// 5P0 − 20P1 + 30P2 − 20P3 + 5P4
		x += 4 * ts[2] * (5 * xComponents[0] - 20 * xComponents[1] + 30 * xComponents[2] - 20 * xComponents[3] + 5 * xComponents[4]);
		y += 4 * ts[2] * (5 * yComponents[0] - 20 * yComponents[1] + 30 * yComponents[2] - 20 * yComponents[3] + 5 * yComponents[4]);

		// −P0 + 5P1 − 10P2 + 10P3 − 5P4 + P5
		x += 5 * ts[3] * (-xComponents[0] + 5 * xComponents[1] - 10 * xComponents[2] + 10 * xComponents[3] - 5 * xComponents[4] + xComponents[5]);
		y += 5 * ts[3] * (-yComponents[0] + 5 * yComponents[1] - 10 * yComponents[2] + 10 * yComponents[3] - 5 * yComponents[4] + yComponents[5]);

		return new Vector2D(x, y);
	}

	public Vector2D secondDerivative(double t) {
		double[] ts = new double[3];
		ts[0] = t;
		for (int i = 1; i < ts.length; i++) {
			ts[i] = ts[i - 1] * t;
		}

		double[] xComponents = new double[]{
				startPose.getX(),
				p1.getX(),
				p2.getX(),
				p3.getX(),
				p4.getX(),
				endPose.getX()
		};
		double[] yComponents = new double[]{
				startPose.getY(),
				p1.getY(),
				p2.getY(),
				p3.getY(),
				p4.getY(),
				endPose.getY()
		};

		// 10P0 − 20P1 + 10P2
		double x = 2 * (10 * xComponents[0] - 20 * xComponents[1] + 10 * xComponents[2]);
		double y = 2 * (10 * yComponents[0] - 20 * yComponents[1] + 10 * yComponents[2]);

		// −10P0 + 30P1 − 30P2 + 10P3
		x += 6 * ts[0] * (-10 * xComponents[0] + 30 * xComponents[1] - 30 * xComponents[2] + 10 * xComponents[3]);
		y += 6 * ts[0] * (-10 * yComponents[0] + 30 * yComponents[1] - 30 * yComponents[2] + 10 * yComponents[3]);

		// 5P0 − 20P1 + 30P2 − 20P3 + 5P4
		x += 12 * ts[1] * (5 * xComponents[0] - 20 * xComponents[1] + 30 * xComponents[2] - 20 * xComponents[3] + 5 * xComponents[4]);
		y += 12 * ts[1] * (5 * yComponents[0] - 20 * yComponents[1] + 30 * yComponents[2] - 20 * yComponents[3] + 5 * yComponents[4]);

		// −P0 + 5P1 − 10P2 + 10P3 − 5P4 + P5
		x += 20 * ts[2] * (-xComponents[0] + 5 * xComponents[1] - 10 * xComponents[2] + 10 * xComponents[3] - 5 * xComponents[4] + xComponents[5]);
		y += 20 * ts[2] * (-yComponents[0] + 5 * yComponents[1] - 10 * yComponents[2] + 10 * yComponents[3] - 5 * yComponents[4] + yComponents[5]);

		return new Vector2D(x, y);
	}

	/**
	 * @param t the internal position within the curve in the domain [0, 1]
	 * @return the resulting curvature at this point in the curve
	 */
	public double findCurvature(double t) {
		Vector2D firstDerivative = firstDerivative(t);
		Vector2D secondDerivative = secondDerivative(t);

		double denominator = firstDerivative.getMagnitude();

		return (firstDerivative.getX() * secondDerivative.getY() - firstDerivative.getY() * secondDerivative.getX()) / (denominator * denominator * denominator);
	}
}
