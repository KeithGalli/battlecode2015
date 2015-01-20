package tank_only_new;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SOLDIERRobot extends BaseRobot {
    
    private boolean hasBeenSupplied;



	public SOLDIERRobot(RobotController rc) throws GameActionException {
		super(rc);
        NavSystem.UNITinit(rc);
        MapEngine.UNITinit(rc);
        hasBeenSupplied = false;
	}

	@Override
	public void run() {
		try {
            DataCache.updateRoundVariables();
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.SOLDIER);
            MapLocation currentLocation = rc.getLocation();
            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
            RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(24, this.theirTeam);
            MapLocation helpNeededLocation = new MapLocation(rc.readBroadcast(95), rc.readBroadcast(96));
            boolean lessThanHalfWay = currentLocation.distanceSquaredTo(myHQ)<rc.readBroadcast(500);
            double supplyLevel = rc.getSupplyLevel();
            if(rc.getSupplyLevel() > 0){
                hasBeenSupplied = true;
            }
            if (enemyRobots.length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                }
            }
            if (rc.isCoreReady()) {
                if ((supplyLevel < 0 && currentLocation.distanceSquaredTo(this.myHQ)<25)) {
                    NavSystem.dumbNav(this.myHQ);
                }
            }
            if (Clock.getRoundNum() < 1400) {
                if (nearbyEnemies.length>0 && lessThanHalfWay ) {
                    NavSystem.dumbNav(nearbyEnemies[0].location);
                } else {
                    int fate = RobotPlayer.rand.nextInt(100);
                    int enemyLocationX = rc.readBroadcast(300);
                    int enemyLocationY = rc.readBroadcast(301);
                    if (enemyLocationX != 0) {
                        MapLocation enemyLocation = new MapLocation(enemyLocationX, enemyLocationY);
                        NavSystem.dumbNav(enemyLocation);
                    } else {
                        if (senseNearbyTowers(currentLocation, currentLocation.directionTo(this.theirHQ))==0 && lessThanHalfWay) {
                            RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
                        }

                    }

                }
            } else {
                if (rc.isCoreReady()) {
                    MapLocation closest  = getClosestTower();
                    if (closest != null) {                        
                        NavSystem.dumbNav(closest);
                    } else {
                        NavSystem.dumbNav(DataCache.enemyHQ);
                    }
                } 
            }
            transferSpecificSupplies(RobotType.SOLDIER, rc, nearbyAllies);
            rc.broadcast(SOLDIER_CURRENT_CHAN, rc.readBroadcast(SOLDIER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
