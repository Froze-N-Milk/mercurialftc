package org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder;

import org.jetbrains.annotations.NotNull;
import org.mercurialftc.mercurialftc.silversurfer.followable.Followable;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.curve.QuinticBezierCurve;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.Marker;
import org.mercurialftc.mercurialftc.silversurfer.followable.markers.MarkerBuilder;
import org.mercurialftc.mercurialftc.silversurfer.followable.motionconstants.MecanumMotionConstants;
import org.mercurialftc.mercurialftc.silversurfer.geometry.ArcLengthHandler;
import org.mercurialftc.mercurialftc.silversurfer.followable.curvebuilder.motionprofile.MotionProfile;

import java.util.ArrayList;

public class FollowableCurve extends Followable {
	private final CurveBuilder curveBuilder;
	private final ArrayList<MecanumMotionConstants> motionConstantsArray;
	private final MotionProfile motionProfile;
	private final MecanumMotionConstants absoluteMotionConstants;
	private final double arcLength;
	private QuinticBezierCurve[] curves;

	protected FollowableCurve(@NotNull CurveBuilder curveBuilder, ArrayList<MecanumMotionConstants> motionConstantsArray, @NotNull ArrayList<MarkerBuilder> unfinishedMarkers, MecanumMotionConstants absoluteMotionConstants) {
		this.curveBuilder = curveBuilder;
		this.curves = curveBuilder.getResult();
		this.motionConstantsArray = motionConstantsArray;
		this.absoluteMotionConstants = absoluteMotionConstants;
		this.motionProfile = new MotionProfile(this);
		setOutputs(motionProfile.profile()); // runs the motion profiler on this spline

		arcLength = new ArcLengthHandler(this).getArcLength();

		Marker[] markers = new Marker[unfinishedMarkers.size()];
		for (int i = 0; i < unfinishedMarkers.size(); i++) {
			markers[i] = unfinishedMarkers.get(i).build(this);
		}

		setMarkers(markers);
	}

	/**
	 * @param elongationFactor the scalar factor by which to multiply the tangent
	 * @param index            index of the tangent to elongate
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
		if (i < 0) {
			return getOutputs()[0];
		}

		int outputIndex = (int) (arcLength / motionProfile.getArcSegmentLength());

		if (outputIndex > getOutputs().length) {
			return getOutputs()[getOutputs().length - 1];
		}

		return getOutputs()[outputIndex];
	}

	public QuinticBezierCurve[] getCurves() {
		return curves;
	}

	public ArrayList<MecanumMotionConstants> getMotionConstantsArray() {
		return motionConstantsArray;
	}

	public CurveBuilder getCurveBuilder() {
		return curveBuilder;
	}

	public MecanumMotionConstants getAbsoluteMotionConstants() {
		return absoluteMotionConstants;
	}
}
