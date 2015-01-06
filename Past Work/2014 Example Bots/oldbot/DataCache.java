package oldbot;

import java.util.ArrayList;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class DataCache {

	public static BaseRobot robot;
	public static RobotController rc;

	public static MapLocation currentLocation;
	public static MapLocation ourHQLocation;
	public static MapLocation enemyHQLocation;
	public static int rushDistSquared;
	public static int rushDist;

	public static Direction[] directionArray = Direction.values();

	public static boolean onCycle;

	// Map width
	public static int mapWidth;
	public static int mapHeight;

	// Round variables - army sizes
	// Allied robots
	public static int numAlliedRobots;


	public static int numNearbyAlliedRobots;

	public static int numEnemyRobots;
	// Enemy robots

	
	public static Robot[] enemyRobots;
	public static Robot[] nearbyAlliedRobots;
	
	public static MapLocation[] alliedPastrs;
	public static int numAlliedPastrs;
	public static MapLocation closestAlliedPastr;

	public static MapLocation[] enemyPastrs;
	public static int numEnemyPastrs;
	public static MapLocation closestEnemyPastr;
	
	public static MapLocation[] potentialPastrs;

	public static Team team;
	


	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;

		currentLocation = rc.getLocation();
		team = rc.getTeam();
		ourHQLocation = rc.senseHQLocation();
		enemyHQLocation = rc.senseEnemyHQLocation();
		rushDistSquared = ourHQLocation.distanceSquaredTo(enemyHQLocation);
		rushDist = (int) Math.sqrt(rushDistSquared);

		mapWidth = rc.getMapWidth();
		mapHeight = rc.getMapHeight();
	}

	/**
	 * A function that updates round variables
	 */
	 public static void updateRoundVariables() throws GameActionException {
		currentLocation = rc.getLocation();

		numAlliedRobots = rc.senseRobotCount();
		nearbyAlliedRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().sensorRadiusSquared*2,rc.getTeam());
		enemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent());
		numEnemyRobots = enemyRobots.length;

		numNearbyAlliedRobots = nearbyAlliedRobots.length;
		
		alliedPastrs = rc.sensePastrLocations(rc.getTeam());
		enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());

	 }
//	 public static void broadcastPastrs(){
//		 BroadcastSystem.write(robot.enemyPastrChannel, pastrsToInt(enemyPastrs));
//		 BroadcastSystem.write(robot.alliedPastrChannel, pastrsToInt(alliedPastrs));
//	 }

//	 public static void receivePastrs(){
//		 enemyPastrs = intToPastrs(BroadcastSystem.read(robot.enemyPastrChannel));
//		 numEnemyPastrs = enemyPastrs.length;
//		 closestEnemyPastr = findClosest(enemyPastrs, rc.getLocation());
//		 alliedPastrs = intToPastrs(BroadcastSystem.read(robot.alliedPastrChannel));
//		 numAlliedPastrs = alliedPastrs.length;
//		 closestAlliedPastr = findClosest(alliedPastrs, rc.getLocation());
//	 }

	 
	 


}