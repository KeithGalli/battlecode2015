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
				int minerFactoriesBuilt = rc.readBroadcast(40);
				int barracks = rc.readBroadcast(BARRACKS_PREVIOUS_CHAN);
				int barracksBuilt = rc.readBroadcast(41);
				int tankFactories = rc.readBroadcast(TANK_FACT_PREVIOUS_CHAN);
				int tankFactoriesBuilt = rc.readBroadcast(42);
				int helipads = rc.readBroadcast(HELIPAD_PREVIOUS_CHAN);
				int helipadsBuilt = rc.readBroadcast(43);
				
			    if (getEnemiesInAttackingRange().length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
	                }
			    } else if(minerFactoriesBuilt < 2 && ore>= 500) {
		            Direction buildDirection = getBuildDirection(RobotType.MINERFACTORY);
		            if (buildDirection!=null) {
		                rc.build(buildDirection, RobotType.MINERFACTORY);
		                rc.broadcast(40, minerFactoriesBuilt+1);
		            }
			    }  else if(barracksBuilt < 2 && minerFactories >= 2 && ore >= 300) {
                    Direction buildDirection = getBuildDirection(RobotType.BARRACKS);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.BARRACKS);
                        rc.broadcast(41, barracksBuilt+1);
                    }			        
                } else if(tankFactoriesBuilt < 2 && rc.hasBuildRequirements(RobotType.TANKFACTORY)) {
                    Direction buildDirection = getBuildDirection(RobotType.TANKFACTORY);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.TANKFACTORY);
                        rc.broadcast(42, tankFactoriesBuilt+1);
                    }           
			    } else if(helipadsBuilt < 2 && minerFactories >= 2 && ore >= 300){
                    Direction buildDirection = getBuildDirection(RobotType.HELIPAD);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.HELIPAD);
                        rc.broadcast(43, helipadsBuilt+1);
                    }       
			    } else if(rc.senseOre(rc.getLocation())>2){
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
