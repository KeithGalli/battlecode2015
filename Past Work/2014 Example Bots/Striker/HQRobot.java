package Striker;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
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
 //                   DataCache.updateRoundVariables();
            		if ((Integer)BroadcastSystem.read(jobChannel)==null){
            			BroadcastSystem.write(jobChannel, 0);
            		}
                    if (rc.isActive()) {
                                    spawnSoldier();
                            }
                    
            } catch (Exception e) {
//                    System.out.println("caught exception before it killed us:");
//                    System.out.println(rc.getRobot().getID());
//                    e.printStackTrace();
            }
    }
    
//    public void persistRetreatChannel() {
//            Message msg = BroadcastSystem.readLastCycle(ChannelType.RETREAT_CHANNEL);
//            if (msg.isValid && msg.body == Constants.RETREAT) { // if needs to be persisted
//                    BroadcastSystem.write(ChannelType.RETREAT_CHANNEL, Constants.RETREAT);
//            }
//    }
    
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
            // Otherwise, let's just spawn in the desired direction, and make sure to clear out a path later
            return canMoveDirection;
    }

}
