package launchers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;


public class HELIPADRobot extends BaseRobot {
    
	
	public HELIPADRobot(RobotController rc) throws GameActionException {
		super(rc);
	}

	@Override
	public void run() {
		try {
//		    int dronesBuilt = rc.readBroadcast(46);
//		    
//            if (dronesBuilt < 6 && rc.hasSpawnRequirements(RobotType.DRONE)) {
//                Direction newDir =  getSpawnDirection(RobotType.DRONE);
//                if (newDir != null) {
//                    rc.spawn(newDir, RobotType.DRONE);
//                    rc.broadcast(46, dronesBuilt+1);
//                }
//            }
//            rc.broadcast(HELIPAD_CURRENT_CHAN, rc.readBroadcast(HELIPAD_CURRENT_CHAN)+1);
//            rc.yield();		    
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
	
}
