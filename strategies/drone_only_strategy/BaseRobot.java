package drone_only_strategy;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class BaseRobot {

	public static int SUPPLYDEPOT_COST = 100;
	public static int TECHNOLOGYINSTITUTE_COST = 200;
	public static int BARRACKS_COST = 300;
	public static int HELIPAD_COST = 300;
	public static int TRAININGFIELD_COST = 200;
	public static int TANKFACTORY_COST = 500;
	public static int MINERFACTORY_COST = 500;
	public static int HANDWASHSTATION_COST = 200;
	public static int AEROSPACELAB_COST = 500;
	public static int BEAVER_COST = 100;
	public static int COMPUTER_COST = 10;
	public static int SOLDIER_COST = 60;
	public static int BASHER_COST = 80;
	public static int MINER_COST = 50;
	public static int DRONE_COST = 125;
	public static int TANK = 250;
	public static int COMMANDER = 100;
	public static int LAUNCHER = 400;
	
	public static RobotController rc;
	public static int id;
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();


	// Default constructor
	public BaseRobot(RobotController myRC) {
		rc = myRC;
		id = rc.getID();

		DataCache.init(this); //MUST COME FIRST
		BroadcastSystem.init(this);
		MapEngine.init(this);

		// DataCache.init(this); // this must come first
		// BroadcastSystem.init(this);
		// Functions.init(rc);
		
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