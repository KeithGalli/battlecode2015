package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class AEROSPACELABRobot extends BaseRobot {




	public AEROSPACELABRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
		    if (rc.isCoreReady() && rc.getTeamOre()>400) {
                Direction newDir =  getSpawnDirection(RobotType.LAUNCHER);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.LAUNCHER);
                }
            }
		    rc.yield();
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
