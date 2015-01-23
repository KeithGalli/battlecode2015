package final_strategy;


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
//			if(Clock.getRoundNum()%50==0){
//			    rc.broadcast(200, 0);
//			}
			hqTransferAllSuppliesForRestOfGame(rc);
			
			rc.broadcast(200, 0);
			
			
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
            
            rc.broadcast(MINER_FACT_PREVIOUS_CHAN, numMinerFactories);
            rc.broadcast(MINER_PREVIOUS_CHAN, numMiners);
            rc.broadcast(BEAVER_PREVIOUS_CHAN, numBeavers);
            rc.broadcast(HELIPAD_PREVIOUS_CHAN, numHelipads);
            rc.broadcast(DRONE_PREVIOUS_CHAN, numDrones);
            rc.broadcast(SUPPLIER_DRONES_PREVIOUS_CHAN, numSupplierDrones);
            rc.broadcast(SUPPLY_DEPOT_PREVIOUS_CHAN, numSupplyDepots);
            rc.broadcast(TANK_PREVIOUS_CHAN, numTanks);
            
            RobotInfo[] enemies = getEnemiesInAttackingRange(RobotType.HQ);
            if(enemies.length>0 && rc.isWeaponReady()){
            	attackLeastHealthEnemy(enemies);
            } else if (rc.isCoreReady() && rc.hasSpawnRequirements(RobotType.BEAVER) && rc.readBroadcast(BEAVER_PREVIOUS_CHAN)<5) {
                RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
            }

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
