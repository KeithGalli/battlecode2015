package dumbass;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class BaseRobot {

	public static int enemyPastrChannel = 10;
	public static int alliedPastrChannel = 11;
	
	public static int pastrLocationChannel = 30;
	public static int pastrBroadcastChannel = 31;
	public static RobotController rc;
	public static int id;
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();

	
	static int bigBoxSize = 5;
	static int myBand = 100;




	// Default constructor
	public BaseRobot(RobotController myRC) {
		rc = myRC;
		id = rc.getRobot().getID();

		DataCache.init(this); // this must come first
		BroadcastSystem.init(this);
		
		
//		if (rc.getType()==RobotType.HQ){
//			try {
//				//NavSystem.initNavSystem(this);
//			} catch (GameActionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	// Actions for a specific robot
	abstract public void run();

	public void loop() {
		while (true) {
			try {
				run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			rc.yield();
		}
	}
}