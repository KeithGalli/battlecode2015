package drone_missle_strategy;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {	
	public Team opp;
	public Team team;


	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);
		team = rc.getTeam();
		opp = team.opponent();
		//Init Systems//
		NavSystem.init(rc);
	}

	@Override
	public void run() {
		try {
		    int numMiners = rc.readBroadcast(MINER_CURRENT_CHAN);
		    rc.broadcast(MINER_CURRENT_CHAN, 0);
		    
		    int numSoldiers = rc.readBroadcast(SOLDIER_CURRENT_CHAN);
            rc.broadcast(SOLDIER_CURRENT_CHAN, 0);
            int numBashers = rc.readBroadcast(BASHER_CURRENT_CHAN);
            rc.broadcast(BASHER_CURRENT_CHAN, 0);
            int numBeavers = rc.readBroadcast(BEAVER_CURRENT_CHAN);
            rc.broadcast(BEAVER_CURRENT_CHAN, 0);
            
            rc.broadcast(MINER_PREVIOUS_CHAN, numMiners);
            rc.broadcast(BEAVER_PREVIOUS_CHAN, numBeavers);
            rc.broadcast(SOLDIER_PREVIOUS_CHAN, numSoldiers);
            rc.broadcast(BASHER_PREVIOUS_CHAN, numBashers);



            if (rc.isCoreReady() && rc.getTeamOre() >= 100 && rc.readBroadcast(BEAVER_PREVIOUS_CHAN)<8) {
                RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
            }
            rc.yield();
			
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
