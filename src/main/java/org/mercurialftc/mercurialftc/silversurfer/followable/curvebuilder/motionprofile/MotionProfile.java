package org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.motionprofile;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.FollowableCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.AngleRadians;
import org.mercurialftc.mercurialftc.silversurfer.geometry.ArcLengthHandler;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

import java.util.Arrays;

/**
 * motion profile for splines
 */
public class MotionProfile {
	private double arcSegmentLength;
	private FollowableCurve spline;
	private int plannedPoints;

	public MotionProfile(FollowableCurve spline) {
		this.spline = spline;
	}

	public double getArcSegmentLength() {
		return arcSegmentLength;
	}

	public Followable.Output[] profile() {
		return optimise();
	}

	private Followable.Output[] optimise() {
		double startTime = System.nanoTime() / 1E9; //current relative time in seconds
		double allowableOptimisationTime = 0.2 + (spline.getCurves().length + 1) * 0.1; //seconds

		double optimisationThreshold = 0.01; // todo find value, in seconds, this should be fine

		double[] deltaSteps = new double[spline.getCurves().length + 1];
		Arrays.fill(deltaSteps, 2);
		double[] elongationFactors = new double[spline.getCurves().length + 1];
		Arrays.fill(elongationFactors, 2.5);

		Followable.Output[] bestTrajectory = finaliseProfile();
		while ((System.nanoTime() / 1E9) <= (startTime + allowableOptimisationTime)) {
			for (int i = 0; i < spline.getCurves().length + 1; i++) { //there are one more tangents than there are segments/curves
				Followable.Output[] currentTrajectory;

				double error = Double.POSITIVE_INFINITY;

				while (error > optimisationThreshold) {
					spline.elongateTangents(elongationFactors[i], i);
					currentTrajectory = finaliseProfile();

					double currentTime = currentTrajectory[currentTrajectory.length - 1].getCallbackTime();
					double bestTime = bestTrajectory[bestTrajectory.length - 1].getCallbackTime();

					if (currentTime < bestTime) {
						bestTrajectory = currentTrajectory;
						break;
					}

					error = bestTime - currentTime; // positive is an improvement

					int errorSignum = (int) Math.signum(error);

					// todo idk if this will work or not lmao, prolly super inefficient too (should work now ig??)
					if (errorSignum < 0) {
						elongationFactors[i] -= deltaSteps[i];
						deltaSteps[i] *= 0.5;
					}
					elongationFactors[i] += deltaSteps[i];
				}

			}

		}
		return bestTrajectory;
	}

