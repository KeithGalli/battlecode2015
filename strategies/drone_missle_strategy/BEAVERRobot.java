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
			    } else if(rc.senseOre(rc.getLocation())>1){
			        RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
				    //rc.mine();
				} else{
			        RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
				}
			    
			}
		    rc.broadcast(BEAVER_CURRENT_CHAN, rc.readBroadcast(BEAVER_CURRENT_CHAN)+1);
		    rc.yield();
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
