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

public class LAUNCHERRobot extends BaseRobot {

	public boolean hasBeenSupplied;
	public MapLocation targetToProtect;
	public boolean readyToFire;


	public LAUNCHERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		hasBeenSupplied = false;
		targetToProtect = getOurClosestTowerToThem();
		readyToFire = false;
	}

	@Override
	public void run() {
		try {
			DataCache.updateRoundVariables();
			MapLocation currentLocation = rc.getLocation();
			double supplyLevel = rc.getSupplyLevel();
			RobotInfo[] enemiesNearby = rc.senseNearbyRobots(24,theirTeam);
			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(24,myTeam);
			int numLaunchers = rc.readBroadcast(LAUNCHER_PREVIOUS_CHAN);
			int attack = rc.readBroadcast(LAUNCHERS_ATTACK);
			if(rc.getMissileCount() ==5){
				readyToFire = true;
			}
			if(rc.getMissileCount() ==0){
				readyToFire = false;
			}
			if(supplyLevel > 0){
				hasBeenSupplied = true;
			}
			if (rc.isCoreReady()) {
                if ((supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) || !hasBeenSupplied) {
                    Direction dirToMove = NavSystem.dumbNav(this.myHQ);
                    moveLauncher(dirToMove, rc.getLocation());
                }
            }
			boolean towerToShoot = false;
			int min = Integer.MAX_VALUE;
			MapLocation locToShoot = null;
			for(MapLocation loc : rc.senseEnemyTowerLocations()){
				System.out.println(currentLocation.distanceSquaredTo(loc));
				if(currentLocation.distanceSquaredTo(loc) < min){
					min = currentLocation.distanceSquaredTo(loc);
				}
				if(currentLocation.distanceSquaredTo(loc)<=36){
					towerToShoot = true;
					locToShoot = loc;
					break;
				}
			}
			rc.setIndicatorString(0,"min "+ min);
			
			if(towerToShoot  ){
				Direction dirToTarget = currentLocation.directionTo(locToShoot);
				if(rc.canLaunch(dirToTarget)){
					rc.launchMissile(dirToTarget);
				}
			}
			if(enemiesNearby.length > 0){
				RobotInfo robotToAvoid = enemiesNearby[0];
				boolean tanksOrLaunchers = false;
				int distance = currentLocation.distanceSquaredTo(robotToAvoid.location);
				RobotInfo furthest = enemiesNearby[0];
				int furthestDist = currentLocation.distanceSquaredTo(furthest.location);
				RobotInfo closest = enemiesNearby[0];
				int closestDist = currentLocation.distanceSquaredTo(closest.location);
					for(RobotInfo ri: enemiesNearby){
						RobotType type = ri.type;
						int currentDistance = ri.location.distanceSquaredTo(currentLocation);
//						if(currentDistance > furthestDist){
						if(currentDistance ==24){
							furthestDist = currentDistance;
							furthest = ri;
						}
						if(currentDistance < closestDist){
							closestDist = currentDistance;
							closest = ri;
						}
						if(type == RobotType.TANK && currentDistance <= 15){
							tanksOrLaunchers = true;
							robotToAvoid = ri;
							break;

						} else if((type == RobotType.TOWER || type ==RobotType.HQ) && currentDistance <=25){
							tanksOrLaunchers = true;
							robotToAvoid = ri;
							break;
						}
						else if(type == RobotType.LAUNCHER && currentDistance<= 25){
							tanksOrLaunchers = true;
							robotToAvoid = ri;
							break;
						}
//							else if(type == RobotType.MINER){
//								numMiners++;
//								int newDistance = location.distanceSquaredTo(ri.location);
//								if(newDistance< distanceToRobot){
//									robotToMoveTowards = ri;
//									distanceToRobot = newDistance;
//								}
//							}
						else if(type == RobotType.SOLDIER || type == RobotType.BEAVER || type == RobotType.BASHER || type == RobotType.DRONE || type == RobotType.MINER){
							int newDistance = currentLocation.distanceSquaredTo(ri.location);
							if(newDistance< distance){
								robotToAvoid = ri;
								distance = newDistance;
							}
						} 
					}
//					boolean towerToShoot = false;
//					int min = Integer.MAX_VALUE;
//					MapLocation locToShoot = null;
//					for(MapLocation loc : rc.senseEnemyTowerLocations()){
//						System.out.println(currentLocation.distanceSquaredTo(loc));
//						if(currentLocation.distanceSquaredTo(loc) < min){
//							min = currentLocation.distanceSquaredTo(loc);
//						}
//						if(currentLocation.distanceSquaredTo(loc)<=36){
//							towerToShoot = true;
//							locToShoot = loc;
//							break;
//						}
//					}
//					rc.setIndicatorString(0,"min "+ min);
					
//					if(towerToShoot  ){
//						Direction dirToTarget = currentLocation.directionTo(locToShoot);
//						if(rc.canLaunch(dirToTarget)){
//							rc.launchMissile(dirToTarget);
//						}
//					}
				
//					if(furthestDist ==24){
//						Direction dirToTarget = currentLocation.directionTo(furthest.location);
//						if(rc.canLaunch(dirToTarget)){
//							rc.launchMissile(dirToTarget);
//						}
//					}
					if(tanksOrLaunchers){
						Direction dirToMove = NavSystem.dumbNav(currentLocation.add(robotToAvoid.location.directionTo(currentLocation)));
						moveLauncher(dirToMove, currentLocation);
					}
					if( pathToFree(currentLocation,nearbyAllies, closest.location)) {
						Direction dirToTarget = currentLocation.directionTo(closest.location);
						if(rc.canLaunch(dirToTarget)){
							rc.launchMissile(dirToTarget);
						}
					}
				 
			}
			else if (numLaunchers < 7 ) {
                if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25 ) {
                    Direction dirToMove = NavSystem.dumbNav(this.myHQ);
                    moveLauncher(dirToMove, rc.getLocation());
                    rc.setIndicatorString(0, "moving to hq");
                } else  {
//                    MapLocation ourClosest = getOurClosestTowerToThem();
//                    //RobotInfo[] neighbors = rc.senseNearbyRobots(rc.getLocation(),1,rc.getTeam());
//                    //System.out.println(neighbors.length);
                	rc.setIndicatorString(0, "moving to tower " + targetToProtect.x + " " + targetToProtect.y);
                    double radiusOfLaunchers = numLaunchers/Math.PI;
                    for(MapLocation towerLoc : rc.senseEnemyTowerLocations()){
                    	Direction directionTowardsTower = targetToProtect.directionTo(towerLoc);
                    	MapLocation furthestTank = targetToProtect.add(directionTowardsTower, (int) Math.sqrt(radiusOfLaunchers));
                    	int distance = towerLoc.distanceSquaredTo(furthestTank);
                    	if(distance <=24){
                    		int difference = 24 - distance;
                    		int changeInTarget = (int)Math.sqrt(difference);
                    		Direction dirFromTowerToLoc = towerLoc.directionTo(targetToProtect);
                    		targetToProtect = targetToProtect.add(dirFromTowerToLoc,changeInTarget);
                    	}
                    }
                    rc.setIndicatorString(1," " + radiusOfLaunchers);
                    rc.setIndicatorString(2, " " + currentLocation.distanceSquaredTo(targetToProtect));
                    int distance = currentLocation.distanceSquaredTo(targetToProtect);
                    if(distance > radiusOfLaunchers || rc.canMove(currentLocation.directionTo(targetToProtect)) ) {
                    	Direction dirToMove = NavSystem.dumbNav(targetToProtect);
                    	moveLauncher(dirToMove, currentLocation);
//                    	rc.setIndicatorString(1,"moving");
//                    	System.out.println("moving");
                    }
                }
            } else if(rc.isCoreReady()) {
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
			RobotInfo[] supplyToExchangeRobots = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,myTeam);
			transferSpecificSupplies(RobotType.LAUNCHER,rc, supplyToExchangeRobots);
			rc.broadcast(LAUNCHER_CURRENT_CHAN, rc.readBroadcast(LAUNCHER_CURRENT_CHAN)+1);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
