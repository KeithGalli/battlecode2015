package drone_missle_strategy;


import java.util.Random;

import drone_missle_strategy.RobotPlayer;
import battlecode.common.*;

public class BEAVERRobot extends BaseRobot {


	static int directionToInt(Direction d) {
		switch(d) {
			case NORTH:
				return 0;
			case NORTH_EAST:
				return 1;
			case EAST:
				return 2;
			case SOUTH_EAST:
				return 3;
			case SOUTH:
				return 4;
			case SOUTH_WEST:
				return 5;
			case WEST:
				return 6;
			case NORTH_WEST:
				return 7;
			default:
				return -1;
		}
	}
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	static Random rand = new Random();

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
			    	RobotPlayer.tryBuild(directions[rand.nextInt(8)], RobotType.MINERFACTORY);
			    }  else if(rc.readBroadcast(BARRACKS_PREVIOUS_CHAN) < 2 && minerFactories >= 2 && ore >= 300){
			    	RobotPlayer.tryBuild(directions[rand.nextInt(8)], RobotType.BARRACKS);
			    } else if(rc.readBroadcast(HELIPAD_PREVIOUS_CHAN) < 2 && minerFactories >= 2 && ore >= 300){
			    	RobotPlayer.tryBuild(directions[rand.nextInt(8)], RobotType.HELIPAD);
			    } else if(rc.senseOre(rc.getLocation())>1){
				    rc.mine();
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
