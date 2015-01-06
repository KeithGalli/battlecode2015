package strikerBasic;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SoldierRobot extends BaseRobot {
	
	public SoldierJob job = SoldierJob.RALLY;
	public SoldierJob nextjob;
	public MapLocation rallyPoint;
	public int groupChannel;
	public int unseenEnemyLoc;


	public SoldierRobot(RobotController rc) {
		super(rc);
		rallyPoint = findRallyPoint();
		NavSystem.init(this);

		
//		if ((Integer) groupChannel == null){
//			groupChannel = BroadcastSystem.read(assignGroup);
//		}
		
	}

	@Override
	public void run() {
		try {
			
            DataCache.updateRoundVariables();	
            //rc.setIndicatorString(0, Integer.toString(DataCache.numEnemyRobots));
            rc.setIndicatorString(0, Integer.toString(rc.getLocation().distanceSquaredTo(DataCache.enemyHQLocation)) );
            DataCache.receivePastrs(allChannel);
//            unseenEnemyLoc = BroadcastSystem.read(groupChannel);
//            /////???????
//            if (unseenEnemyLoc != 0){
//            	job = SoldierJob.FIGHT;
//            }
            ////////
			switch (job){

			case RALLY:
				rallyingCode();
				break;
				
			case HUNT:
				huntCode();
				break;
			
			
			case FIGHT:
				fightCode();
				break;
			}
				
			if (nextjob != null) {
				job = nextjob;
                nextjob = null; // clear the state for the next call of run() to use
            }
		} catch (Exception e) {
			//			                    System.out.println("caught exception before it killed us:");
			//			                    System.out.println(rc.getRobot().getID());
			//			                    e.printStackTrace();
		}
	}
	
	public void huntCode() throws GameActionException {
		if (DataCache.numEnemyRobots > 0) {
            nextjob = SoldierJob.FIGHT;
           // BroadcastSystem.write(groupChannel, 1);
            fightCode();
		}
		else if(DataCache.numEnemyPastures == 0){
			NavSystem.goToLocation(DataCache.enemyHQLocation);
		}
		else{
			NavSystem.goToLocation(DataCache.closestPastr);
		}
		
	}


	public void rallyingCode() throws GameActionException {
        
                        // If there are enemies nearby, trigger FIGHTING SoldierJOB
                        if (DataCache.numEnemyRobots > 0) {
                                nextjob = SoldierJob.FIGHT;
                               // BroadcastSystem.write(groupChannel, 1);
                                fightCode();
                        

                        } else if(DataCache.numNearbyAlliedSoldiers>=5){
                        		nextjob = SoldierJob.HUNT;
                        		huntCode();
                        	
                        } else{
                        	rc.setIndicatorString(1, Integer.toString(DataCache.locToInt(rallyPoint)) );
                        	NavSystem.goToLocation(rallyPoint);
                        }
        }
	
	public void fightCode() throws GameActionException {
        if (DataCache.numEnemyRobots == 0) {
                nextjob = SoldierJob.HUNT;
                huntCode();
        } else {
                // Otherwise, just keep fighting
                aggressiveMicroCode();
        }
}

    public void aggressiveMicroCode() throws GameActionException {
            int[] closestEnemyInfo = DataCache.getClosestEnemy(DataCache.nearbyEnemyRobots);
            MapLocation closestEnemyLocation = new MapLocation(closestEnemyInfo[1], closestEnemyInfo[2]);

            if(closestEnemyLocation.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
				rc.setIndicatorString(1, "trying to shoot");
				if(rc.isActive()){
					rc.attackSquare(closestEnemyLocation);
				}
            }
            
			else {
				Direction desireddir = rc.getLocation().directionTo(closestEnemyLocation);
					NavSystem.simpleMove(desireddir);
				}
            
    }
    




	private MapLocation findRallyPoint() {
        MapLocation enemyLoc = DataCache.enemyHQLocation;
        MapLocation ourLoc = DataCache.ourHQLocation;
        int x, y;
        x = (enemyLoc.x + 3 * ourLoc.x) / 4;
        y = (enemyLoc.y + 3 * ourLoc.y) / 4;
        return new MapLocation(x,y);
}

}
