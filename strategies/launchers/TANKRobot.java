package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TANKRobot extends BaseRobot {
    
    public static Direction tarDir;

    public boolean hasBeenSupplied;


	public TANKRobot(RobotController rc) throws GameActionException {
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
                    Direction dirToMove = NavSystem.dumbNav(this.myHQ);
                    move(dirToMove, rc.getLocation());
                }
            }
            if (Clock.getRoundNum() < 1400) {
                if (rc.isCoreReady()) {
                    if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) {
                        Direction dirToMove = NavSystem.dumbNav(this.myHQ);
                        move(dirToMove, rc.getLocation());
                    } else if (rc.senseNearbyRobots(20, this.theirTeam).length < 1 ) {
                        MapLocation ourClosest = getOurClosestTowerToThem();
                        RobotInfo[] neighbors = rc.senseNearbyRobots(rc.getLocation(),1,rc.getTeam());
                        //System.out.println(neighbors.length);
                        int numTanks = numTanksSurrounding(rc,neighbors);
                        System.out.println(numTanks);
                        double radiusOfTanks = rc.readBroadcast(TANK_PREVIOUS_CHAN)/Math.PI;
                        if(currentLocation.distanceSquaredTo(ourClosest) > radiusOfTanks || rc.canMove(currentLocation.directionTo(ourClosest)) ) {
                        	Direction dirToMove = NavSystem.dumbNav(ourClosest);
                        	move(dirToMove, rc.getLocation());
                        }
                    }
                }
            } else {
                if (rc.isCoreReady()) {
                    MapLocation closest  = getClosestTower();
                    if (closest != null) {                        
                        Direction dirToMove = NavSystem.dumbNav(closest);
                        if(dirToMove != null) {
                        	rc.move(dirToMove);
                        }
                    } else {
                        Direction dirToMove = NavSystem.dumbNav(DataCache.enemyHQ);
                        if(dirToMove != null) {
                        	rc.move(dirToMove);
                        }
                    }
                } 
            }
            transferSpecificSupplies(RobotType.TANK, rc, nearbyAllies);
            rc.broadcast(TANK_CURRENT_CHAN, rc.readBroadcast(TANK_CURRENT_CHAN)+1);


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
