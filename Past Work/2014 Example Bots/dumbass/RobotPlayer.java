package dumbass;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class RobotPlayer{
	
	static RobotController rc;
	static Direction allDirections[] = Direction.values();
	static Random randall = new Random();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	
	public static void run(RobotController rcIn) throws GameActionException{
		
		rc= rcIn;
		BaseRobot robot = null;
		
		int randseed = rc.getRobot().getID();
        Util.randInit(randseed, randseed * Clock.getRoundNum());
        
        try {
            switch(rc.getType()) {
            case HQ:
            	
                    robot = new HQRobot(rc);
                    break;
            case SOLDIER:
            	
                    robot = new SoldierRobot(rc);
                    break;
            case PASTR:
            	
            		robot = new PastrRobot(rc);
                    break;
            }
            robot.loop();
        } catch (Exception e) {
        	e.printStackTrace();
    }
		
	}
	
	
}