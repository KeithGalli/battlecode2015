package drone_only_strategy;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class BEAVERRobot extends BaseRobot {




	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			if(rc.isCoreReady()){

		        
				double ore = rc.getTeamOre();
//				int minerFactories = rc.readBroadcast(MINER_FACT_PREVIOUS_CHAN);
				int minerFactoriesBuilt = rc.readBroadcast(40);
//				int helipads = rc.readBroadcast(HELIPAD_PREVIOUS_CHAN);
				int helipadsBuilt = rc.readBroadcast(43);
				
			    if (getEnemiesInAttackingRange().length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
	                }
			    } else if( ore>= 500 && ((minerFactoriesBuilt < 1 ) || (minerFactoriesBuilt < 2 && helipadsBuilt > 0))) {
		            Direction buildDirection = getBuildDirection(RobotType.MINERFACTORY);
		            if (buildDirection!=null) {
		                rc.build(buildDirection, RobotType.MINERFACTORY);
		                rc.broadcast(40, minerFactoriesBuilt+1);
		            }
			    }  else if( (minerFactoriesBuilt == 1 && ore >= 300 && helipadsBuilt < 2) || (minerFactoriesBuilt == 2 && ore>= 300 && helipadsBuilt < 4) ){
                    Direction buildDirection = getBuildDirection(RobotType.HELIPAD);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.HELIPAD);
                        rc.broadcast(43, helipadsBuilt+1);
                    }       
			    } else if(rc.senseOre(rc.getLocation())>2){
				    rc.mine();
				} else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation())> 14){
			        RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
				} else{
					RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
				}
			}
			transferSupplies(rc);
			rc.broadcast(BEAVER_CURRENT_CHAN, rc.readBroadcast(BEAVER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
