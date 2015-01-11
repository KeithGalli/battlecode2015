package drone_missle_strategy;

import battlecode.common.*;

public class MINERRobot extends BaseRobot {

	public static int oreCost = 50;
	
	public MINERRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			while(true) {
				if(rc.isCoreReady()) {
					
					if(rc.senseOre(rc.getLocation()) >= 4 && rc.canMine()) {
						rc.mine();
					} else {
						moveAwayFromHQ();
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveAwayFromHQ() {
		Direction dir = getDirectionAwayFromHQ();
		try {
			RobotPlayer.tryMove(dir);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

	private Direction getDirectionAwayFromHQ() {
		return (rc.getLocation().directionTo(rc.senseHQLocation()).opposite());
	}
}
