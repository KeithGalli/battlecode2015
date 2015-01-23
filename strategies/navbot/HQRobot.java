package navbot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {

	public static int count= 0;
	public static int broadcastCount = 0;

	public static MapLocation prevRobotLoc = new MapLocation(-1, -1);

	public static MapLocation testRobotLoc;
	public static MapLocation testRobotInternalLoc;
	public static MapLocation[] testHQLocs;
	public static MapLocation[] testRobotSeenLocs;
	public static int broadcastReady;

	public static int[][] testmap;


	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);

		//Init Systems//
		NavSystem.HQinit(rc);
		MapEngine.HQinit(rc);
		BroadcastSystem.write(2001, 0);
		testHQLocs = MapEngine.structScan(rc.getLocation());
		MapEngine.scanTiles(testHQLocs);

	}

	@Override
	public void run() {
		try {

			

			broadcastReady = BroadcastSystem.read(2001);


			if (rc.isCoreReady() && rc.getTeamOre() >= 100 && count ==0) {
				rc.setIndicatorString(0, "trying to spawn");
				rc.spawn(rc.getLocation().directionTo(DataCache.enemyHQ),RobotType.BEAVER);
				count = 1;
        	}


        	if (broadcastCount > 100){
        		broadcastCount = 0;



        		for (MapLocation loc: MapEngine.senseQueueHQ){
        			
                    if (!MapEngine.prevSensedLocs.contains(loc)){
                        
                        testRobotSeenLocs = MapEngine.unitScan(loc);
                        MapEngine.scanTiles(testRobotSeenLocs);
                        MapEngine.prevSensedLocs.add(loc);
                    }
        			

        		}


        		MapEngine.resetMapAndPrep();

        		BroadcastSystem.prepareandsendMapDataDict(MapEngine.sensedDictHQ);
        		
        		BroadcastSystem.prepareandsendWaypointDict(MapEngine.waypointDictHQ);
                
                BroadcastSystem.write(2001, 1);
        	}



        	testRobotInternalLoc = Functions.intToLoc(rc.readBroadcast(TESTCHANNEL));

        	testRobotLoc = Functions.internallocToLoc(testRobotInternalLoc);

            if(!MapEngine.prevSensedLocs.contains(testRobotLoc)){

            	if (prevRobotLoc.x==testRobotLoc.x && prevRobotLoc.y==testRobotLoc.y){
            		
            	} else if (prevRobotLoc.x != -1 && prevRobotLoc.y != -1){
            		MapEngine.senseQueueHQ.add(testRobotLoc);
            	}
            }
        	prevRobotLoc = testRobotLoc;
        	broadcastCount++;

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
