package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class BARRACKSRobot extends BaseRobot {




	public BARRACKSRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			if (rc.hasSpawnRequirements(RobotType.SOLDIER) && Clock.getRoundNum() < 700 && rc.readBroadcast(SOLDIER_PREVIOUS_CHAN)<20) {
                Direction newDir =  getSpawnDirection(RobotType.SOLDIER);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.SOLDIER);
                }
            }

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
