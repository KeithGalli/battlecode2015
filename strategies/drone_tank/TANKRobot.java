package drone_tank;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TANKRobot extends BaseRobot {
    
    public static Direction tarDir;




	public TANKRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
	}

	@Override
	public void run() {
		try {
		    DataCache.updateRoundVariables();
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.TANK);
            MapLocation currentLocation = rc.getLocation();
            double supplyLevel = rc.getSupplyLevel();
            if (enemyRobots.length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                }
            }
            if (Clock.getRoundNum() < 1400) {
                if (rc.isCoreReady()) {
                    if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) {
                        NavSystem.dumbNav(this.myHQ);
                    } else {
                        MapLocation ourClosest = getOurClosestTowerToThem();
                        NavSystem.dumbNav(ourClosest);
                    }
                }
            } else {
                if (rc.isCoreReady()) {
                    MapLocation closest  = getClosestTower();
                    if (closest != null) {                        
                        NavSystem.dumbNav(closest);
                    } else {
                        NavSystem.dumbNav(DataCache.enemyHQ);
                    }
                } 
            }
            
            transferSpecificSupplies(RobotType.TANK, rc);
            rc.broadcast(TANK_CURRENT_CHAN, rc.readBroadcast(TANK_CURRENT_CHAN)+1);


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
