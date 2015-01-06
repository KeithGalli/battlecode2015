package dumbass;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class HQRobot extends BaseRobot {
	
	static MapLocation rallyPoint;

	
	


    public HQRobot(RobotController rc) throws GameActionException {
            super(rc);
            rc.broadcast(101,DataCache.locToInt(DataCache.mldivide(rc.senseHQLocation(),bigBoxSize)));//this tells soldiers to stay near HQ to start
			rc.broadcast(102,-1);//and to remain in squad 1
			rc.broadcast(pastrLocationChannel, 0);
			spawnSoldier();
			NavSystem.HQinit(rc, bigBoxSize);
			rallyPoint = DataCache.mladd(DataCache.mldivide(DataCache.mlsubtract(rc.senseEnemyHQLocation(),rc.senseHQLocation()),3),rc.senseHQLocation());

    		BroadcastSystem.findPathAndBroadcast(1,rc.getLocation(),rallyPoint,bigBoxSize,2);
    		
            

    }
    
    @Override
    public void run() {
            try {
            	
//            		if ((Integer)BroadcastSystem.read(assignGroup)==null){ //if game just started
//            			BroadcastSystem.write(assignGroup, groupchannel);
//            		}
            	
            		DataCache.updateRoundVariables();
            		BroadcastSystem.findPathAndBroadcast(1,rc.getLocation(),rallyPoint,bigBoxSize,2);

            		rc.setIndicatorString(0, Integer.toString(DataCache.numEnemyPastrs));
            		
            		if(DataCache.enemyPastrs.length>0){
            			BroadcastSystem.findPathAndBroadcast(2,rallyPoint,DataCache.enemyPastrs[0],bigBoxSize,2);//for some reason, they are not getting this message
            		}

                    if (DataCache.numEnemyRobots>0){
                    	int[] closestEnemyInfo = DataCache.getClosestEnemy(DataCache.enemyRobots);
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
