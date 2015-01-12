package drone_missle_strategy;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class BASHERRobot extends BaseRobot {




	public BASHERRobot(RobotController rc) throws GameActionException {
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
                RobotInfo[] nearbyRobots = rc.senseNearbyRobots(24, this.theirTeam);
                if (rc.readBroadcast(DRONE_PREVIOUS_CHAN)>15) {
                    MapLocation closestTower = new MapLocation(rc.readBroadcast(50), rc.readBroadcast(51));
                    RobotPlayer.tryMove(rc.getLocation().directionTo(closestTower));
                } else if (nearbyRobots.length >= 1) {
                    RobotPlayer.tryMove(rc.getLocation().directionTo(nearbyRobots[0].location));
                } else {
                    int towerX = rc.readBroadcast(52);
                    int towerY = rc.readBroadcast(53);
                    MapLocation tower = new MapLocation(towerX, towerY);
                    RobotPlayer.tryMove(rc.getLocation().directionTo(tower));  
                }
            }
	        rc.broadcast(BASHER_CURRENT_CHAN, rc.readBroadcast(BASHER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
