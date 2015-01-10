package drone_missle_strategy;

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
		    if (rc.isCoreReady()) {
		        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		        int distanceToClosest = rc.getLocation().distanceSquaredTo(enemyTowers[0]);
		        MapLocation closest = enemyTowers[0];
		        for (MapLocation tower: enemyTowers) {
		            int distanceToTower = rc.getLocation().distanceSquaredTo(tower);
		            if (distanceToTower<distanceToClosest) {
		                distanceToClosest = distanceToTower;
		                closest = tower;
		            }
		        }
		        RobotPlayer.tryMove(rc.getLocation().directionTo(closest));
		    }
		    rc.yield();
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
