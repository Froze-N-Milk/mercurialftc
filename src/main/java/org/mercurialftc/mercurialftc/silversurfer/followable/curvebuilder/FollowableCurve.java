package org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder;

import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve.QuinticBezierCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;
import org.mercurialftc.mercurialftc.silversurfer.geometry.ArcLengthHandler;
import org.mercurialftc.mercurialftc.silversurfer.motionprofile.MotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.motionprofile.MotionProfile;

import java.util.ArrayList;

public class FollowableCurve extends Followable {
	// todo handle turning stuff
	
	private QuinticBezierCurve[] curves;
	private CurveBuilder curveBuilder;
	private ArcLengthHandler arcLengthHandler;
	private ArrayList<MotionConstants> motionConstantsArray;
	private MotionProfile motionProfile;
	
	protected FollowableCurve(CurveBuilder curveBuilder, ArrayList<MotionConstants> motionConstantsArray, ArrayList<MarkerBuilder> unfinishedMarkers) {
		this.curveBuilder = curveBuilder;
		this.curves = curveBuilder.getResult();
		this.arcLengthHandler = new ArcLengthHandler(this);
		this.motionConstantsArray = motionConstantsArray;
		this.motionProfile = new MotionProfile(this);
		setOutputs(motionProfile.profile()); // runs the motion profiler on this spline
		
		Marker[] markers = new Marker[unfinishedMarkers.size()];
		for (int i = 0; i < unfinishedMarkers.size(); i++) {
			markers[i] = unfinishedMarkers.get(i).build(this);
		}
		
		setMarkers(markers);
	}
	
	/**
	 * @param elongationFactor the scalar factor by which to multiply the tangent
	 * @param index index of the tangent to elongate
	 */
	public void elongateTangents(double elongationFactor, int index) {
		curveBuilder.elongateTangents(elongationFactor, index);
		this.curves = curveBuilder.getResult();
	}
	
	/**
	 * @param i the index of the control point used to generate this curve
	 * @return the output that closely corresponds to the control point index passed in;
	 */
	public Output getOutputFromIndex(int i) {
		if(i < 0) {
			return getOutputs()[0];
		}
		
		double arcLength = arcLengthHandler.getBreakpoints()[i];
		int outputIndex = (int) (arcLength / motionProfile.getArcSegmentLength());
		
		if(outputIndex > getOutputs().length) {
			return getOutputs()[getOutputs().length - 1];
		}
		
		return getOutputs()[outputIndex];
	}
	
	public QuinticBezierCurve[] getCurves() {
		return curves;
	}
	
	public ArcLengthHandler getArcLengthHandler() {
		return arcLengthHandler;
	}
	
	public ArrayList<MotionConstants> getMotionConstantsArray() {
		return motionConstantsArray;
	}
	
	public CurveBuilder getCurveBuilder() {
		return curveBuilder;
	}
}
