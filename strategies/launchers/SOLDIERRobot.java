package launchers;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SOLDIERRobot extends BaseRobot {

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
	public int towerNum;
	public static Random random = new Random();
	public MapLocation[] towers;


	public SOLDIERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
//		locationOfSensedEnemy = null;
//		firstTarget = theirHQ;
//		reachedTower = false;
//		reachedTowerOrVoid = false;
//		twoTowerTargets = getFurthestTowersFromEachOther();
//		System.out.println("target 1 " +twoTowerTargets[0].x + " "+ twoTowerTargets[0].y);
//		System.out.println("target 2 " + twoTowerTargets[1].x + " "+ twoTowerTargets[1].y);
//		reachedFirstTower = false;
//		reachedSecondTower = false;
//		targetOne = true;
//		directionOfMiners = null;
//		locationOfMiners = null;
		towers = rc.senseEnemyTowerLocations();
		towerNum = (rc.readBroadcast(SOLDIERS_MADE)) % towers.length;
		rc.broadcast(SOLDIERS_MADE, rc.readBroadcast(SOLDIERS_MADE)+1);
	}

	@Override
	public void run() {
		try {
			DataCache.updateRoundVariables();
			MapLocation location = rc.getLocation();
			RobotInfo[] enemiesAround = rc.senseNearbyRobots(24,theirTeam);
			rc.setIndicatorString(1,new Integer(enemiesAround.length).toString());
			RobotInfo[] enemiesToAttack = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);

//			int numMiners = numMiners(enemiesAround);
			MapLocation minerLoc = new MapLocation(rc.readBroadcast(MINERS_TO_ATTACK_X),rc.readBroadcast(MINERS_TO_ATTACK_Y));
//			if(minerLoc.equals(location) && numMiners==0){
//				rc.broadcast(NUM_MINERS_IN_POSITION, 0);
//			}
//			rc.setIndicatorString(0, "length of enemies " + new Integer(enemiesAround.length).toString());
//			rc.setIndicatorString(2, "number Miners " + new Integer(numMiners).toString());
			int currentNumMiners = rc.readBroadcast(NUM_MINERS_IN_POSITION);
//			if(numMiners>= currentNumMiners){
//				rc.broadcast(MINERS_TO_ATTACK_X, location.x);
//				rc.broadcast(MINERS_TO_ATTACK_Y, location.y);
//				rc.broadcast(NUM_MINERS_IN_POSITION, numMiners);
//			}
			if(rc.getSupplyLevel() < 45 && enemiesAround.length == 0 && enemiesToAttack.length == 0){
				Direction dirToMove = NavSystem.dumbNav(myHQ);
				move(dirToMove,location);
			}
			else if(enemiesToAttack.length == 0){
				if(minerLoc.equals(location)){
					rc.broadcast(NUM_MINERS_IN_POSITION, 0);
				}
				if(enemiesAround.length==0){
					if(currentNumMiners != 0){
						
						Direction dirToMove = NavSystem.dumbNav(minerLoc);
						move(dirToMove,location);
						rc.setIndicatorString(1,"moving towards miners");
						
					} else{

						Direction dirToMove = NavSystem.dumbNav(towers[towerNum]);
						move(dirToMove,location);
						rc.setIndicatorString(1, "moving towards tower");
					}
				} else {
					boolean tanksOrLaunchers = false;
					RobotInfo robotToAvoid = null;
					RobotInfo robotToMoveTowards = enemiesAround[0];
					int distanceToRobot = location.distanceSquaredTo(robotToMoveTowards.location);
					int numMiners =  0;
					rc.setIndicatorString(0,"bytecode before " + Clock.getBytecodesLeft());
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
							break;
						}
						else if(type == RobotType.MINER){
							numMiners++;
							int newDistance = location.distanceSquaredTo(ri.location);
							if(newDistance< distanceToRobot){
								robotToMoveTowards = ri;
								distanceToRobot = newDistance;
							}
						}
						else if(type == RobotType.SOLDIER || type == RobotType.BEAVER || type == RobotType.BASHER || type == RobotType.DRONE){
							int newDistance = location.distanceSquaredTo(ri.location);
							if(newDistance< distanceToRobot){
								robotToMoveTowards = ri;
								distanceToRobot = newDistance;
							}
						} 
					}

					if(tanksOrLaunchers){
						rc.setIndicatorString(0,"avoiding tank or launcher first else if");
//						MapLocation locMovingTo = location.add(robotToAvoid.location.directionTo(location));
						Direction dirToMove = NavSystem.dumbNav(location.add(robotToAvoid.location.directionTo(location)));
//						if(rc.isCoreReady()){
							move(dirToMove, location);
//						}

					} else{
						//rc.setIndicatorString(0, "not avoiding tank first else if");
//						RobotInfo robotToMoveTowards = enemiesAround[0];
//						int distanceToRobot = location.distanceSquaredTo(robotToMoveTowards.location);
//						int numMiners =  0;
//						for(RobotInfo ri : enemiesAround){
//							RobotType type = ri.type;
//							if(type == RobotType.MINER){
//								numMiners++;
//								int newDistance = location.distanceSquaredTo(ri.location);
//								if(newDistance< distanceToRobot){
//									robotToMoveTowards = ri;
//									distanceToRobot = newDistance;
//								}
//							}
//							else if(type == RobotType.SOLDIER || type == RobotType.BEAVER || type == RobotType.BASHER || type == RobotType.DRONE){
//								int newDistance = location.distanceSquaredTo(ri.location);
//								if(newDistance< distanceToRobot){
//									robotToMoveTowards = ri;
//									distanceToRobot = newDistance;
//								}
//							} 
//						}
						//rc.setIndicatorString(1,"bytecode after " + Clock.getBytecodesLeft());
						if(numMiners>= currentNumMiners){
							rc.broadcast(MINERS_TO_ATTACK_X, location.x);
							rc.broadcast(MINERS_TO_ATTACK_Y, location.y);
							rc.broadcast(NUM_MINERS_IN_POSITION, numMiners);
						}
						Direction dirToMove = NavSystem.dumbNav(robotToMoveTowards.location);
						move(dirToMove, location);
						//rc.setIndicatorString(1,"bytecode after " + Clock.getBytecodesLeft());
					}
				}

			} else{
				boolean tanksOrLaunchers = false;
				RobotInfo robotToAvoid = null;
				boolean inAttackingRangeOfRob = false;
				RobotInfo robInAttackRangeToAvoid = null;
				RobotInfo leastHealth = enemiesToAttack[0];
				double minHealth = leastHealth.health;
				int numMiners = 0;
				for(RobotInfo ri: enemiesAround){
					RobotType type = ri.type;
					int distance = location.distanceSquaredTo(ri.location);
					if(type == RobotType.TANK || type == RobotType.LAUNCHER){
						if(location.distanceSquaredTo(ri.location) <= 17){
							tanksOrLaunchers = true;
							robotToAvoid = ri;
							break;
						}
					} else if(type == RobotType.TOWER || type ==RobotType.HQ){
						tanksOrLaunchers = true;
						robotToAvoid = ri;
						break;
					}
					if(distance <= RobotType.SOLDIER.attackRadiusSquared){
						if(ri.health < minHealth){
							minHealth = ri.health;
							leastHealth = ri;
						}
						if(location.distanceSquaredTo(ri.location) <= 5 || (type == RobotType.SOLDIER && location.distanceSquaredTo(ri.location) <=8)){
							inAttackingRangeOfRob = true;
							robInAttackRangeToAvoid = ri;
							break;
						}
						if(type == RobotType.MINER){
							numMiners ++;
						}
					}
					
				}
				if(tanksOrLaunchers){
					rc.setIndicatorString(0,"avoiding tank or launcher second else if");
					Direction dirToMove = NavSystem.dumbNav(location.add(robotToAvoid.location.directionTo(location)));
					if(rc.isCoreReady()){
						move(dirToMove, location);
					}

				} else {
					rc.setIndicatorString(0,"not avoiding tank second else if");
//					boolean inAttackingRangeOfRob = false;
//					RobotInfo robToAvoid = null;
//					RobotInfo leastHealth = enemiesToAttack[0];
//					double minHealth = leastHealth.health;
//					int numMiners = 0;
//					for(RobotInfo ri: enemiesToAttack){
//						RobotType type = ri.type;
//						if(ri.health < minHealth){
//							minHealth = ri.health;
//							leastHealth = ri;
//						}
//						if(location.distanceSquaredTo(ri.location) <= 5 || (type == RobotType.SOLDIER && location.distanceSquaredTo(ri.location) <=8)){
//							inAttackingRangeOfRob = true;
//							robInAttackRangeToAvoid = ri;
//							break;
//						}
//						if(type == RobotType.MINER){
//							numMiners ++;
//						}
//
//					}
					if(inAttackingRangeOfRob){
						Direction dirToMove = NavSystem.dumbNav(location.add(robInAttackRangeToAvoid.location.directionTo(location)));
						if(rc.isCoreReady()){
							move(dirToMove, location);
						} else if(rc.isWeaponReady()) {
							rc.attackLocation(leastHealth.location);
						}
					} else if(rc.isWeaponReady()){
						rc.attackLocation(leastHealth.location);
					}
					if(numMiners>= currentNumMiners){
						rc.broadcast(MINERS_TO_ATTACK_X, location.x);
						rc.broadcast(MINERS_TO_ATTACK_Y, location.y);
						rc.broadcast(NUM_MINERS_IN_POSITION, numMiners);
					}
				}
			}
//			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(24,myTeam);
//			transferSpecificSupplies(RobotType.SOLDIER, rc, nearbyAllies);
			rc.broadcast(SOLDIER_CURRENT_CHAN, SOLDIER_CURRENT_CHAN+1);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
