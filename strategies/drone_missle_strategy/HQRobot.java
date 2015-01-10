package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {




	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);

		//Init Systems//
		NavSystem.init(rc);




	}

	@Override
	public void run() {
		try {

			//
			// CODE HERE
			//

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
