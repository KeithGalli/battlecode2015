package Striker;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer{
	
	public static RobotController robot;
	static Direction allDirections[] = Direction.values();
	static Random rand = new Random();
	static int directionalLooks[] = new int[]{0,1,-1,2,-2};
	static int jobChannel = 15;
	static int jobList = 0;
	
	public static void run(RobotController rc){
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
            default:
                    break;
            }
            robot.loop();
        } catch (Exception e) {
//            e.printStackTrace();
    }
		
	}
}
