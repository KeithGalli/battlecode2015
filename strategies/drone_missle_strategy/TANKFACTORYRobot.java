package drone_missle_strategy;

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
		try {
		    int droneCount = rc.readBroadcast(DRONE_PREVIOUS_CHAN);
            int tankCount = rc.readBroadcast(TANK_PREVIOUS_CHAN);
            int basherCount = rc.readBroadcast(BASHER_PREVIOUS_CHAN);
            if ((tankCount < 10 || (withinRange(tankCount, droneCount, 1, 0.3)&&withinRange(tankCount,basherCount, 1, 0.3)) || rc.getTeamOre()>=455) && rc.isCoreReady() && rc.getTeamOre()>250) {
                Direction newDir =  getSpawnDirection(RobotType.TANK);
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.TANK);
                }
            }
			rc.broadcast(TANK_FACT_CURRENT_CHAN, rc.readBroadcast(TANK_FACT_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
