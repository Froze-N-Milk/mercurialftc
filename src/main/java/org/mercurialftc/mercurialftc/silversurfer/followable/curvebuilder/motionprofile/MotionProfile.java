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
	private final FollowableCurve spline;
	private double arcSegmentLength;
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

		// handles first case
		outputs[0] = new Followable.Output(
				Vector2D.fromPolar(0, arcLengthHandler.findCurveFromArcLength(0).getFirstDerivative().getHeading()), // the velocity output
				0,
				0,
				arcLengthHandler.findCurveFromArcLength(0).getCurve().getStartPose(),
				arcLengthHandler.findCurveFromArcLength(0).getCurve().getStartPose()
		);

		double previousVelocity = 0;

		for (int i = 1; i < plannedPoints; i++) {
			ArcLengthHandler.ArcLengthRelationship curveFromArcLength = arcLengthHandler.findCurveFromArcLength(i * arcSegmentLength);

			MecanumMotionConstants motionConstants = spline.getMotionConstantsArray().get(curveFromArcLength.getCurveIndex());

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = motionConstants.makeDirectionOfTravelLimiter(curveFromArcLength.getFirstDerivative().getHeading());

			double vMax = directionOfTravelLimiter.getVelocity();

			double vMaxAccelerationLimited = Math.sqrt(previousVelocity * previousVelocity + 2 * directionOfTravelLimiter.getAcceleration() * arcSegmentLength);

//			// todo add distance to nearest object

			double finalVelocityConstraint = Math.min(vMaxAccelerationLimited, vMax);

			outputs[i] = new Followable.Output(
					Vector2D.fromPolar(finalVelocityConstraint, curveFromArcLength.getFirstDerivative().getHeading()), // the velocity output
					0,
					0,
					new Pose2D(curveFromArcLength.getResult().getX(), curveFromArcLength.getResult().getY(), 0),
					curveFromArcLength.getCurve().getEndPose()
			);

			previousVelocity = finalVelocityConstraint;
		}

//		do a backward pass for accelerational constraints

//		set the final output to be back at 0
		outputs[outputs.length - 1] = new Followable.Output(
				Vector2D.fromPolar(0, arcLengthHandler.findCurveFromArcLength(arcLengthHandler.getArcLength()).getFirstDerivative().getHeading()),
				0,
				0,
				arcLengthHandler.findCurveFromArcLength(arcLengthHandler.getArcLength()).getCurve().getEndPose(),
				arcLengthHandler.findCurveFromArcLength(arcLengthHandler.getArcLength()).getCurve().getEndPose()
		);

		previousVelocity = 0;

		for (int i = plannedPoints - 2; i >= 0; i--) {
			ArcLengthHandler.ArcLengthRelationship curveFromArcLength = arcLengthHandler.findCurveFromArcLength(i * arcSegmentLength);

			MecanumMotionConstants motionConstants = spline.getMotionConstantsArray().get(curveFromArcLength.getCurveIndex());

			Vector2D translationVector = outputs[i].getTranslationVector();
			double translationalVelocity = translationVector.getMagnitude();

			MecanumMotionConstants.DirectionOfTravelLimiter directionOfTravelLimiter = motionConstants.makeDirectionOfTravelLimiter(curveFromArcLength.getFirstDerivative().getHeading());

			double vMaxAccelerationLimited = Math.sqrt(previousVelocity * previousVelocity + 2 * directionOfTravelLimiter.getAcceleration() * arcSegmentLength);

			double finalVelocityConstraint = Math.min(translationalVelocity, vMaxAccelerationLimited);

			outputs[i] = new Followable.Output(
					Vector2D.fromPolar(finalVelocityConstraint, translationVector.getHeading()),
					outputs[i].getRotationalVelocity(),
					outputs[i].getCallbackTime(),
					outputs[i].getPosition(),
					outputs[i].getDestination()
			);

			previousVelocity = finalVelocityConstraint;
		}

		// forward pass to calculate times

		previousVelocity = outputs[0].getTranslationVector().getMagnitude();
		double time = 0;

		for (int i = 1; i < plannedPoints; i++) {
			double velocity = outputs[i].getTranslationVector().getMagnitude();

			// ∆t = 2∆s / (v_i + v_{i−1})
			double deltaT = (2 * arcSegmentLength) / (velocity + previousVelocity);

			time += deltaT;

			outputs[i] = new Followable.Output(
					outputs[i].getTranslationVector(),
					outputs[i].getRotationalVelocity(),
					time,
					outputs[i].getPosition(),
					outputs[i].getDestination()
			);

			previousVelocity = velocity;
		}

		// forward pass rotation

		double previousRotationalVelocity = 0;
		AngleRadians previousEstimatedRotationalPosition = arcLengthHandler.findCurveFromArcLength(0).getCurve().getStartPose().getTheta();

		for (int i = 1; i < plannedPoints; i++) {
			ArcLengthHandler.ArcLengthRelationship curveFromArcLength = arcLengthHandler.findCurveFromArcLength(i * arcSegmentLength);
			MecanumMotionConstants motionConstants = spline.getMotionConstantsArray().get(curveFromArcLength.getCurveIndex());

			double deltaT = (outputs[i].getCallbackTime() - outputs[i - 1].getCallbackTime());

			AngleRadians targetRotationalPosition = curveFromArcLength.getCurve().getEndPose().getTheta();

			double rotationalError = previousEstimatedRotationalPosition.findShortestDistance(targetRotationalPosition); //shortest distance from estimated current position to target position

			double rotationalBreakDistance = deltaT * Math.abs(previousRotationalVelocity) + (previousRotationalVelocity * previousRotationalVelocity) / (2 * motionConstants.getMaxRotationalAcceleration());

			int rotationalBreakControl = (int) (Math.signum(Math.abs(rotationalError) - rotationalBreakDistance)); // if negative, we should be slowing down

			double rotationalVelocity = previousRotationalVelocity + (deltaT * motionConstants.getMaxRotationalAcceleration() * Math.signum(rotationalError));

			int velocitySignum = (int) Math.signum(rotationalVelocity);

			double maxRotationalVelocityBreakLimited = Math.abs(Math.abs(previousRotationalVelocity) + (deltaT * motionConstants.getMaxRotationalAcceleration() * rotationalBreakControl));
			double maxRotationalVelocityTranslationLimited = motionConstants.getMaxRotationalVelocity() * (outputs[i].getTranslationVector().getMagnitude() / motionConstants.getMaxTranslationalYVelocity());
			double finalRotationalVelocityConstraint = Math.min(maxRotationalVelocityTranslationLimited, maxRotationalVelocityBreakLimited);
			finalRotationalVelocityConstraint = Math.min(finalRotationalVelocityConstraint, Math.abs(rotationalVelocity)) * velocitySignum;


			previousRotationalVelocity = finalRotationalVelocityConstraint;
			previousEstimatedRotationalPosition = previousEstimatedRotationalPosition.add(finalRotationalVelocityConstraint * deltaT + 0.5 * (previousRotationalVelocity - finalRotationalVelocityConstraint) * deltaT * 2).toAngleRadians();

			outputs[i] = new Followable.Output(
					outputs[i].getTranslationVector(),
					finalRotationalVelocityConstraint,
					outputs[i].getCallbackTime(),
					new Pose2D(outputs[i].getPosition().getX(), outputs[i].getPosition().getY(), previousEstimatedRotationalPosition),
					outputs[i].getDestination()
			);
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
