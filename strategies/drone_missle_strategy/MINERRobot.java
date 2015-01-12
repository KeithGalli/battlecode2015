package drone_missle_strategy;

import java.util.Random;

import battlecode.common.*;

public class MINERRobot extends BaseRobot {
	
	static Random rand;
	public final static int MINER_COST = 50;
	private static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	
	public MINERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		int roundNum = Clock.getRoundNum();
		double randDouble = Math.random();
		
		try {
			if(rc.isCoreReady()) {
				
				//ROUNDS 0->120: simply move away from HQ with .5 probability
				if(roundNum < 120) {
					if(randDouble > 0.5 && rc.senseOre(rc.getLocation()) >= 4 && rc.canMine()) {
						rc.mine();
					} else {
						minerMoveAwayFromHQ();
					}
				//ROUNDS 120-1000: mine if possible, otherwise move AWAY from HQ 
				} else if(roundNum > 80 && roundNum < 1000){
					if(rc.senseOre(rc.getLocation()) >= 4 && rc.canMine()) {
						rc.mine();
					} else {
						minerMoveAwayFromHQ();
					}
				//ROUNDS 1000+: mine if possible, otherwise move randomly and attack (enemy zero)
				} else {
					if(rc.senseOre(rc.getLocation()) >= 4 && rc.canMine()) {
						rc.mine();
					} else {
						attackEnemyZero();
						minerMoveAround();
					}
				}
				
			}
		} catch (Exception e) {
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
