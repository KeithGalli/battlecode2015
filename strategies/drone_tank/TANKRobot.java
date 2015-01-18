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
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.TANK);
            if (enemyRobots.length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                }
            }
            if (Clock.getRoundNum() < 1000) {
                if (rc.isCoreReady()) {
                    DataCache.updateRoundVariables();
                    MapLocation ourClosest = getOurClosestTowerToThem();
                    tarDir = rc.getLocation().directionTo(ourClosest);
                    NavSystem.dumbNav(ourClosest);

                }
            } else {
                if (rc.isCoreReady()) {
                    MapLocation closest  = getClosestTower();
                    if (closest != null) {
                        DataCache.updateRoundVariables();

                        //rc.broadcast(TESTCHANNEL, Functions.locToInt(DataCache.currentLoc));
                        
                        tarDir = rc.getLocation().directionTo(closest);
                        
                        NavSystem.dumbNav(closest);
                    } else {
                        DataCache.updateRoundVariables();
                        tarDir = rc.getLocation().directionTo(this.theirHQ);
                        NavSystem.dumbNav(this.theirHQ);
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
