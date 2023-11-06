# Obstacle Maps

Silver Surfer supplies an easily extensible Obstacle Map interface, should you wish to populate an obstacle map yourself.

Silver Surfer also provides Obstacle Maps for the season, and an empty Obstacle Map:

```java
ObstacleMap centerStageObstacleMap = new CenterStageObstacleMap(
    Units.MILLIMETER, // Unit of the field tile size and robot 'size'
    ONE_TILE, // size of a field tile
    200 // 'size' of the robot, you may find better or worse results from adjsuting this, should be the radius of robot.
); 

ObstacleMap emptyObstacleMap = new EmptyObstacleMap();
```

If you have a way of dynamically identifying obstacles, you can can handle your own reference to an ArrayList of obstacles that an obstacle map will also look at:

```java
ArrayList<Obstacle> myDynamicObstacles = new ArrayList<>();

ObstacleMap centerStageObstacleMap = new CenterStageObstacleMap(
    Units.MILLIMETER, // Unit of the field tile size and robot 'size'
    ONE_TILE, // size of a field tile,
    myDynamicObstacles,
    200 // 'size' of the robot, you may find better or worse results from adjsuting this, should be the radius of robot.
); 

ObstacleMap emptyObstacleMap = new EmptyObstacleMap(
    Units.MILLIMETER, // Unit of the robot 'size'
    myDynamicObstacles,
    200 // 'size' of the robot, you may find better or worse results from adjsuting this, should be the radius of robot.
);

// now, as you add and remove items from the list, the obstacle map will handle them and the static, in-built obstacles 
```

An obstacle map can be used with the [#obstacleavoidantfollower](drive-base.md#obstacleavoidantfollower "mention") and [wave-building.md](wave-building.md "mention")