	private Followable.Output[] finaliseProfile() {
		plannedPoints = divideArcLength();

//		double[] intersects = findIntersects();

		Followable.Output[] outputs = new Followable.Output[plannedPoints];

		ArcLengthHandler arcLengthHandler = spline.getArcLengthHandler();
		double time = 0;
		double previousVelocity = 0;
		double previousRotationalVelocity = 0;
		double previousDeltaT = 0;
		AngleRadians previousEstimatedRotationalPosition = arcLengthHandler.findCurveFromArcLength(0).getCurve().getStartPose().getTheta();

		// handles first case
		outputs[0] = new Followable.Output(
				Vector2D.fromPolar(0, arcLengthHandler.findCurveFromArcLength(0).getResult().getHeading()), // the velocity output
				0,
				0,
				arcLengthHandler.findCurveFromArcLength(0).getCurve().getStartPose(),
				arcLengthHandler.findCurveFromArcLength(0).getCurve().getStartPose()
		);

		for (int i = 1; i < plannedPoints; i++) {

			MecanumMotionConstants motionConstants = spline.getMotionConstantsArray().get(i);
			ArcLengthHandler.ArcLengthRelationship curveFromArcLength = arcLengthHandler.findCurveFromArcLength(i * arcSegmentLength);

			AngleRadians targetRotationalPosition = curveFromArcLength.getCurve().getEndPose().getTheta();
			AngleRadians estimatedRotationalPosition = previousEstimatedRotationalPosition.add(previousRotationalVelocity * previousDeltaT).toAngleRadians();

			double rotationalError = estimatedRotationalPosition.findShortestDistance(targetRotationalPosition); //shortest distance from estimated current position to target position

			double rotationalBreakDistance = (previousRotationalVelocity * previousRotationalVelocity) / (2 * motionConstants.getMaxRotationalAcceleration());

			int rotationalBreakControl = (int) Math.signum(Math.abs(rotationalError) - rotationalBreakDistance);

			double rotationDistance = previousEstimatedRotationalPosition.findShortestDistance(estimatedRotationalPosition); // out of date by one planning point

			// todo should do for now, possibly need to implement some scaling for the acceleration to dampen or smth
			double rotationalVelocity = Math.sqrt((previousRotationalVelocity * previousRotationalVelocity) + 2 * motionConstants.getMaxRotationalAcceleration() * Math.signum(rotationalError) * rotationalBreakControl * rotationDistance); // todo should do for now, possibly need to implement some scaling for the acceleration to dampen or smth
			rotationalVelocity = Math.min(rotationalVelocity, motionConstants.getMaxRotationalVelocity());

			double estimatedTangentialReduction = 1 + (Math.sqrt(2) - 1) / 2 + Math.cos(2 * estimatedRotationalPosition.findShortestDistance(curveFromArcLength.getResult().getHeading())) * ((Math.sqrt(2) - 1) / 2);

			double vMax = motionConstants.getMaxTranslationalVelocity() / estimatedTangentialReduction;

			double vMaxRotation = motionConstants.getMaxRotationalVelocity() / rotationalVelocity;
			// todo make these optional
			// todo add distance to nearest object

			double finalVelocityConstraint = Math.min(vMax, vMaxRotation);

			// ∆t = 2∆s / (v_i + v_{i−1})
			double deltaT = (2 * arcSegmentLength) / (finalVelocityConstraint + previousVelocity);

			time += deltaT;

			outputs[i] = new Followable.Output(
					Vector2D.fromPolar(finalVelocityConstraint, curveFromArcLength.getResult().getHeading()), // the velocity output
					rotationalVelocity,
					time,
					new Pose2D(curveFromArcLength.getResult().getX(), curveFromArcLength.getResult().getY(), estimatedRotationalPosition),
					curveFromArcLength.getCurve().getEndPose()
			);

//			c1 = c;
			previousEstimatedRotationalPosition = estimatedRotationalPosition;
			previousVelocity = finalVelocityConstraint;
			previousRotationalVelocity = rotationalVelocity;
			previousDeltaT = deltaT;
		}

		return outputs;
	}

	/*
	public double[] findIntersects() {
		double[] outputVelocities = new double[plannedPoints];

		ArcLengthHandler arcLengthHandler = spline.getArcLengthHandler();
		ArcLengthHandler.ArcLengthRelationship initialCurve = arcLengthHandler.findCurveFromArcLength(arcLengthHandler.getArcLength());
		double curvatureI = initialCurve.getCurvature();

		// handles first case
		outputVelocities[0] = 0;

		for (int i = plannedPoints - 1; i > 0; i--) {
			ArcLengthHandler.ArcLengthRelationship curveFromArcLength = arcLengthHandler.findCurveFromArcLength(i * arcSegmentLength);
			double curvatureI_1 = curveFromArcLength.getCurvature(); //c_{i-1}

			double threshIntersect = findThreshIntersect(i, curvatureI, curvatureI_1);

			outputVelocities[i] = threshIntersect;

			curvatureI = curvatureI_1;
		}

		return outputVelocities;
	}
	 */

	/**
	 * Sets {@link #arcSegmentLength} to be the new true length of each arc segment.
	 *
	 * @return whole number of segments when the arcLength is divided into approximately 2.5mm lengths.
	 */
	private int divideArcLength() {
		double arcLength = spline.getArcLengthHandler().getBreakpoints()[spline.getArcLengthHandler().getBreakpoints().length - 1];

		double estimate = arcLength / 2.5; // target segment length

		int result = (int) Math.round(estimate);

		arcSegmentLength = arcLength / result;

		return result;
	}

