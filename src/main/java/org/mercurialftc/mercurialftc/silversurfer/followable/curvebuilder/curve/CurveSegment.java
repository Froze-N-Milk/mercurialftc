package org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Pose2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public class CurveSegment {
	private Pose2D startPose;
	private Pose2D endPose;
	
	public Pose2D getStartPose() {
		return startPose;
	}
	
	public Pose2D getEndPose() {
		return endPose;
	}
	
	public void setStartPose(Pose2D startPose) {
		this.startPose = startPose;
	}
	
	public void setEndPose(Pose2D endPose) {
		this.endPose = endPose;
	}
	
	public CurveSegment(Pose2D startPose, Pose2D endPose) {
		this.startPose = startPose;
		this.endPose = endPose;
	}
	
	public Vector2D getTranslationalVector() {
		return new Vector2D(endPose.getX() - startPose.getX(), endPose.getY() - startPose.getY());
	}
	
	public Vector2D getInverseTranslationalVector() {
		return new Vector2D(startPose.getX() - endPose.getX(), startPose.getY() - endPose.getY());
	}
}
