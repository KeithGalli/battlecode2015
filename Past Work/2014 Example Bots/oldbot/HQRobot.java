package oldbot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class HQRobot extends BaseRobot {

	

    public HQRobot(RobotController rc) throws GameActionException {
            super(rc);
            BroadcastSystem.write(3, -1);
            BroadcastSystem.write(1, 0);

			spawnConstructionSoldier();
			
			NavSystem.HQinit(rc);
			//System.out.println("printing data");
			//NavSystem.displayArray(NavSystem.mapData);
			//NavSystem.displayArray(NavSystem.voidID);
			//System.out.println(NavSystem.waypointDictHQ);
			//System.out.println("preparing data");
			//int mapdataint = BroadcastSystem.prepareMapDataArray(NavSystem.mapData);
			//System.out.println(mapdataint);
			
			BroadcastSystem.broadcast2MapArrays(mapDataBand, NavSystem.voidID, NavSystem.mapData);
			BroadcastSystem.prepareandsendMapDataDict(NavSystem.waypointDictHQ);
			//System.out.println("broadcasting data");
			BroadcastSystem.write(3, 0);

    }
    
    @Override
    public void run() {
            try {
            	
            		DataCache.updateRoundVariables();

            		rc.setIndicatorString(0, Integer.toString(DataCache.numEnemyPastrs));

                    if (DataCache.numEnemyRobots>0){
                    	int[] closestEnemyInfo = Functions.getClosestEnemy(DataCache.enemyRobots);
                        MapLocation closestEnemyLocation = new MapLocation(closestEnemyInfo[1], closestEnemyInfo[2]);
                        if(closestEnemyLocation.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
            				rc.setIndicatorString(1, "trying to shoot");
            				if(rc.isActive()){
            					rc.setIndicatorString(2, "rcactive");
            					rc.attackSquare(closestEnemyLocation);
            				}
                        }
                    }
                    if (rc.isActive()&&rc.senseRobotCount()<4) {
                    	spawnAnySoldier();
                        }
                    
            } catch (Exception e) {
//                    System.out.println("caught exception before it killed us:");
//                    System.out.println(rc.getRobot().getID());
                    //e.printStackTrace();
            }
    }
    
    public static void spawnAnySoldier() throws GameActionException {
    	if (rc.senseRobotCount()<3){
        	spawnConstructionSoldier();
        	}
        	else{
            spawnSoldier();
        	}
    }
    
    public static void spawnConstructionSoldier() throws GameActionException {    	
        Direction desiredDir = rc.getLocation().directionTo(DataCache.enemyHQLocation).opposite();
        Direction dir = getSpawnDirection(desiredDir);
        if (dir != null) {
                rc.spawn(dir);
        }
}
    
    public static void spawnSoldier() throws GameActionException {    	
            Direction desiredDir = rc.getLocation().directionTo(DataCache.enemyHQLocation);
            Direction dir = getSpawnDirection(desiredDir);
            if (dir != null) {
                    rc.spawn(dir);
            }
    }

    /**
     * helper fcn to see what direction to actually go given a desired direction
     * @param rc
     * @param dir
     * @return
     */
    private static Direction getSpawnDirection(Direction dir) {
            Direction canMoveDirection = null;
            int desiredDirOffset = dir.ordinal();
            int[] dirOffsets = new int[]{4, -3, 3, -2, 2, -1, 1, 0};
            for (int i = dirOffsets.length; --i >= 0; ) {
                    int dirOffset = dirOffsets[i];
                    Direction currentDirection = DataCache.directionArray[(desiredDirOffset + dirOffset + 8) % 8];
                    if (rc.canMove(currentDirection)) {
                            if (canMoveDirection == null) {
                                    canMoveDirection = currentDirection;
                            }
                    }                        
            }
            return canMoveDirection;
    }

}
