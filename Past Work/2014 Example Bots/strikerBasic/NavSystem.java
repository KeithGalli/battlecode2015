package strikerBasic;
import java.util.ArrayList;

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

public class NavSystem {
        
        public static SoldierRobot robot;
        public static RobotController rc;
        
        
        static Direction allDirections[] = Direction.values();
        
        // Used by both smart and backdoor nav
        public static MapLocation currentWaypoint;
        public static MapLocation destination;
        
        // Used to store the waypoints of a backdoor nav computation
        public static MapLocation[] backdoorWaypoints; // Should always have four MapLocations
        public static int backdoorWaypointsIndex;

        // Map size
        public static int mapHeight;
        public static int mapWidth;
        public static MapLocation mapCenter;
        
        // For BFS
        public static int BFSRound = 0;
        public static int[] BFSTurns;
        public static int BFSIdle = 0;
        
        // For swarm coefficients
        public static double swarmC;
        public static double swarmD = 0;
        
    	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
    	static ArrayList<MapLocation> path;
    	static int bigBoxSize = 3;

        
        /**
         * MUST CALL THIS METHOD BEFORE USING NavSystem
         * @param myRC
         */
        public static void init(SoldierRobot myRobot) {
                robot = myRobot;
                rc = robot.rc;
                BreadthFirst.init(rc, bigBoxSize);

        }
        
        /**
         * Tells rc to go to a location while defusing mines along the way
         * @param location
         * @throws GameActionException
         */
        public static void goToLocation(MapLocation location) throws GameActionException {
        	if(path.size()==0){
				path = BreadthFirst.pathTo(rc.getLocation(), location, 100000);
			}
			//follow breadthFirst path
			Direction bdir = BreadthFirst.getNextDirection(path, bigBoxSize);
			BasicPathing.tryToMove(bdir, true, rc, directionalLooks, allDirections);
        }
        
        
        public static void simpleMove(Direction chosenDirection) throws GameActionException{
    		for(int directionalOffset:directionalLooks){
    			int forwardInt = chosenDirection.ordinal();
    			Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
    			if(rc.canMove(trialDir)){
    				rc.move(trialDir);
    				break;
    			}
    		}
    	}
}