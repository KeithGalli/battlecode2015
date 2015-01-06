package oldbot;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class BaseRobot {

	public static RobotController rc;
	public static int id;
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	
	
	public static int waypointBand = 20000;
	
	public static int mapDataBand = 30000;
	public static int voidDataBand = 40000;
	
	static int bigBoxSize = 5;
	static int myBand = 100;


	// Default constructor
	public BaseRobot(RobotController myRC) {
		rc = myRC;
		id = rc.getRobot().getID();

		DataCache.init(this); // this must come first
		BroadcastSystem.init(this);
		Functions.init(rc);
		
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