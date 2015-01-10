package tank_strategy;

import battlecode.common.*;

public class MINERFACTORYRobot extends BaseRobot {




	public MINERFACTORYRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		while(true) {
			Direction[] dirs = Direction.values();
			int count = 0;
			int index = 0;
			
			//spawn a Miner if able to
			while(count == 0 && index <= 7){
				if(rc.isCoreReady() && rc.canSpawn(dirs[index], RobotType.MINER) && rc.senseTeamOre() > 50){
					rc.spawn(dirs[index], RobotType.MINER);
					count++;
				}
				index++;
			}
			
			rc.yield();
		}
	}
}
