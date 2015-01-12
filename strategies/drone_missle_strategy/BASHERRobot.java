package drone_missle_strategy;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
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
                int centerMapX = (this.theirHQ.x + this.myHQ.x)/2;
                int centerMapY = (this.theirHQ.y + this.myHQ.y)/2;
                MapLocation centerMap = new MapLocation(centerMapX, centerMapY);
                RobotPlayer.tryMove(rc.getLocation().directionTo(centerMap));    
            }
	        rc.broadcast(BASHER_CURRENT_CHAN, rc.readBroadcast(BASHER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
