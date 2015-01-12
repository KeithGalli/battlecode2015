package drone_missle_strategy;


import java.util.Random;

import drone_missle_strategy.RobotPlayer;
import battlecode.common.*;

public class BEAVERRobot extends BaseRobot {

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			if(rc.getHealth() < 2){
				RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
				int numAllies = nearbyAllies.length;
				double supply = rc.getSupplyLevel()-2;
				for (RobotInfo ri : nearbyAllies){
					rc.transferSupplies((int) (supply/ numAllies), ri.location);
				}
			}
		    if(rc.isCoreReady()){
				double ore = rc.getTeamOre();
				int minerFactories = rc.readBroadcast(MINER_FACT_PREVIOUS_CHAN);
			    if (getEnemiesInAttackingRange().length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
	                }
			    } else if(minerFactories < 2 && ore>= 500){
			    	RobotPlayer.tryBuild(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.MINERFACTORY);
			    }  else if(rc.readBroadcast(BARRACKS_PREVIOUS_CHAN) < 2 && minerFactories >= 2 && ore >= 300){
			    	RobotPlayer.tryBuild(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BARRACKS);
			    } else if(rc.readBroadcast(HELIPAD_PREVIOUS_CHAN) < 2 && minerFactories >= 2 && ore >= 300){
			    	RobotPlayer.tryBuild(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.HELIPAD);
			    } else if(rc.readBroadcast(TANK_FACT_PREVIOUS_CHAN) < 2 && ore >= 500) {
			    	RobotPlayer.tryBuild(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.TANKFACTORY);
			    } else if(rc.senseOre(rc.getLocation())>1){
				    rc.mine();
				} else{
			        RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
				}
			    
			}
		    if(rc.getSupplyLevel() > 20){
		    	transferSupplies(rc);
		    }
		    rc.broadcast(BEAVER_CURRENT_CHAN, rc.readBroadcast(BEAVER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
