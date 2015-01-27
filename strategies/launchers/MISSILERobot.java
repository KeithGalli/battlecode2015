package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class MISSILERobot extends BaseRobot {


	public MapLocation towerClosest;

	public MISSILERobot(RobotController rc) throws GameActionException {
		super(rc);
		towerClosest = getClosestTower();
	}

	@Override
	public void run() {
		try {
			RobotInfo[] enemiesInAttackingRange = rc.senseNearbyRobots(24,theirTeam);
//			RobotInfo[] alliesInAttackingRange = rc.senseNearbyRobots(5,myTeam);
//			RobotInfo[] enemiesInExplodingRange = rc.senseNearbyRobots(2,theirTeam);
//			RobotInfo[] alliesInExplodingRange = rc.senseNearbyRobots(2,myTeam);
//			if(enemiesInExplodingRange.length > 0 && alliesInExplodingRange.length ==0){
//				rc.explode();
//			}
			MapLocation[] towers = rc.senseEnemyTowerLocations();
			MapLocation location = rc.getLocation();
			int furthestDist = location.distanceSquaredTo(enemiesInAttackingRange[0].location);
			//RobotInfo closestRob = enemiesInAttackingRange[0];
//			boolean tower = false;
//			MapLocation locTower = null;
//			for(MapLocation loc : towers){
//				if(location.distanceSquaredTo(loc) <=25){
//					locTower = loc;
//					tower = true;
//					break;
//				}
//			}
//			if(tower && rc.isCoreReady()){
//				rc.move(location.directionTo(locTower));
//			}
//			for(RobotInfo ri : enemiesInAttackingRange){
//				int distance = ri.location.distanceSquaredTo(location);
////				if(distance < closestDist && !rc.isLocationOccupied(location.add(location.directionTo(ri.location)))){
//				if(distance > furthestDist){
//					closestRob = ri;
//					furthestDist = distance;
//				}
//			}
			if(enemiesInAttackingRange.length ==0){
				rc.setIndicatorString(0,"no enemies");
//				if(towerClosest == null){
//					
//					MapLocation locTower = towers[0];
//					for(MapLocation loc : towers){
//						if(location.distanceSquaredTo(loc) <=25){
//							locTower = loc;
//							towerClosest = loc;
//							break;
//						}
//					}
//					if(rc.isCoreReady()){
//						rc.move(location.directionTo(locTower));
//					}
//				} else {
//					
//					if(rc.isCoreReady()){
//						rc.move(location.directionTo(towerClosest));
//					}
//				}
				if(rc.isCoreReady()){
					rc.move(location.directionTo(towerClosest));
				}
			}
			else if(rc.isCoreReady()){
				rc.setIndicatorString(0,"enemies");
				RobotInfo closestRob = enemiesInAttackingRange[0];
				rc.move(location.directionTo(closestRob.location));
			}

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
