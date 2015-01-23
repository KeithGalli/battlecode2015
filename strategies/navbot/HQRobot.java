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

            //ARBITRARY 100 RD Counter

        	if (broadcastCount > 100){
        		broadcastCount = 0;


                //senseQueueHQ: [(x1,y1), (x2,y2)] of locations that a robot has been at
        		for (MapLocation loc: MapEngine.senseQueueHQ){
        			
                    //If its a new location
                    if (!MapEngine.prevSensedLocs.contains(loc)){
                        //Get all visible tiles from that location
                        testRobotSeenLocs = MapEngine.unitScan(loc);
                        //Scan all visible tiles from that location
                        MapEngine.scanTiles(testRobotSeenLocs);
                        //Add that location to previously seen locations
                        MapEngine.prevSensedLocs.add(loc);
                    }
        			

        		}

                //Prepare the map for broadcasting.
        		MapEngine.resetMapAndPrep();

                //Send the map data dict
        		BroadcastSystem.prepareandsendMapDataDict(MapEngine.sensedDictHQ);
        		//Send the waypoint data dict
        		BroadcastSystem.prepareandsendWaypointDict(MapEngine.waypointDictHQ);
                
                //Tell the robot the dictionaries are ready for download.
                BroadcastSystem.write(2001, 1);
        	}


            //Get the test robots internal loc 
            //REMEMBER ALL BROADCAST LOCATIONS ARE INTERNAL
        	testRobotInternalLoc = Functions.intToLoc(rc.readBroadcast(TESTCHANNEL));

            //Convert to real maplocation
        	testRobotLoc = Functions.internallocToLoc(testRobotInternalLoc);

            //If the robot is at a new location
            if(!MapEngine.prevSensedLocs.contains(testRobotLoc)){

                //if the location is not it's last one
            	if (prevRobotLoc.x==testRobotLoc.x && prevRobotLoc.y==testRobotLoc.y){
            		
            	} else if (prevRobotLoc.x != -1 && prevRobotLoc.y != -1){
                    //Add it's location to the queue
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
