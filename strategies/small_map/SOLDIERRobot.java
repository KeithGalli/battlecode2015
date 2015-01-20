package small_map;

import java.util.Arrays;
import java.util.Collections;

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
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.TANK);
            MapLocation currentLocation = rc.getLocation();
            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
            RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(24, this.theirTeam);
            MapLocation helpNeededLocation = new MapLocation(rc.readBroadcast(95), rc.readBroadcast(96));
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
                if ((supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) || !hasBeenSupplied) {
                    NavSystem.dumbNav(this.myHQ);
                }
            }
            if (Clock.getRoundNum() < 1000) {
                if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) {
                    NavSystem.dumbNav(this.myHQ);
                } else if (nearbyEnemies.length>0) {
                    NavSystem.dumbNav(nearbyEnemies[0].location);
                } else if (helpNeededLocation.x != 0) {
                    NavSystem.dumbNav(helpNeededLocation);
                } else {
                    int fate = RobotPlayer.rand.nextInt(100);
                    if (fate <80) {
                        Direction[] directions = getDirectionsAway(this.myHQ);
                        int randInt = RobotPlayer.rand.nextInt(5);
                        Collections.shuffle(Arrays.asList(directions));
                        for (Direction direction : directions) {
                            if (rc.canMove(direction)) {
                                rc.move(direction);
                            }
                        }                        
                    }

                }
                
            }
            else if (Clock.getRoundNum() < 1400) {
                if (rc.isCoreReady()) {
                    MapLocation ourClosest = getOurClosestTowerToThem();
                    RobotInfo[] neighbors = rc.senseNearbyRobots(rc.getLocation(),1,rc.getTeam());
                    //System.out.println(neighbors.length);
                    int numTanks = numTanksSurrounding(rc,neighbors);
                    System.out.println(numTanks);
                    double radiusOfTanks = rc.readBroadcast(TANK_PREVIOUS_CHAN)/Math.PI;
                    if(currentLocation.distanceSquaredTo(ourClosest) > radiusOfTanks || rc.canMove(currentLocation.directionTo(ourClosest)) ) {
                        NavSystem.dumbNav(ourClosest);
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
            rc.broadcast(SOLDIER_CURRENT_CHAN, rc.readBroadcast(TANK_CURRENT_CHAN)+1);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
