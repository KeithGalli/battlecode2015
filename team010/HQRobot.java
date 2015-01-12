package team010;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {

	public static int count= 0;


	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);

		//Init Systems//
		NavSystem.HQinit(rc);


		



	}

	@Override
	public void run() {
		try {

			if (rc.isCoreReady() && rc.getTeamOre() >= 100 && count ==0) {
				rc.setIndicatorString(0, "trying to spawn");
				rc.spawn(rc.getLocation().directionTo(DataCache.enemyHQ),RobotType.BEAVER);
				count = 1;
	            	//RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
        	}


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
