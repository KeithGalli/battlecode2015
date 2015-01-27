package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class MISSILERobot extends BaseRobot {




	public MISSILERobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
			RobotInfo[] enemiesInAttackingRange = rc.senseNearbyRobots(24,theirTeam);
			RobotInfo[] alliesInAttackingRange = rc.senseNearbyRobots(5,myTeam);
			RobotInfo[] enemiesInExplodingRange = rc.senseNearbyRobots(2,theirTeam);
			RobotInfo[] alliesInExplodingRange = rc.senseNearbyRobots(2,myTeam);
			if(enemiesInExplodingRange.length > 0 && alliesInExplodingRange.length ==0){
				rc.explode();
			}
			MapLocation location = rc.getLocation();
			int furthestDist = location.distanceSquaredTo(enemiesInAttackingRange[0].location);
			RobotInfo closestRob = enemiesInAttackingRange[0];
//			for(RobotInfo ri : enemiesInAttackingRange){
//				int distance = ri.location.distanceSquaredTo(location);
////				if(distance < closestDist && !rc.isLocationOccupied(location.add(location.directionTo(ri.location)))){
//				if(distance > furthestDist){
//					closestRob = ri;
//					furthestDist = distance;
//				}
//			}
			if(rc.isCoreReady()){
				rc.move(location.directionTo(closestRob.location));
			}

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
