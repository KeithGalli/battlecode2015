package drone_only_strategy;


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
		NavSystem.init(rc);




	}

	@Override
	public void run() {
		try {
			
			if(Clock.getBytecodeNum() < 900){
				transferSupplies(rc);
			} else{
				transferSpecificSupplies(RobotType.DRONE,rc);
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
            
            rc.broadcast(MINER_FACT_PREVIOUS_CHAN, numMinerFactories);
            rc.broadcast(MINER_PREVIOUS_CHAN, numMiners);
            rc.broadcast(BEAVER_PREVIOUS_CHAN, numBeavers);
            rc.broadcast(HELIPAD_PREVIOUS_CHAN, numHelipads);
            rc.broadcast(DRONE_PREVIOUS_CHAN, numDrones);
            
            RobotInfo[] enemies = getEnemiesInAttackingRange(RobotType.HQ);
            if(enemies.length>0 && rc.isWeaponReady()){
            	attackLeastHealthEnemy(enemies);
            } else if (rc.isCoreReady() && rc.getTeamOre() >= 100 && rc.readBroadcast(BEAVER_PREVIOUS_CHAN)<5) {
                RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
            }

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
