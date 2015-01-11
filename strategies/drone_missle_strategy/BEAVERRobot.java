package drone_missle_strategy;

import battlecode.common.*;

public class BEAVERRobot extends BaseRobot {




	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
		    if (getEnemiesInAttackingRange().length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
                }
		    } else if (rc.isCoreReady()) {
		    	if(rc.getTeamOre() > 500 && rc.canBuild(Direction.NORTH, RobotType.MINERFACTORY)) {
		    		rc.build(Direction.NORTH, RobotType.MINERFACTORY);
		    	} else {
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
