package strikerBasicv2;

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
            
            

    }
    
    @Override
    public void run() {
            try {
            	
//            		if ((Integer)BroadcastSystem.read(assignGroup)==null){ //if game just started
//            			BroadcastSystem.write(assignGroup, groupchannel);
//            		}
            	
            		DataCache.updateRoundVariables();
//            		if (DataCache.HQupdate()){
//            			DataCache.broadcastPastrs(allChannel);
//            		}
                    if (DataCache.numEnemyRobots>0){
                    	int[] closestEnemyInfo = DataCache.getClosestEnemy(DataCache.nearbyEnemyRobots);
                        MapLocation closestEnemyLocation = new MapLocation(closestEnemyInfo[1], closestEnemyInfo[2]);
                        if(closestEnemyLocation.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
            				rc.setIndicatorString(1, "trying to shoot");
            				if(rc.isActive()){
            					rc.setIndicatorString(2, "rcactive");
            					rc.attackSquare(closestEnemyLocation);
            				}
                        }
                    }
                    if (rc.isActive()) {
                        spawnSoldier();
                        }
                    
            } catch (Exception e) {
//                    System.out.println("caught exception before it killed us:");
//                    System.out.println(rc.getRobot().getID());
                    e.printStackTrace();
            }
    }
    public void spawnSoldier() throws GameActionException {    	
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
    private Direction getSpawnDirection(Direction dir) {
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
