package org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder;

import org.mercurialftc.mercurialftc.scheduler.commands.Command;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.FollowableBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve.CurveSegment;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve.QuinticBezierCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.angle.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.ArrayList;

public class CurveBuilder extends FollowableBuilder {
	private final ArrayList<CurveSegment> segments;
	private final ArrayList<MarkerBuilder> unfinishedMarkers;
	private final MecanumMotionConstants absoluteMotionConstants;
	private Vector2D[] tangents;
	private Vector2D[] outputTangents;
	private Vector2D[] secondDerivatives;

	public CurveBuilder(MecanumMotionConstants motionConstants, MecanumMotionConstants absoluteMotionConstants) {
		super(motionConstants);
		this.segments = new ArrayList<>();
		this.unfinishedMarkers = new ArrayList<>();
		this.absoluteMotionConstants = absoluteMotionConstants;
	}

	public void addSegment(Pose2D previousPose, Pose2D destinationPose) {
		segments.add(new CurveSegment(previousPose, destinationPose));
	}

	@Override
	public void addOffsetCommandMarker(double offset, Marker.MarkerType markerType, Command markerReached) {
		unfinishedMarkers.add(
				new MarkerBuilder(markerReached, markerType, offset, segments.size() - 1)
		);
	}

	/**
	 * generates the tangents at each way point which are considered the first
	 */
	private void firstHeuristic() {
		tangents = new Vector2D[segments.size() + 1]; // there needs to be a tangent for each point, there are 1 more points than there are segments
		Vector2D tempVector = segments.get(0).getTranslationalVector();
		tangents[0] = Vector2D.fromPolar(tempVector.getMagnitude(), tempVector.getHeading().getRadians());

		for (int i = 1; i < tangents.length - 1; i++) {
			Vector2D BA = segments.get(i - 1).getInverseTranslationalVector();
			Vector2D BC = segments.get(i).getTranslationalVector();

			AngleRadians theta = new AngleRadians(BC.getHeading().getRadians() - BA.getHeading().getRadians());
			theta.setTheta(theta.getRadians() / 2);
			theta = theta.add(BA.getHeading()).toAngleRadians();
			theta = theta.add((Math.PI / 2)).toAngleRadians();

			double magnitude = Math.min(BA.getMagnitude(), BC.getMagnitude());

			tangents[i] = Vector2D.fromPolar(magnitude, theta);
		}

		tempVector = segments.get(segments.size() - 1).getTranslationalVector();
		tangents[tangents.length - 1] = Vector2D.fromPolar(tempVector.getMagnitude(), tempVector.getHeading().getRadians());
	}

	private void secondHeuristic() {
		secondDerivatives = new Vector2D[segments.size() + 1]; // there needs to be a second derivative for each point, there are 1 more points than there are segments

		//do the first one
		// −6A − 4tA − 2tB + 6B
		Pose2D tempA = segments.get(0).getStartPose();
		Pose2D tempB = segments.get(0).getEndPose();

		Vector2D temp_tA = outputTangents[0];
		Vector2D temp_tB = outputTangents[1];

		double tempResultX = (-6 * tempA.getX() - 4 * temp_tA.getX() - 2 * temp_tB.getX() + 6 * tempB.getX());
		double tempResultY = (-6 * tempA.getY() - 4 * temp_tA.getY() - 2 * temp_tB.getY() + 6 * tempB.getY());

		secondDerivatives[0] = new Vector2D(tempResultX, tempResultY);

		for (int i = 1; i < secondDerivatives.length - 1; i++) {
			// α (6A + 2tA + 4tB − 6B) + β (−6B − 4tB − 2tC + 6C)

			double ABDistance = segments.get(i - 1).getTranslationalVector().getMagnitude();
			double BCDistance = segments.get(i).getTranslationalVector().getMagnitude();
			double distanceSum = ABDistance + BCDistance;

			double alpha = BCDistance / distanceSum;
			double beta = ABDistance / distanceSum;

			Pose2D A = segments.get(i - 1).getStartPose();
			Pose2D B = segments.get(i).getStartPose();
			Pose2D C = segments.get(i).getEndPose();

			Vector2D tA = outputTangents[i - 1];
			Vector2D tB = outputTangents[i];
			Vector2D tC = outputTangents[i + 1];

			double resultX = alpha * (6 * A.getX() + 2 * tA.getX() + 4 * tB.getX() - 6 * B.getX()) + beta * (-6 * B.getX() - 4 * tB.getX() - 2 * tC.getX() + 6 * C.getX());
			double resultY = alpha * (6 * A.getY() + 2 * tA.getY() + 4 * tB.getY() - 6 * B.getY()) + beta * (-6 * B.getY() - 4 * tB.getY() - 2 * tC.getY() + 6 * C.getY());

			secondDerivatives[i] = new Vector2D(resultX, resultY);
		}

		// do the last one
		// 6A + 2tA + 4tB − 6B

		tempA = segments.get(segments.size() - 1).getStartPose();
		tempB = segments.get(segments.size() - 1).getEndPose();

		temp_tA = outputTangents[tangents.length - 2];
		temp_tB = outputTangents[tangents.length - 1];

		tempResultX = (6 * tempA.getX() + 2 * temp_tA.getX() + 4 * temp_tB.getX() - 6 * tempB.getX());
		tempResultY = (6 * tempA.getY() + 2 * temp_tA.getY() + 4 * temp_tB.getY() - 6 * tempB.getY());

		secondDerivatives[secondDerivatives.length - 1] = new Vector2D(tempResultX, tempResultY);
	}

	public Followable build() {
		firstHeuristic();

		outputTangents = new Vector2D[tangents.length];
		for (int i = 0; i < tangents.length; i++) {
			elongateTangents(0.25, i);
		}
		secondHeuristic();

		return new FollowableCurve(
				this,
				getMotionConstantsArray(),
				unfinishedMarkers,
				absoluteMotionConstants
		);
	}

	public QuinticBezierCurve[] getResult() {
//		firstHeuristic();
		secondHeuristic();

		QuinticBezierCurve[] result = new QuinticBezierCurve[segments.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new QuinticBezierCurve(
					segments.get(i).getStartPose(),
					outputTangents[i],
					secondDerivatives[i],
					outputTangents[i + 1],
					secondDerivatives[i + 1],
					segments.get(i).getEndPose()
			);
		}
		return result;
	}

	public void elongateTangents(double elongationFactor, int index) {
		outputTangents[index] = tangents[index].scalarMultiply(elongationFactor);
	}
}
