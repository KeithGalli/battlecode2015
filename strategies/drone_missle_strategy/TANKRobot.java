package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class TANKRobot extends BaseRobot {




	public TANKRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			int numTank = rc.readBroadcast(TANK_CURRENT_CHAN);
			MapLocation[] towers = rc.senseTowerLocations();
			int numTowerProtecting = numTank % towers.length;
			MapLocation locationToProtect = towers[numTowerProtecting];
			if(rc.isCoreReady()){
				if (getEnemiesInAttackingRange().length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
	                }
				} else if(locationToProtect.distanceSquaredTo(rc.getLocation())> 7){
					RobotPlayer.tryMove(rc.getLocation().directionTo(locationToProtect));
				} else{
					RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
				}
			} 
			rc.broadcast(TANK_CURRENT_CHAN, numTank+1);

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
