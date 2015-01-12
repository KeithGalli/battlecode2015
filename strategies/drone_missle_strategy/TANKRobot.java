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
			int numTowerProtecting = numTank % (towers.length+1);
			MapLocation locationToProtect;
			if(numTowerProtecting == towers.length){
				locationToProtect = rc.senseHQLocation();
			} else{
				locationToProtect = towers[numTowerProtecting];
			}
			if (getEnemiesInAttackingRange().length>0 && rc.isWeaponReady()) {
                    attackLeastHealthEnemy(getEnemiesInAttackingRange());
			}else if(rc.isCoreReady()){
				if(locationToProtect.distanceSquaredTo(rc.getLocation())> 2){
					RobotPlayer.tryMove(rc.getLocation().directionTo(locationToProtect));
				} else{
					RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);
				}
			}
			if(rc.getSupplyLevel() > 30){
				transferSupplies(rc);
			}
			
			rc.broadcast(TANK_CURRENT_CHAN, numTank+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
