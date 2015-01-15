package drone_only_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class DRONERobot extends BaseRobot {


	//static int supplierID = 0;

	public DRONERobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
//		    boolean towerOrHQNearby = false;
//            RobotInfo[] robotsNearby = rc.senseNearbyRobots(24, this.theirTeam);
//            for (RobotInfo robot : robotsNearby) {
//                if (robot.type == RobotType.TOWER || robot.type == RobotType.HQ) {
//                    towerOrHQNearby = true;
//                    break;
//                }
//            }

            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.DRONE);
            if (enemyRobots.length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                }
            }
		    if (Clock.getRoundNum() < 1900) {
//		        if (rc.isCoreReady() && rc.getSupplyLevel()<60 && rc.getLocation().distanceSquaredTo(this.myHQ)<40) {
//		            RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
//		        }
		        
		        if (rc.isCoreReady() && senseNearbyTowers(rc.getLocation())==0) {

		            int fate2 = RobotPlayer.rand.nextInt(2);
		            if (fate2==0) {
		                  int fate = RobotPlayer.rand.nextInt(5);
		                  Direction[] directions = getDirectionsToward(this.theirHQ);
		                  Direction direction = directions[fate];
		                  RobotPlayer.tryMove(direction);
		            } else RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
		        }
		    } else {
		        MapLocation closest  = getClosestTower();
		        if (closest!=null) {
		            RobotPlayer.tryMove(rc.getLocation().directionTo(closest));
		        } else {
		            RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
		        }

		    }
		    
		    transferSpecificSupplies(RobotType.DRONE, rc);
            rc.broadcast(DRONE_CURRENT_CHAN, rc.readBroadcast(DRONE_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
	
//	public static int getSupplierID(){
//		return supplierID;
//	}
}
