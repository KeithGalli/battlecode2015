package launchers;

import battlecode.common.*;

public class MINERFACTORYRobot extends BaseRobot {

	public final static int MAX_MINERS = 25;
	public MINERFACTORYRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			if(rc.isCoreReady()){
			    if (rc.readBroadcast(MINER_PREVIOUS_CHAN)< MAX_MINERS && rc.hasSpawnRequirements(RobotType.MINER)) {
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
