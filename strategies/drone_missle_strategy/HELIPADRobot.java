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
		    int droneCount = rc.readBroadcast(DRONE_PREVIOUS_CHAN);
		    int tankCount = rc.readBroadcast(TANK_PREVIOUS_CHAN);
		    int basherCount = rc.readBroadcast(BASHER_PREVIOUS_CHAN);

		    if (droneCount < 25 || (withinRange(droneCount, tankCount, 1, 0.3)&&withinRange(droneCount,basherCount, 1, .3)) && rc.isCoreReady() && rc.getTeamOre()>125) {
                Direction newDir =  getSpawnDirection(RobotType.DRONE);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.DRONE);
                }
		    }
            rc.broadcast(HELIPAD_CURRENT_CHAN, rc.readBroadcast(HELIPAD_CURRENT_CHAN)+1);
            rc.yield();
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
