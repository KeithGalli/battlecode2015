package launchers;

import battlecode.common.*;

import java.util.*;

public class DataCache {

    public static BaseRobot robot;
    public static RobotController rc;

    public static MapLocation enemyHQ;
    public static MapLocation ourHQ;

    public static MapLocation[] enemyTowers;
    public static MapLocation[] ourTowers;

    public static MapLocation currentLoc;

    public static List<MapLocation> seenLocs=new ArrayList<MapLocation>();


    public static void init(BaseRobot myRobot) {
        robot = myRobot;
        rc = robot.rc;
        enemyHQ = rc.senseEnemyHQLocation();
        ourHQ = rc.senseHQLocation();
        enemyTowers = rc.senseEnemyTowerLocations();
        ourTowers = rc.senseTowerLocations();
    }

    public static void updateRoundVariables() throws GameActionException {
        currentLoc = rc.getLocation();
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