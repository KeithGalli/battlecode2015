package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HELIPADRobot extends BaseRobot {




	public HELIPADRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
		    if (rc.isCoreReady() && rc.getTeamOre()>125) {
                Direction newDir =  getSpawnDirection(RobotType.DRONE);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.DRONE);
                }
		    }
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
