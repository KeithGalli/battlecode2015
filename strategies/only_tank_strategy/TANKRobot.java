package only_tank_strategy;

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
    public MapLocation targetToProtect;


	public TANKRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		hasBeenSupplied = false;
		targetToProtect = getOurClosestTowerToThem();
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
                    NavSystem.dumbNav(this.myHQ);
                }
            }
            if (Clock.getRoundNum() < 1400) {
                if (rc.isCoreReady()) {
                    if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) {
                        NavSystem.dumbNav(this.myHQ);
                    } else if (rc.senseNearbyRobots(20, this.theirTeam).length < 1 ) {
                        MapLocation ourClosest = getOurClosestTowerToThem();
                        RobotInfo[] neighbors = rc.senseNearbyRobots(rc.getLocation(),1,rc.getTeam());
                        //System.out.println(neighbors.length);
                        int numTanks = numTanksSurrounding(rc,neighbors);
                        System.out.println(numTanks);
                        double radiusOfTanks = rc.readBroadcast(TANK_PREVIOUS_CHAN)/Math.PI;
                        for(MapLocation towerLoc : rc.senseEnemyTowerLocations()){
                        	Direction directionTowardsTower = targetToProtect.directionTo(towerLoc);
                        	MapLocation furthestTank = targetToProtect.add(directionTowardsTower, (int) Math.sqrt(radiusOfTanks));
                        	int distance = towerLoc.distanceSquaredTo(furthestTank);
                        	if(distance <=24){
                        		int difference = 24 - distance;
                        		int changeInTarget = (int)Math.sqrt(difference);
                        		Direction dirFromTowerToLoc = towerLoc.directionTo(targetToProtect);
                        		targetToProtect = targetToProtect.add(dirFromTowerToLoc,changeInTarget);
                        		System.out.println("changing target");
                        	}
                        }
                        if(currentLocation.distanceSquaredTo(targetToProtect) > radiusOfTanks || rc.canMove(currentLocation.directionTo(targetToProtect)) ) {
                        	NavSystem.dumbNav(targetToProtect);
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
            transferSpecificSupplies(RobotType.TANK, rc, nearbyAllies);
            rc.broadcast(TANK_CURRENT_CHAN, rc.readBroadcast(TANK_CURRENT_CHAN)+1);


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
