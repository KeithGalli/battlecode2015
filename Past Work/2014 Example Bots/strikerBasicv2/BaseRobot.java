package strikerBasicv2;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class BaseRobot {

	public static int allChannel = 203;
	public static int mapChannel = 204;
	public static int assignGroup = 202;
	public static int rallyChannel = 201;
	public static int headChannel = 200;
	public static int jobList = 0;
	public RobotController rc;
	public int id;


	// Default constructor
	public BaseRobot(RobotController myRC) {
		rc = myRC;
		id = rc.getRobot().getID();

		DataCache.init(this); // this must come first
		BroadcastSystem.init(this);
		
		if (rc.getType()==RobotType.HQ){
			try {
				NavSystem.initNavSystem(this);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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