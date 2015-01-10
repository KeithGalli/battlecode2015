package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {


	public final static int BEAVER_COUNT_BCAST = 1;
	public final static int SUPPLY_DEPOT_COUNT_BCAST = 2;
	public final static int TECH_INST_COUNT_BCAST = 3;
	public final static int COMP_COUNT_BCAST = 4;
	public final static int TRAIN_FIELD_BCAST = 5;
	public final static int COMMANDER_BCAST = 6;
	public final static int BARRACKS_BCAST = 7;
	public final static int SOLDIER_BCAST = 8;
	public final static int TANK_FACT_BCAST = 9;
	public final static int TANK_BCAST = 10;
	public final static int BASHER_BCAST =11;
	public final static int HELIPAD_BCAST = 12;
	public final static int DRONE_BCAST = 13;
	public final static int AERO_LAB_BCAST = 14;
	public final static int LAUNCHER_BCAST = 15;
	public final static int HAND_STATION_BCAST = 16;
	public final static int MINER_FACT_BCAST = 17;
	public final static int MINER_BCAST = 18;
	
	public Team opp;
	public Team team;


	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);
		team = rc.getTeam();
		opp = team.opponent();
		//Init Systems//
		NavSystem.init(rc);




	}

	@Override
	public void run() {
		try {
			
			RobotInfo[] myRobots;
			try {
				int numBeavers = 0;
				int numSupplyDepot = 0;
				int numTechInst = 0;
				int numComputers = 0;
				int numTrainField = 0;
				int numCommander = 0;
				int numBarracks = 0;
				int numSoldiers = 0;
				int numTankFactories = 0;
				int numTanks = 0;
				int numBashers = 0;
				int numHelipads = 0;
				int numDrones = 0;
				int numAeroLabs = 0;
				int numLaunchers = 0;
				int numHandStations = 0;
				int numMinerFactories = 0;
				int numMiners = 0;
				myRobots = rc.senseNearbyRobots(999999, team);
				for(RobotInfo robInfo : myRobots){
					RobotType type = robInfo.type;
					if(type == RobotType.BEAVER){
						numBeavers ++;
					} else if( type == RobotType.SUPPLYDEPOT){
						numSupplyDepot ++;
					} else if( type == RobotType.TECHNOLOGYINSTITUTE){
						numTechInst ++;
					} else if(type == RobotType.COMPUTER){
						numComputers ++;
					} else if(type == RobotType.TRAININGFIELD){
						numTrainField ++;
					} else if(type == RobotType.COMMANDER){
						numCommander ++;
					} else if( type == RobotType.BARRACKS){
						numBarracks ++;
					} else if(type == RobotType.SOLDIER){
						numSoldiers ++;
					} else if(type == RobotType.TANKFACTORY){
						numTankFactories ++;
					} else if(type == RobotType.TANK){
						numTanks ++;
					} else if (type == RobotType.BASHER){
						numBashers ++;
					} else if(type == RobotType.HELIPAD){
						numHelipads ++;
					} else if(type == RobotType.DRONE){
						numDrones ++;
					} else if(type == RobotType.AEROSPACELAB){
						numAeroLabs ++;
					} else if(type == RobotType.LAUNCHER){
						numLaunchers ++;
					} else if(type == RobotType.HANDWASHSTATION){
						numHandStations ++;
					} else if(type == RobotType.MINERFACTORY){
						numMinerFactories ++;
					} else if(type == RobotType.MINER){
						numMiners ++;
					}
				}
				rc.broadcast(BEAVER_COUNT_BCAST, numBeavers);
				rc.broadcast(SUPPLY_DEPOT_COUNT_BCAST, numSupplyDepot);
				rc.broadcast(TECH_INST_COUNT_BCAST, numTechInst);
				rc.broadcast(COMP_COUNT_BCAST, numComputers);
				rc.broadcast(TRAIN_FIELD_BCAST, numTrainField);
				rc.broadcast(COMMANDER_BCAST, numCommander);
				rc.broadcast(BARRACKS_BCAST, numBarracks);
				rc.broadcast(SOLDIER_BCAST, numSoldiers);
				rc.broadcast(TANK_FACT_BCAST, numTankFactories);
				rc.broadcast(TANK_BCAST, numTanks);
				rc.broadcast(BASHER_BCAST, numBashers);
				rc.broadcast(HELIPAD_BCAST, numHelipads);
				rc.broadcast(DRONE_BCAST, numDrones);
				rc.broadcast(AERO_LAB_BCAST, numAeroLabs);
				rc.broadcast(LAUNCHER_BCAST, numLaunchers);
				rc.broadcast(HAND_STATION_BCAST, numHandStations);
				rc.broadcast(MINER_FACT_BCAST, numMinerFactories);
				rc.broadcast(MINER_BCAST, numMiners);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
