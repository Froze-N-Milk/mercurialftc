package org.mercurialftc.mercurialftc.util.controlloop.ramsete;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.util.controlloop.motionprofile.MotionProfile;

public class RamseteController {
	private final RamseteConstraints ramseteConstraints;
	private Pose2D targetPose;
	private double xError, yError;
	
	private double v, w;
	
	public double getV() {
		return v;
	}
	
	public double getW() {
		return w;
	}
	
	public RamseteController(RamseteConstraints ramseteConstraints) {
		this.ramseteConstraints = ramseteConstraints;
		xError = yError = 0;
	}
	
	private void computeLocalErrors(double xE, double yE, double currentTheta) {
		double sin = Math.sin(currentTheta);
		double cos = Math.cos(currentTheta);
		
		xError = (cos * xE + sin * yE);
		yError = (- sin * xE + cos * yE);
	}
	
//	/**
//	 * must be called often to be accurate
//	 *
//	 * @param motionProfile the motion profile being used to calculate target velocity and angular velocity
//	 */
	
	public void update(MotionProfile motionProfile, double xE, double yE, double thetaError, double currentTheta) {
		double vD = motionProfile.getTargetVelocity(); // TODO: review once finished with trackers, motion profiles, guided vector fields and paths
		double wD = motionProfile.getTargetAngularVelocity();
		
		double k = 2 * ramseteConstraints.getZeta() * Math.sqrt((wD * wD) + ramseteConstraints.getBeta() * (vD * vD));
		
		computeLocalErrors(xE, yE, currentTheta);
		
		v = vD * Math.cos(thetaError) + k * yError;
		w = wD + k * thetaError + (ramseteConstraints.getBeta() * vD * Math.sin(thetaError) * xError / thetaError);
	}
	
	public Pose2D getTargetPose() {
		return targetPose;
	}
	
	public void setTargetPose(Pose2D targetPose) {
		this.targetPose = targetPose;
	}
}
