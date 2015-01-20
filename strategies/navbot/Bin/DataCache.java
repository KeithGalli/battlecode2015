package navbot;

import battlecode.common.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class DataCache {

	public static BaseRobot robot;
	public static RobotController rc;

	public static MapLocation enemyHQ;
	public static MapLocation ourHQ;

	public static MapLocation mapCenter;

	public static MapLocation[] enemyTowers;
	public static MapLocation[] ourTowers;

	public static MapLocation currentLoc;
	public static MapLocation internalLoc;

	public static int xmodifier;
	public static int ymodifier;

	public static List<MapLocation> seenLocs=new ArrayList<MapLocation>();


	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;
		enemyHQ = rc.senseEnemyHQLocation();
		ourHQ = rc.senseHQLocation();
		enemyTowers = rc.senseEnemyTowerLocations();
		ourTowers = rc.senseTowerLocations();

		mapCenter = new MapLocation((ourHQ.x+enemyHQ.x)/2, (ourHQ.y+enemyHQ.y)/2);
		xmodifier = ourHQ.x;
		ymodifier = ourHQ.y;
		//System.out.println(mapCenter);

	}

	public static void updateRoundVariables() throws GameActionException {
		currentLoc = rc.getLocation();
		internalLoc = Functions.locToInternalLoc(currentLoc);
	}

	public static void updateSeenLocs(List<MapLocation> newLocs){
		seenLocs.addAll(newLocs);
	}

	public static void displaySeenLocs(){
		for (MapLocation loc: seenLocs){
			rc.setIndicatorDot(loc, 255, 255, 255);
		}
	}
}
	