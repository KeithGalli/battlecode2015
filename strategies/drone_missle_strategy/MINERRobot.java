package drone_missle_strategy;

import java.util.Random;

import battlecode.common.*;

public class MINERRobot extends BaseRobot {
	
	static Random rand = new Random();
	public final static int MINER_COST = 50;
	private static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	private static Direction[] directionsForGroupZero = {Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST};
	private static Direction[] directionsForGroupOne = {Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST};
	private static Direction[] directionsForGroupTwo = {Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST};
	private static Direction[] directionsForGroupThree = {Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	private static Direction[][] groupDirections = {directionsForGroupZero, directionsForGroupOne, directionsForGroupTwo, directionsForGroupThree};
	
	public MINERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		int roundNum = Clock.getRoundNum();
		Random randDouble = new Random();
		Random randInt = new Random();
		
		try {
			//assign the miner a group movement number
			int minerNum = rc.readBroadcast(MINER_CURRENT_CHAN);
			int minerGroupNum = minerNum % 4;
			
			if(rc.isCoreReady()) {
			
				//mine if logical (ore > 4) and possible
				if(rc.senseOre(rc.getLocation()) >= 4 && rc.canMine()) rc.mine();
				
				//else if enemies in range, attack
				else if (getEnemiesInAttackingRange().length > 0 && rc.isWeaponReady()) attackLeastHealthEnemy(getEnemiesInAttackingRange());
				
				else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 100) tryMinerMove(groupDirections[minerGroupNum][(int) (Math.random()*3)]);
				
				else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 400) {
					if(randDouble.nextDouble() < 0.3) tryMinerMove(directions[(int) (Math.random()*3)]);
					else tryMinerMove(groupDirections[minerGroupNum][(int) (Math.random()*3)]);
				}
				else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 625) {
					if(randDouble.nextDouble() < 0.5) tryMinerMove(directions[(int) (Math.random()*3)]);
					else tryMinerMove(groupDirections[minerGroupNum][(int) (Math.random()*3)]);
				}
				else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 900) {
					if(randDouble.nextDouble() < 0.7) tryMinerMove(directions[(int) (Math.random()*3)]);
					else tryMinerMove(groupDirections[minerGroupNum][(int) (Math.random()*3)]);
				}
				else tryMinerMove(directions[(int) (Math.random()*8)]);
			}
			transferMinerSupplies(rc);
			rc.broadcast(MINER_CURRENT_CHAN, rc.readBroadcast(MINER_CURRENT_CHAN)+1);
			
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

	private void minerMoveAwayFromHQ() {
		Direction dir = getDirectionAwayFromHQ();
		MapLocation startingLocation = rc.getLocation();
		try {
			tryMinerMove(dir);
			
			if(rc.getLocation() == startingLocation) {
				tryMinerMove(dir.opposite());
			}
			
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

	private Direction getDirectionAwayFromHQ() {
		return (rc.getLocation().directionTo(rc.senseHQLocation()).opposite());
	}
	
    private void tryMinerMove(Direction d) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2};
        int dirint = directionToInt(d);
        while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 5) {
            rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
        }
    }
    
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
    
	private static void minerMoveAround() throws GameActionException {
		Random generator = new Random();
		Direction nextDir = directions[generator.nextInt(8)];
		
		if(rand.nextDouble()<0.05){
			if(rand.nextDouble()<0.5) nextDir = nextDir.rotateLeft();
			else nextDir = nextDir.rotateRight();
		}
		
		//check that we are not facing off the edge of the map
		if(rc.senseTerrainTile(rc.getLocation().add(nextDir))!=TerrainTile.NORMAL){
			nextDir = nextDir.rotateLeft();
		}
		
		//try to move in the facing direction
		if(rc.isCoreReady()&&rc.canMove(nextDir)){
			rc.move(nextDir);
		}
	}
	
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
