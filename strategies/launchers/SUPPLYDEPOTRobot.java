package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class SUPPLYDEPOTRobot extends BaseRobot {




	public SUPPLYDEPOTRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			rc.broadcast(SUPPLY_DEPOT_CURRENT_CHAN, rc.readBroadcast(SUPPLY_DEPOT_CURRENT_CHAN)+1);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
