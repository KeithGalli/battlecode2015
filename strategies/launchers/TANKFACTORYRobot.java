package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TANKFACTORYRobot extends BaseRobot {




	public TANKFACTORYRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
	    //int tanksBuilt 
	    
		try {
            if (rc.hasSpawnRequirements(RobotType.TANK)) {
                Direction newDir =  getSpawnDirection(RobotType.TANK);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.TANK);
                }
            }

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