	/*
	private double findThreshIntersect(int i, double curvatureI, double curvatureI_1) {
		double thresh = 0.0; // output

		double maxRotationalAcceleration = spline.getMotionConstantsArray().get(i).getMaxAngularAcceleration();
		double maxTranslationalAcceleration = spline.getMotionConstantsArray().get(i).getMaxTranslationalAcceleration();

		if (curvatureI > 0 && curvatureI_1 >= 0) {
			if (curvatureI > curvatureI_1) {
				thresh = Math.sqrt((2 * arcSegmentLength * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration) * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration)) / ((maxTranslationalAcceleration * (curvatureI + curvatureI_1) + 2 * maxRotationalAcceleration) * (curvatureI - curvatureI_1)));
			} else if (curvatureI < curvatureI_1) {
				double thresh1 = Math.sqrt((8 * curvatureI * maxRotationalAcceleration * arcSegmentLength) / ((curvatureI_1 + curvatureI) * (curvatureI_1 + curvatureI)));
				double tmp1 = Math.sqrt((4 * curvatureI * arcSegmentLength * (curvatureI * maxTranslationalAcceleration + maxRotationalAcceleration)) / ((curvatureI_1 - curvatureI) * (curvatureI_1 - curvatureI)));
				double tmp2 = Math.sqrt((2 * arcSegmentLength * (curvatureI * maxTranslationalAcceleration + maxRotationalAcceleration) * (curvatureI * maxTranslationalAcceleration + maxRotationalAcceleration)) / ((curvatureI_1 - curvatureI) * (2 * maxRotationalAcceleration + (curvatureI_1 + curvatureI) * maxTranslationalAcceleration)));

				double thresh_tmp1 = Math.min(tmp1, tmp2);
				double thresh_tmp2 = Math.min(Math.sqrt((2 * maxRotationalAcceleration * arcSegmentLength) / (curvatureI_1)), Math.sqrt(2 * maxTranslationalAcceleration * arcSegmentLength));
				double thresh_tmp3 = Double.NEGATIVE_INFINITY;

				double tmp = Math.min(((2 * maxRotationalAcceleration * arcSegmentLength) / (curvatureI_1)), ((2 * arcSegmentLength * (curvatureI * maxTranslationalAcceleration - maxRotationalAcceleration) * (curvatureI * maxTranslationalAcceleration - maxRotationalAcceleration)) / ((curvatureI_1 - curvatureI) * (2 * maxRotationalAcceleration - (curvatureI_1 + curvatureI) * maxTranslationalAcceleration))));

				if (tmp > ((-4 * curvatureI * arcSegmentLength * (curvatureI * maxTranslationalAcceleration - maxRotationalAcceleration)) / ((curvatureI_1 - curvatureI) * (curvatureI_1 + curvatureI))) && tmp > (2 * maxTranslationalAcceleration * arcSegmentLength)) {
					thresh_tmp3 = Math.sqrt(tmp);
				}

				thresh = Math.max(Math.max(thresh1, thresh_tmp1), Math.max(thresh_tmp2, thresh_tmp3));
			} else { // ci == ci_1
				thresh = Double.POSITIVE_INFINITY;
			}
		} else if (curvatureI < 0 && curvatureI_1 <= 0) {
			if (curvatureI > curvatureI_1) {
				double thresh1 = Math.sqrt((-8 * curvatureI * maxRotationalAcceleration * arcSegmentLength) / ((curvatureI_1 + curvatureI) * (curvatureI_1 + curvatureI)));
				double tmp1 = Math.sqrt((-4 * curvatureI * arcSegmentLength * (maxRotationalAcceleration - curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 + curvatureI) * (curvatureI_1 - curvatureI)));
				double tmp2 = Math.sqrt((-2 * arcSegmentLength * (maxRotationalAcceleration - curvatureI * maxTranslationalAcceleration) * (maxRotationalAcceleration - curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 - curvatureI) * (2 * maxRotationalAcceleration - (curvatureI_1 + curvatureI) * maxTranslationalAcceleration)));

				double thresh_tmp1 = Math.min(tmp1, tmp2);
				double thresh_tmp2 = Math.min(Math.sqrt((-2 * maxRotationalAcceleration * arcSegmentLength) / (curvatureI_1)), Math.sqrt(2 * maxTranslationalAcceleration * arcSegmentLength));
				double thresh_tmp3 = Double.NEGATIVE_INFINITY;

				double tmp = Math.min(((-2 * maxRotationalAcceleration * arcSegmentLength) / (curvatureI_1)), ((-2 * arcSegmentLength * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration) * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 - curvatureI) * (2 * maxRotationalAcceleration + (curvatureI_1 + curvatureI) * maxTranslationalAcceleration))));

				if (tmp > ((-4 * curvatureI * arcSegmentLength * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 - curvatureI) * (curvatureI_1 + curvatureI))) && tmp > (2 * maxTranslationalAcceleration * arcSegmentLength)) {
					thresh_tmp3 = Math.sqrt(tmp);
				}

				thresh = Math.max(Math.max(thresh1, thresh_tmp1), Math.max(thresh_tmp2, thresh_tmp3));
			} else if (curvatureI < curvatureI_1) {
				thresh = Math.sqrt((-2 * arcSegmentLength * (maxRotationalAcceleration - curvatureI * maxTranslationalAcceleration) * (maxRotationalAcceleration - curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 - curvatureI) * ((curvatureI + curvatureI_1) * maxTranslationalAcceleration - 2 * maxRotationalAcceleration)));
			} else { // ci == ci_1
				thresh = Double.POSITIVE_INFINITY;
			}
		} else if (curvatureI < 0 && curvatureI_1 > 0) {
			double vtwostarpos = Math.sqrt((2 * arcSegmentLength * maxRotationalAcceleration) / (curvatureI_1));
			double precond = Double.POSITIVE_INFINITY;

			if (curvatureI_1 + curvatureI < 0) {
				precond = Math.sqrt((-4 * curvatureI * arcSegmentLength * (curvatureI * maxTranslationalAcceleration - maxRotationalAcceleration)) / ((curvatureI_1 - curvatureI) * (curvatureI_1 + curvatureI)));
			}

			double thresh_tmp = Math.min(precond, Math.sqrt((-2 * arcSegmentLength * (curvatureI * maxTranslationalAcceleration - maxRotationalAcceleration) * (curvatureI * maxTranslationalAcceleration - maxRotationalAcceleration)) / ((curvatureI_1 - curvatureI) * ((curvatureI + curvatureI_1) * maxTranslationalAcceleration - 2 * maxRotationalAcceleration))));
			thresh_tmp = Math.max(thresh_tmp, Math.sqrt(2 * arcSegmentLength * maxTranslationalAcceleration));

			thresh = Math.min(thresh_tmp, vtwostarpos);
		} else if (curvatureI > 0 && curvatureI_1 < 0) {
			double vonestarpos = Math.sqrt(-(2 * arcSegmentLength * maxRotationalAcceleration) / (curvatureI_1));
			double precond = Double.POSITIVE_INFINITY;

			if (curvatureI_1 + curvatureI > 0) {
				precond = Math.sqrt((-4 * curvatureI * arcSegmentLength * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 - curvatureI) * (curvatureI_1 + curvatureI)));
			}

			double thresh_tmp = Math.min(precond, Math.sqrt((-2 * arcSegmentLength * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration) * (maxRotationalAcceleration + curvatureI * maxTranslationalAcceleration)) / ((curvatureI_1 - curvatureI) * ((curvatureI + curvatureI_1) * maxTranslationalAcceleration + 2 * maxRotationalAcceleration))));
			thresh_tmp = Math.max(thresh_tmp, Math.sqrt(2 * arcSegmentLength * maxTranslationalAcceleration));

			thresh = Math.min(thresh_tmp, vonestarpos);
		} else if (curvatureI == 0) {
			if (curvatureI_1 > 0) {
				double vtwohatpos = Math.sqrt((2 * arcSegmentLength * maxRotationalAcceleration) / (curvatureI_1));
				double thresh_tmp = Math.max(Math.sqrt(2 * arcSegmentLength * maxTranslationalAcceleration), Math.sqrt((-2 * arcSegmentLength * maxRotationalAcceleration * maxRotationalAcceleration) / (curvatureI_1 * (curvatureI_1 * maxTranslationalAcceleration - 2 * maxRotationalAcceleration))));
				thresh = Math.min(vtwohatpos, thresh_tmp);
			} else if (curvatureI_1 < 0) {
				double vtwohatpos = Math.sqrt(-(2 * arcSegmentLength * maxRotationalAcceleration) / (curvatureI_1));
				double thresh_tmp = Math.max(Math.sqrt(2 * arcSegmentLength * maxTranslationalAcceleration), Math.sqrt((-2 * arcSegmentLength * maxRotationalAcceleration * maxRotationalAcceleration) / (curvatureI_1 * (curvatureI_1 * maxTranslationalAcceleration + 2 * maxRotationalAcceleration))));
				thresh = Math.min(vtwohatpos, thresh_tmp);
			} else {
				thresh = Double.POSITIVE_INFINITY;
			}
		}

		return thresh;
	}
	 */
}
