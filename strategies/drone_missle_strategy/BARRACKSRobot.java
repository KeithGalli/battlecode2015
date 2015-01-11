package drone_missle_strategy;

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
            int droneCount = rc.readBroadcast(DRONE_PREVIOUS_CHAN);
            int tankCount = rc.readBroadcast(TANK_PREVIOUS_CHAN);
            int basherCount = rc.readBroadcast(BASHER_PREVIOUS_CHAN);
            if (basherCount < 10 || (withinRange(basherCount, tankCount, 2, 0.2)&&withinRange(tankCount,basherCount, .5, .2)) && rc.isCoreReady() && rc.getTeamOre()>250) {
                Direction newDir =  getSpawnDirection(RobotType.TANK);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.TANK);
                }
            }	    		    
			rc.broadcast(BARRACKS_CURRENT_CHAN, rc.readBroadcast(BARRACKS_CURRENT_CHAN)+1);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
