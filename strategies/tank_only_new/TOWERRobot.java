package tank_only_new;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TOWERRobot extends BaseRobot {




	public TOWERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
		    RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(35, this.theirTeam);
		    RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.TOWER);
            if (enemyRobots.length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                }
            }
            if (nearbyEnemies.length>0) {
                int enemyZeroX = nearbyEnemies[0].location.x;
                int enemyZeroY = nearbyEnemies[0].location.y;
                rc.broadcast(300, enemyZeroX);
                rc.broadcast(301, enemyZeroY);
            } 
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
