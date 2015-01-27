package launchers;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class COMMANDERRobot extends BaseRobot {

	public MapLocation locationOfSensedEnemy;
	public MapLocation firstTarget;
	public boolean reachedTower;
	public boolean reachedTowerOrVoid;
	public MapLocation[] twoTowerTargets;
	public boolean reachedFirstTower;
	public boolean reachedSecondTower;
	public boolean targetOne;
	public Direction directionOfMiners;
	public MapLocation locationOfMiners;


	public COMMANDERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		locationOfSensedEnemy = null;
		firstTarget = theirHQ;
		reachedTower = false;
		reachedTowerOrVoid = false;
		twoTowerTargets = getFurthestTowersFromEachOther();
		System.out.println("target 1 " +twoTowerTargets[0].x + " "+ twoTowerTargets[0].y);
		System.out.println("target 2 " + twoTowerTargets[1].x + " "+ twoTowerTargets[1].y);
		reachedFirstTower = false;
		reachedSecondTower = false;
		targetOne = true;
		directionOfMiners = null;
		locationOfMiners = null;
	}

	@Override
	public void run() {
		try {
			DataCache.updateRoundVariables();
			int startSupplyQueue = rc.readBroadcast(COMMANDER_START_QUEUE_CHAN);
			int endSupplyQueue = rc.readBroadcast(COMMANDER_END_QUEUE_CHAN);
			MapLocation location = rc.getLocation();
//			RobotInfo[] enemiesAround = rc.senseNearbyRobots(24,theirTeam);
			RobotInfo[] enemiesAround = rc.senseNearbyRobots(24,theirTeam);
			rc.setIndicatorString(1,new Integer(enemiesAround.length).toString());
			RobotInfo[] enemiesToAttack = rc.senseNearbyRobots(RobotType.COMMANDER.attackRadiusSquared, theirTeam);
//			if(enemiesAround.length > 0 && enemiesToAttack.length ==0){
//				NavSystem.dumbNav(enemiesAround[0].location);
//			} else if(enemiesToAttack.length ==0){
//				NavSystem.dumbNav(getOurClosestTowerToThem());
//				
//			} else {
//				attackLeastHealthEnemy(enemiesToAttack);
//			}
			if(rc.getSupplyLevel() < 45 && enemiesAround.length == 0 && enemiesToAttack.length == 0){
				Direction dirToMove = NavSystem.dumbNav(myHQ);
				move(dirToMove,location);
			}
			else if(enemiesToAttack.length == 0 && enemiesAround.length ==0){
				if(directionOfMiners != null && locationOfMiners != null){
					if(locationOfMiners.equals(location)){
						Direction dirToMove = NavSystem.dumbNav(location.add(directionOfMiners));
						move(dirToMove, location);
					} else {
						Direction dirToMove = NavSystem.dumbNav(locationOfMiners);
						move(dirToMove, location);
					}

				} else{
					if(Clock.getRoundNum() % 100 == 0){
						targetOne = !targetOne;
					}
					Direction dirToMove;
					if(targetOne){
						dirToMove = NavSystem.dumbNav(twoTowerTargets[0]);
					} else {
						dirToMove = NavSystem.dumbNav(twoTowerTargets[1]);
					}
					move(dirToMove,location);
				}
//				if(senseTowersClose(location)==0){
//					Direction dirToMove = null;
//					if(!reachedTower && !reachedFirstTower && !reachedSecondTower) {
//						dirToMove = NavSystem.dumbNav(theirHQ);
//						
//					} else if ( !reachedFirstTower) {
//						dirToMove = NavSystem.dumbNav(twoTowerTargets[0]);
//					} else  {
//						dirToMove = NavSystem.dumbNav(twoTowerTargets[1]);
//					}
//					rc.setIndicatorString(0, "hasn't reached yet");
//
//					int min = Integer.MAX_VALUE;
//					for(MapLocation tower : rc.senseEnemyTowerLocations()){
//						int dist = location.distanceSquaredTo(tower);
//						if(dist < min){
//							min = dist;
//						}
//					}
//					rc.setIndicatorString(1,new Integer(min).toString());
//					
//					move(dirToMove,location);
//				} else {
//					int distanceToHQ = location.distanceSquaredTo(theirHQ);
//					int distanceToFirstTower = location.distanceSquaredTo(twoTowerTargets[0]);
//					int distanceToSecondTower = location.distanceSquaredTo(twoTowerTargets[1]);
//					Direction dirToMove = null;
//					if(distanceToHQ < distanceToFirstTower && distanceToHQ < distanceToSecondTower){
//						reachedTower = true;
//						rc.setIndicatorString(0, "reached hq");
//						dirToMove = NavSystem.dumbNav(twoTowerTargets[0]);
//					} else if(distanceToFirstTower < distanceToHQ && distanceToFirstTower < distanceToSecondTower){
//						reachedFirstTower = true;
//						dirToMove = NavSystem.dumbNav(twoTowerTargets[1]);
//					} else {
//						reachedSecondTower = true;
//						dirToMove = NavSystem.dumbNav(twoTowerTargets[0]);
//					}
//					rc.setIndicatorString(0, "reached something");
//					move(dirToMove, location);
//				}
//					if(!reachedTower) {
//						NavSystem.dumbNav(firstTarget);
//					} else {
//						Direction toExplore;
//						MapLocation newLocation;
//						if(reachedTowerOrVoid) {
//							toExplore = location.directionTo(theirHQ).rotateRight().rotateRight();
//							newLocation = location.add(toExplore);
//						} else {
//							toExplore = location.directionTo(theirHQ).rotateLeft().rotateLeft();
//							newLocation = location.add(toExplore);
//						}
//						if(senseNearbyTowers(newLocation)==0 && rc.senseTerrainTile(newLocation).isTraversable()){
//							if(rc.isCoreReady()){
//								rc.move(toExplore);
//							}
//						} else {
//							reachedTowerOrVoid = !reachedTowerOrVoid;
//						}
//					}
//				} else {
//					reachedTower = true;
//					Direction toExplore;
//					MapLocation newLocation;
//					if(reachedTowerOrVoid) {
//						toExplore = location.directionTo(theirHQ).rotateRight().rotateRight();
//						newLocation = location.add(toExplore);
//					} else {
//						toExplore = location.directionTo(theirHQ).rotateLeft().rotateLeft();
//						newLocation = location.add(toExplore);
//					}
//					if(senseNearbyTowers(newLocation)==0){
//						if(rc.isCoreReady()){
//							rc.move(toExplore);
//						}
//					} else {
//						reachedTowerOrVoid = !reachedTowerOrVoid;
//					}
////					NavSystem.dumbNav(firstTowerTarget);
////					if(location.distanceSquaredTo(firstTowerTarget) <= 26){
////						firstTowerTarget = getFurthestTower();
////					}
//				}
			}
			else if(enemiesToAttack.length == 0 && enemiesAround.length !=0 ) {
				boolean tanksOrLaunchers = false;
				RobotInfo robotToAvoid = null;
				for(RobotInfo ri: enemiesAround){
					RobotType type = ri.type;
					if(type == RobotType.TANK || type == RobotType.LAUNCHER){
						if(location.distanceSquaredTo(ri.location) <= 17){
							tanksOrLaunchers = true;
							robotToAvoid = ri;
							break;
						}
					} else if(type == RobotType.TOWER || type ==RobotType.HQ){
						tanksOrLaunchers = true;
						robotToAvoid = ri;
					}
				}
				if(tanksOrLaunchers){
					rc.setIndicatorString(0,"avoiding tank or launcher first else if");
					MapLocation locMovingTo = location.add(robotToAvoid.location.directionTo(location));
//					NavSystem.dumbNav(location.add(robotToAvoid.location.directionTo(location)));
					if(rc.senseTerrainTile(location.add(location.directionTo(locMovingTo))).isTraversable()){
//						NavSystem.dumbNav(locMovingTo);
//						if(rc.isCoreReady()){
//							rc.move(robotToAvoid.location.directionTo(location));
//						}
						Direction dirToMove = NavSystem.dumbNav(location.add(robotToAvoid.location.directionTo(location)));
						if(rc.isCoreReady()){
							move(dirToMove, location);
						}
					} else {
						int distance = location.distanceSquaredTo(locMovingTo);
						int teleportationDistance = 10;
						if(distance <= 10){
							teleportationDistance = distance-1;
						}
						Direction dirToFlash = location.directionTo(locMovingTo);
						rc.castFlash(location.add(dirToFlash,(int) Math.sqrt(teleportationDistance)));
						rc.setIndicatorString(2,"flashing");
					}
				} else{
					rc.setIndicatorString(0, "not avoiding tank first else if");
					RobotInfo robotToMoveTowards = enemiesAround[0];
					int distanceToRobot = location.distanceSquaredTo(robotToMoveTowards.location);
					for(RobotInfo ri : enemiesAround){
						RobotType type = ri.type;
						if(type == RobotType.MINER || type == RobotType.SOLDIER || type == RobotType.BEAVER || type == RobotType.BASHER || type == RobotType.DRONE){
							int newDistance = location.distanceSquaredTo(ri.location);
							if(newDistance< distanceToRobot){
								robotToMoveTowards = ri;
								distanceToRobot = newDistance;
							}
						} 
					}
					if(rc.senseTerrainTile(location.add(location.directionTo(robotToMoveTowards.location))).isTraversable()){
						rc.setIndicatorString(0, "tile is traversable");
//						if(rc.isCoreReady()){
//							rc.move(location.directionTo(robotToMoveTowards.location));
//						}
						Direction dirToMove = NavSystem.dumbNav(robotToMoveTowards.location);
						if(robotToMoveTowards.type == RobotType.MINER){
							directionOfMiners = location.directionTo(robotToMoveTowards.location);
							locationOfMiners = robotToMoveTowards.location;
						}
						move(dirToMove, location);
					} else {
						rc.setIndicatorString(0, "tile is not traversable");
						int distance = location.distanceSquaredTo(robotToMoveTowards.location);
						int teleportationDistance = 10;
						if(distance <= 10){
							teleportationDistance = distance-1;
						}
						Direction dirToFlash = location.directionTo(robotToMoveTowards.location);
						if(rc.isCoreReady()){
							rc.castFlash(location.add(dirToFlash,(int)Math.sqrt(teleportationDistance)));
						}
						rc.setIndicatorString(2,"flashing");
					}
				}
//				RobotInfo[] tanksAndLaunchersInAttackingDistance = new RobotInfo[enemiesAround.length];
//				RobotInfo robotToMoveTowards = enemiesToAttack[0];
//				int distanceToRobot = location.distanceSquaredTo(robotToMoveTowards.location);
//				for(RobotInfo ri : enemiesAround){
//					RobotType type = ri.type;
//					if(type == RobotType.MINER || type == RobotType.SOLDIER || type == RobotType.BEAVER || type == RobotType.BASHER){
//						int newDistance = location.distanceSquaredTo(ri.location);
//						if(newDistance< distanceToRobot){
//							robotToMoveTowards = ri;
//							distanceToRobot = newDistance;
//						}
//					} else if(type == RobotType.TANK || type == RobotType.LAUNCHER){
//						tanksAndLaunchersInAttackingDistance[tanksAndLaunchersInAttackingDistance.length] = ri;
//					}
//				}
//				MapLocation newLoc = location.add(location.directionTo(robotToMoveTowards.location));
//				boolean safeToMove = true;
//				if(tanksAndLaunchersInAttackingDistance.length >0){
//					for(RobotInfo ri : tanksAndLaunchersInAttackingDistance){
//						if(newLoc.distanceSquaredTo(ri.location) < 15){
//							safeToMove = false;
//							break;
//						}
//					}
//				}
//				if(safeToMove){
//					NavSystem.dumbNav(robotToMoveTowards.location);
//				}
			} else if(enemiesToAttack.length >0 ){
				boolean tanksOrLaunchers = false;
				RobotInfo robotToAvoid = null;
				for(RobotInfo ri: enemiesAround){
					RobotType type = ri.type;
					if(type == RobotType.TANK || type == RobotType.LAUNCHER){
						if(location.distanceSquaredTo(ri.location) <= 17){
							tanksOrLaunchers = true;
							robotToAvoid = ri;
							break;
						}
					} else if(type == RobotType.TOWER || type ==RobotType.HQ){
						tanksOrLaunchers = true;
						robotToAvoid = ri;
					}
				}
				if(tanksOrLaunchers){
					rc.setIndicatorString(0,"avoiding tank or launcher second else if");
					//NavSystem.dumbNav(location.add(robotToAvoid.location.directionTo(location)));
					MapLocation locMovingTo = location.add(robotToAvoid.location.directionTo(location));
					if(rc.senseTerrainTile(location.add(location.directionTo(locMovingTo))).isTraversable()){
//						if(rc.isCoreReady()){
//							rc.move(robotToAvoid.location.directionTo(location));
//						}
						Direction dirToMove = NavSystem.dumbNav(location.add(robotToAvoid.location.directionTo(location)));
						if(rc.isCoreReady()){
							move(dirToMove, location);
						}
//						NavSystem.dumbNav(locMovingTo);
					} else {
//						int distance = location.distanceSquaredTo(locMovingTo);
//						int teleportationDistance = 10;
//						if(distance <= 10){
//							teleportationDistance = distance-1;
//						}
//						Direction dirToFlash = location.directionTo(locMovingTo);
//						rc.castFlash(location.add(dirToFlash,teleportationDistance));
//						rc.setIndicatorString(2,"flashing");
						Direction dirToFlash = location.directionTo(locMovingTo);
						if(rc.isCoreReady()){
							rc.castFlash(location.add(dirToFlash, 2));
						}
						rc.setIndicatorString(2, "flashing");
					}
				} else {
					rc.setIndicatorString(0,"not avoiding tank second else if");
					boolean inAttackingRangeOfRob = false;
					RobotInfo robToAvoid = null;
					RobotInfo leastHealth = enemiesToAttack[0];
					double minHealth = leastHealth.health;
					for(RobotInfo ri: enemiesToAttack){
						RobotType type = ri.type;
						if(ri.health < minHealth){
							minHealth = ri.health;
							leastHealth = ri;
						}
						if(location.distanceSquaredTo(ri.location) <= 5 || (type == RobotType.SOLDIER && location.distanceSquaredTo(ri.location) <=8)){
							inAttackingRangeOfRob = true;
							robToAvoid = ri;
							break;
						}
//						if(location.distanceSquaredTo(ri.location) <= 5){
//							inAttackingRangeOfRob = true;
//							robToAvoid = ri;
//							break;
//						}
					}
					if(inAttackingRangeOfRob){
						Direction dirToMove = NavSystem.dumbNav(location.add(robToAvoid.location.directionTo(location)));
						if(rc.isCoreReady()){
							move(dirToMove, location);
						} else if(rc.isWeaponReady()) {
							rc.attackLocation(leastHealth.location);
						}
					} else if(rc.isWeaponReady()){
						rc.attackLocation(leastHealth.location);
					}
				}
			} 


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
		
		
//		public RobotInfo tanksOrLaunchersNearby(RobotInfo[] nearbyEnemies, MapLocation location){
//			boolean tanksOrLaunchers = false;
//			for(RobotInfo ri: nearbyEnemies){
//				RobotType type = ri.type;
//				if(type == RobotType.TANK || type == RobotType.LAUNCHER){
//					if(location.distanc)
//				}
//			}
//		}
	}
}
