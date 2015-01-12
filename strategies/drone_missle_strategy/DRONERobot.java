package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class DRONERobot extends BaseRobot {

	public static boolean attackMode = false;


	public DRONERobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			int numDrones = rc.readBroadcast(DRONE_PREVIOUS_CHAN);
			if(numDrones < 6){
				attackMode = false;
			} else if(numDrones >= 15) {
				attackMode = true;
			}
			if(rc.getHealth() < 2){
				RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
				int numAllies = nearbyAllies.length;
				double supply = rc.getSupplyLevel();
				for (RobotInfo ri : nearbyAllies){
					rc.transferSupplies((int) (supply/ numAllies), ri.location);
				}
			}
		    if (getEnemiesInAttackingRange().length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
                }
            }
		    if (rc.isCoreReady()) {
//		    	if(Clock.getRoundNum() > 1300 && attackMode ){
//		    		MapLocation tower = getClosestTower();
//		    		RobotPlayer.tryMove(rc.getLocation().directionTo(tower));
//		    	}
		    	if ( rc.readBroadcast(DRONE_PREVIOUS_CHAN)>15 && rc.senseNearbyRobots(16, theirTeam).length < 10 & senseNearbyTowers(rc.getLocation()) <2) {
		            MapLocation closestTower = new MapLocation(rc.readBroadcast(50), rc.readBroadcast(51));
		            RobotPlayer.tryMove(rc.getLocation().directionTo(closestTower));

		        }else if(rc.getSupplyLevel() < 60) {
		        	RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
		        } else if(rc.getSupplyLevel()> 300){
		        	moveAwayFromHQ();
		        	//transferMinerSupplies(rc);
		        } else{
		            	RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
		            }
		    }
		    transferMinerSupplies(rc);
            rc.broadcast(DRONE_CURRENT_CHAN, rc.readBroadcast(DRONE_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
	
	private void moveAwayFromHQ() {
		Direction dir = getDirectionAwayFromHQ();
		try {
			RobotPlayer.tryMove(dir);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

	private Direction getDirectionAwayFromHQ() {
		return (rc.getLocation().directionTo(rc.senseHQLocation()).opposite());
	}
}
