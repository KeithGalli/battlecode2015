package drone_missle_strategy;

import drone_missle_strategy.MINERRobot;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MINERFACTORYRobot extends BaseRobot {




	public MINERFACTORYRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			if(rc.isCoreReady()){
			    if (rc.readBroadcast(MINER_PREVIOUS_CHAN)<25 && rc.getTeamOre() > 50) {
			        Direction spawnDirection = getSpawnDirection(RobotType.MINER);
			        if (spawnDirection != null)
			            rc.spawn(spawnDirection, RobotType.MINER);
			    }
			}
	        rc.broadcast(MINER_FACT_CURRENT_CHAN, rc.readBroadcast(MINER_FACT_CURRENT_CHAN)+1);
	        rc.yield();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
}
