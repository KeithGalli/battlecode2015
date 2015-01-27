package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TRAININGFIELDRobot extends BaseRobot {




	public TRAININGFIELDRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			if (rc.hasSpawnRequirements(RobotType.COMMANDER)) {
                Direction newDir =  getSpawnDirection(RobotType.COMMANDER);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.COMMANDER);
                }
            }

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
