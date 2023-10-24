import org.mercurialftc.mercurialftc.silversurfer.encoderticksconverter.Units;
import org.mercurialftc.mercurialftc.silversurfer.geometry.Vector2D;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.CenterStageObstacleMap;
import org.mercurialftc.mercurialftc.silversurfer.geometry.obstaclemap.ObstacleMap;

public class ObstacleMapTests {
	private final ObstacleMap obstacleMap = new CenterStageObstacleMap(
			Units.MILLIMETER,
			600,
			200
	);

	//	@Test
	void centered() {
		Vector2D position = new Vector2D();
		System.out.println(obstacleMap.obstacleAvoidanceVector(position));
	}

	//	@Test
	void closeToWall() {
		Vector2D position = new Vector2D(-2 * 600 + 1, -2 * 600 + 1);
		System.out.println(obstacleMap.obstacleAvoidanceVector(position));
	}

}
