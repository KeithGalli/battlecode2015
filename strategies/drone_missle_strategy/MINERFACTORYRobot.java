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
			while(true) {
				if(rc.isCoreReady()){
					
					if(rc.getTeamOre() > MINERRobot.oreCost && rc.canSpawn(getSpawnDirection(RobotType.MINER), RobotType.MINER)){
							rc.spawn(getSpawnDirection(RobotType.MINER), RobotType.MINER);
							
					}
				}
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
}
