package org.mercurialftc.mercurialftc.silversurfer.follower;

import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;

public interface ArbFollower {

	/**
	 * takes in driver inputs, rotates them and sends them to the wheels
	 *
	 * @param translationVector  should be a unit vector
	 * @param rotationalVelocity should be in the domain [-1, 1]
	 */
	void follow(Vector2D translationVector, double rotationalVelocity, double loopTime);
}
