package launchers;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {




	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);

		//Init Systems//
		NavSystem.HQinit(rc);
		MapEngine.HQinit(rc);




	}

	@Override
	public void run() {
		try {
			
//			if(Clock.getBytecodeNum() < 900){
//				transferSupplies(rc);
//			} else{
//				transferSpecificSupplies(RobotType.DRONE,rc);
//			}
			if(Clock.getRoundNum() ==1){
				rc.broadcast(SUPPLIER_START_QUEUE_CHAN, 104);
				rc.broadcast(SUPPLIER_END_QUEUE_CHAN, 104);
				rc.broadcast(SUPPLIER_ID_CHAN, 0);
				rc.broadcast(SUPPLIER_NEEDED, 0);
				rc.broadcast(COMMANDER_START_QUEUE_CHAN, 203);
				rc.broadcast(COMMANDER_END_QUEUE_CHAN, 203);
//				rc.broadcast(104, 0);
//				rc.broadcast(105,0);
			}
//			System.out.println("start channel " + rc.readBroadcast(SUPPLIER_START_QUEUE_CHAN));
//			System.out.println("end channel " + rc.readBroadcast(SUPPLIER_END_QUEUE_CHAN));
			//hqTransferSupplies(rc);
			hqTransferAllSuppliesForRestOfGame(rc);
			
			if(Clock.getRoundNum() % 10 ==0){
				rc.broadcast(LAUNCHERS_ATTACK, 1);
			}else {
				rc.broadcast(LAUNCHERS_ATTACK, 0);
			}
			
		    int numMinerFactories = rc.readBroadcast(MINER_FACT_CURRENT_CHAN);
		    rc.broadcast(MINER_FACT_CURRENT_CHAN, 0);
		    int numMiners = rc.readBroadcast(MINER_CURRENT_CHAN);
		    rc.broadcast(MINER_CURRENT_CHAN, 0);
		    int numBeavers = rc.readBroadcast(BEAVER_CURRENT_CHAN);
            rc.broadcast(BEAVER_CURRENT_CHAN, 0);
            int numHelipads = rc.readBroadcast(HELIPAD_CURRENT_CHAN);
            rc.broadcast(HELIPAD_CURRENT_CHAN, 0);
            int numDrones = rc.readBroadcast(DRONE_CURRENT_CHAN);
            rc.broadcast(DRONE_CURRENT_CHAN, 0);
            int numSupplierDrones = rc.readBroadcast(SUPPLIER_DRONES_CURRENT_CHAN);
            rc.broadcast(SUPPLIER_DRONES_CURRENT_CHAN, 0);
            int numSupplyDepots = rc.readBroadcast(SUPPLY_DEPOT_CURRENT_CHAN);
            rc.broadcast(SUPPLY_DEPOT_CURRENT_CHAN, 0);
            int numTanks = rc.readBroadcast(TANK_CURRENT_CHAN);
            rc.broadcast(TANK_CURRENT_CHAN, 0);
            int numTechInsts = rc.readBroadcast(TECH_INST_CURRENT_CHAN);
            rc.broadcast(TECH_INST_CURRENT_CHAN, 0);
            int numTrainFields = rc.readBroadcast(TRAIN_CURRENT_CHAN);
            rc.broadcast(TRAIN_CURRENT_CHAN, 0);
            int numSoldiers = rc.readBroadcast(SOLDIER_CURRENT_CHAN);
            rc.broadcast(SOLDIER_CURRENT_CHAN, 0);
            int numLaunchers = rc.readBroadcast(LAUNCHER_CURRENT_CHAN);
            rc.broadcast(LAUNCHER_CURRENT_CHAN, 0);
            
            rc.broadcast(MINER_FACT_PREVIOUS_CHAN, numMinerFactories);
            rc.broadcast(MINER_PREVIOUS_CHAN, numMiners);
            rc.broadcast(BEAVER_PREVIOUS_CHAN, numBeavers);
            rc.broadcast(HELIPAD_PREVIOUS_CHAN, numHelipads);
            rc.broadcast(DRONE_PREVIOUS_CHAN, numDrones);
            rc.broadcast(SUPPLIER_DRONES_PREVIOUS_CHAN, numSupplierDrones);
            rc.broadcast(SUPPLY_DEPOT_PREVIOUS_CHAN, numSupplyDepots);
            rc.broadcast(TANK_PREVIOUS_CHAN, numTanks);
            rc.broadcast(TECH_INST_PREVIOUS_CHAN, numTechInsts );
            rc.broadcast(TRAIN_PREVIOUS_CHAN, numTrainFields);
            rc.broadcast(SOLDIER_PREVIOUS_CHAN, numSoldiers);
            rc.broadcast(LAUNCHER_PREVIOUS_CHAN, numLaunchers);

            
            RobotInfo[] enemies = getEnemiesInAttackingRange(RobotType.HQ);
            if(enemies.length>0 && rc.isWeaponReady()){
            	attackLeastHealthEnemy(enemies);
            } else if (rc.isCoreReady() && ((rc.readBroadcast(40) > 0 && rc.hasSpawnRequirements(RobotType.BEAVER) && rc.readBroadcast(BEAVER_PREVIOUS_CHAN)<5 && rc.readBroadcast(MINER_PREVIOUS_CHAN)>2)  || rc.readBroadcast(BEAVER_PREVIOUS_CHAN)==0)) {
                RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
            }

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
