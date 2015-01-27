package launchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import battlecode.common.*;

public class MINERRobot extends BaseRobot {
	
	static Random rand = new Random();
	public final static int MINER_COST = 60;
	
    private final static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
//	static boolean willMove = true;
	public boolean supplied;
//	
//	private static Direction[] directionsForGroupZero = {Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST};
//	private static Direction[] directionsForGroupOne = {Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST};
//	private static Direction[] directionsForGroupTwo = {Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST};
//	private static Direction[] directionsForGroupThree = {Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
//	private static Direction[][] groupDirections = {directionsForGroupZero, directionsForGroupOne, directionsForGroupTwo, directionsForGroupThree};
//	
	
	
	public MINERRobot(RobotController rc) throws GameActionException {
		super(rc);
		supplied = true;
	}

	@Override
	public void run() {

		
		try {
			//assign the miner a group movement number
//			int minerNum = rc.readBroadcast(MINER_CURRENT_CHAN);
//			int minerGroupNum = minerNum % 4;
			RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.MINER);
	        MapLocation currentLocation = rc.getLocation();
	        double oreCurrentLocation = rc.senseOre(currentLocation);
//			if(rc.getSupplyLevel() > 15) supplied = true;
//			if(rc.getSupplyLevel() < 15 && supplied){
//				int endChannel = rc.readBroadcast(SUPPLIER_END_QUEUE_CHAN);
//				rc.broadcast(SUPPLIER_NEEDED, 1);
//				System.out.println("start channel" + rc.readBroadcast(SUPPLIER_START_QUEUE_CHAN));
//				System.out.println("end channel" + endChannel);
//				rc.broadcast(endChannel, rc.getLocation().x);
//				rc.broadcast(endChannel+1, rc.getLocation().y);
//				rc.broadcast(SUPPLIER_END_QUEUE_CHAN, endChannel+2);
//				supplied = false;
//			}
	        
	        RobotInfo[] enemiesAround = rc.senseNearbyRobots(24,theirTeam);
			int numMiners = numMiners(enemiesAround);
			int currentNumMiners = rc.readBroadcast(NUM_MINERS_IN_POSITION);
			if(numMiners>= currentNumMiners){
				rc.broadcast(MINERS_TO_ATTACK_X, currentLocation.x);
				rc.broadcast(MINERS_TO_ATTACK_Y, currentLocation.y);
				rc.broadcast(NUM_MINERS_IN_POSITION, numMiners);
			}
	        
			if(rc.isCoreReady()) {
			    if (rc.senseOre(rc.getLocation())>4 && rc.canMine()) {
                    rc.mine();
                } else if (enemyRobots.length > 0 && rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                } else {
                    Direction[] directions = getDirectionsAway(this.myHQ);
                    Direction direction = moveToMaxOrRandom(directions);
                    if(rc.canMove(direction) && senseNearbyTowers(currentLocation, direction)==0) {
                        rc.move(direction);
                    } else {
                        int rv = RobotPlayer.rand.nextInt(8);
                        Direction randomDirection = RobotPlayer.directions[rv];
                        if (senseNearbyTowers(currentLocation, randomDirection)==0) {
                            if (rc.canMove(randomDirection)) {
                                rc.move(randomDirection);
                            }
                        }
                    }
                }			    
			}
			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
			transferSpecificSupplies(RobotType.MINER, rc, nearbyAllies);
			rc.broadcast(MINER_CURRENT_CHAN, rc.readBroadcast(MINER_CURRENT_CHAN)+1);
			
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

    private Direction moveToMaxOrRandom(Direction[] dirs) throws GameActionException {
        MapLocation myLocation = rc.getLocation();      
        int fate = RobotPlayer.rand.nextInt(5);
        Direction dir = dirs[fate];
        Collections.shuffle(Arrays.asList(directions));
        
        for (Direction direction : directions) {
            if (!rc.canMove(dir) || (rc.canMove(direction)&& rc.senseOre(myLocation.add(direction))> rc.senseOre(myLocation.add(dir)))) {
                dir = direction;
            }
        }
        return dir;
    }
    	
//	private void minerMoveAwayFromHQ() {
//		Direction dir = getDirectionAwayFromHQ();
//		MapLocation startingLocation = rc.getLocation();
//		try {
//			tryMinerMove(dir);
//			
//			if(rc.getLocation() == startingLocation) {
//				tryMinerMove(dir.opposite());
//			}
//			
//		} catch (GameActionException e) {
//			e.printStackTrace();
//		}
//	}

	private Direction getDirectionAwayFromHQ() {
		return (rc.getLocation().directionTo(rc.senseHQLocation()).opposite());
	}
	
//    private static void tryMinerMove(Direction d) throws GameActionException {
//        int offsetIndex = 0;
//        int[] offsets = {0,1,-1,2,-2};
//        int dirint = directionToInt(d);
//        MapLocation myLocation = rc.getLocation();
//        
//        while ((offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8]))
//        	|| isLocationInEnemyTerritory(myLocation.add(directions[(dirint+offsets[offsetIndex]+8)%8]))){
//            offsetIndex++;
//            System.out.println("isLocationInEnemyTerritory:" + isLocationInEnemyTerritory(myLocation.add(directions[(dirint+offsets[offsetIndex]+8)%8])));
//        }
//        if (offsetIndex < 5) {
//            rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
//        } else tryMinerMove(d.opposite());
//    }
    
    static int directionToInt(Direction d) {
        switch(d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }
    
//	private static void minerMoveAround() throws GameActionException {
//		Random generator = new Random();
//		Direction nextDir = directions[generator.nextInt(8)];
//		
//		if(rand.nextDouble()<0.05){
//			if(rand.nextDouble()<0.5) nextDir = nextDir.rotateLeft();
//			else nextDir = nextDir.rotateRight();
//		}
//		
//		//check that we are not facing off the edge of the map
//		if(rc.senseTerrainTile(rc.getLocation().add(nextDir))!=TerrainTile.NORMAL){
//			nextDir = nextDir.rotateLeft();
//		}
//		
//		//try to move in the facing direction
//		if(rc.isCoreReady()&&rc.canMove(nextDir)){
//			rc.move(nextDir);
//		}
//	}
	
//	private static void moveToMaxAdjacentOre() throws GameActionException {
//		List<MapLocation> adjacentLocations = new ArrayList<MapLocation>();
//		MapLocation myLocation = rc.getLocation();
//		MapLocation maxAdjacentOreLocation = myLocation.add(directions[0]);
//		
//		for(int index = 1; index < 8; index ++){
//			adjacentLocations.add(myLocation.add(directions[index]));
//		}
//		
//		for(MapLocation otherLocation : adjacentLocations) {
//			if( rc.senseOre(otherLocation) > rc.senseOre(maxAdjacentOreLocation) ) {
//				maxAdjacentOreLocation = otherLocation;
//			}
//		}
//		tryMinerMove(myLocation.directionTo(maxAdjacentOreLocation));
//	}
//	
	private static void attackEnemyZero() throws GameActionException {
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),rc.getType().attackRadiusSquared,rc.getTeam().opponent());
		if(nearbyEnemies.length>0){//there are enemies nearby
			//try to shoot at them
			//specifically, try to shoot at enemy specified by nearbyEnemies[0]
			if(rc.isWeaponReady()&&rc.canAttackLocation(nearbyEnemies[0].location)){
				rc.attackLocation(nearbyEnemies[0].location);
			}
		}
	}
}
