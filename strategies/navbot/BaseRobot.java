package navbot;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class BaseRobot {


	public static int AEROSPACELAB_CONS = 1; 
	public static int BARRACKS_CONS = 2;
	public static int BASHER_CONS = 3; 
	public static int BEAVER_CONS = 4; 
	public static int COMMANDER_CONS = 5; 
	public static int COMPUTER_CONS = 6; 
	public static int DRONE_CONS = 7; 
	public static int HANDWASHSTATION_CONS = 8;
	public static int HELIPAD_CONS = 9; 
	public static int HQ_CONS = 10; 
	public static int LAUNCHER_CONS = 11; 
	public static int MINER_CONS = 12; 
	public static int MINERFACTORY_CONS = 13; 
	public static int MISSILE_CONS = 14; 
	public static int SOLDIER_CONS = 15; 
	public static int SUPPLYDEPOT_CONS = 16; 
	public static int TANK_CONS = 17; 
	public static int TANKFACTORY_CONS = 18; 
	public static int TECHNOLOGYINSTITUTE_CONS = 19; 
	public static int TOWER_CONS = 20; 
	public static int TRAININGFIELD_CONS = 21; 

	//public static int REFCHANNEL = 20000;

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

	public static int myChannel;
	
	public static RobotController rc;
	public static int id;
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();


	// Default constructor
	public BaseRobot(RobotController myRC) {
		rc = myRC;
		id = rc.getID();

		DataCache.init(this); //MUST COME FIRST
		Functions.init(this);
		BroadcastSystem.init(this);

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