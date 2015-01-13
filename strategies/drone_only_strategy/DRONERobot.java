package drone_only_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class DRONERobot extends BaseRobot {




	public DRONERobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
            
            if (getEnemiesInAttackingRange().length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
                }
            }
		    if (Clock.getRoundNum() < 1900) {
		        if (rc.isCoreReady() && rc.getSupplyLevel()<60 && rc.getLocation().distanceSquaredTo(this.myHQ)<40) {
		            RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
		        }
		        if (rc.isCoreReady() && rc.senseNearbyRobots(24, this.theirTeam).length < 1) {
		            RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
		        } else {
		            
		        }
		    } else if (Clock.getRoundNum() >= 1900) {
		        RobotPlayer.tryMove(rc.getLocation().directionTo(getClosestTower()));
		    }
		    transferMinerSupplies(rc);
            rc.broadcast(DRONE_CURRENT_CHAN, rc.readBroadcast(DRONE_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
