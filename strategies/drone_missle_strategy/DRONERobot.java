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

		        if (rc.readBroadcast(DRONE_PREVIOUS_CHAN)>15 && rc.senseNearbyRobots(16, theirTeam).length < 2) {
		            MapLocation closestTower = new MapLocation(rc.readBroadcast(50), rc.readBroadcast(51));
		            RobotPlayer.tryMove(rc.getLocation().directionTo(closestTower));
		        } else {
		            int fate = RobotPlayer.rand.nextInt(1000);
		            if (fate<200) {
		                RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
		            }

		        }

		    }
            rc.broadcast(DRONE_CURRENT_CHAN, rc.readBroadcast(DRONE_CURRENT_CHAN)+1);
		    rc.yield();
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
