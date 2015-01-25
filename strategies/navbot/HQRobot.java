package navbot;

import java.util.ArrayList;
import java.util.*;
import java.util.List;

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
    public static int oreCount = 0;

    public static int distributeMod = 50000;


	public static MapLocation testRobotLoc;
	public static MapLocation testRobotInternalLoc;
	public static MapLocation[] testHQLocs;
	public static MapLocation[] testRobotSeenLocs;
	public static int broadcastReady;

    public static boolean receivedAllPaths = false;

    public static boolean broadcastMode = false;

    public static boolean waitingOnPaths = false;
    public static List<Integer> receivedPaths = new ArrayList<Integer>();
    public static List<Integer> robotChannels = new ArrayList<Integer>();

	public static int[][] testmap;

    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};



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

			




			if (rc.isCoreReady() && rc.getTeamOre() >= 100 && count ==0) {
				rc.setIndicatorString(0, "trying to spawn");
				trySpawn(rc.getLocation().directionTo(DataCache.enemyHQ),RobotType.BEAVER);
                receivedPaths.add(0);
                robotChannels.add(distributeMod);
                BroadcastSystem.write(DISTRIBUTECHANNEL, distributeMod);
                distributeMod+=200;
				count = 1;
        	}

            //ARBITRARY 100 RD Counter
            // SPLIT ORE COUNT
            if (broadcastCount>300){
                for (int channel: robotChannels){
                    BroadcastSystem.write(channel, 1);
                }
                receivedAllPaths = false;
                broadcastMode = true;
                broadcastCount = 0;
                System.out.println("HQ TEST");
            }

            if (broadcastMode == true){
                if (!receivedAllPaths){
                    receivedAllPaths = true;
                    for (int i=0; i<receivedPaths.size();i++){
                        if (receivedPaths.get(i)==0){
                            //System.out.println("Test");
                            receivedPaths.set(i, BroadcastSystem.receiveLocsDataList(robotChannels.get(i)+5));
                            if (receivedPaths.get(i)==0){
                                receivedAllPaths = false;
                            }
                        }
                    }
                } else {
                    System.out.println("HQ TEST END");
                    System.out.println("HQ TEST 2");
                    MapEngine.resetOreList();
                    //System.out.println(MapEngine.senseQueueHQ);


                    //senseQueueHQ: [(x1,y1), (x2,y2)] of locations that a robot has been at
                    for (MapLocation loc: MapEngine.senseQueueHQ){

                        //MapEngine.rescanOreMapGivenLoc(loc);
                        //System.out.println("HQ TEST CHECK");
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
                    BroadcastSystem.prepareandsendMapDataDict(MapEngine.sensedMAINDictHQ,BroadcastSystem.dataBand);
                    //Send the waypoint data dict
                    BroadcastSystem.prepareandsendWaypointDict(MapEngine.waypointDictHQ);
                    //System.out.println(MapEngine.waypointDictHQ);
                    MapEngine.resetSensedMAINDict();

                    for (int i=0; i<receivedPaths.size();i++){
                        receivedPaths.set(i, 0);
                    }
                    //System.out.println("HQ TEST END");
                    
                    //Tell the robot the dictionaries are ready for download.
                    for (int channel: robotChannels){
                        BroadcastSystem.write(channel, 2);
                    }
                    broadcastMode = false;
                    System.out.println("HQ TEST END");
                }
            } else {
                broadcastCount++;
                oreCount++;
            }
 
            


        	// if (broadcastCount > 300){
         //        System.out.println("HQ TEST");
         //        for (int channel: robotChannels){
         //           // System.out.println("HQBOT");
         //           // System.out.println(channel);
         //            BroadcastSystem.write(channel, 1);
         //        }
         //        System.out.println(receivedPaths);
         //        receivedAllPaths = false;
         //        while(!receivedAllPaths){

         //            receivedAllPaths = true;
         //            for (int i=0; i<receivedPaths.size();i++){
         //                if (receivedPaths.get(i)==0){
         //                    receivedPaths.set(i, BroadcastSystem.receiveLocsDataList(robotChannels.get(i)+5));
         //                    if (receivedPaths.get(i)==0){
         //                        receivedAllPaths = false;
         //                    }
         //                }
         //            }
         //        }
         //       // System.out.println(receivedPaths);
         //        //System.out.println(MapEngine.senseQueueHQ);

                
        	// 	broadcastCount = 0;
         //        //MapEngine.resetOreList();
         //        //System.out.println(MapEngine.senseQueueHQ);

         //        System.out.println("HQ TEST MIDDLE 1");
         //        //senseQueueHQ: [(x1,y1), (x2,y2)] of locations that a robot has been at
        	// 	for (MapLocation loc: MapEngine.senseQueueHQ){

        	// 		//MapEngine.rescanOreMapGivenLoc(loc);
                    
                    
         //            //If its a new location
         //            if (!MapEngine.prevSensedLocs.contains(loc)){
         //                //Get all visible tiles from that location
         //                testRobotSeenLocs = MapEngine.unitScan(loc);
         //                //Scan all visible tiles from that location
         //                MapEngine.scanTiles(testRobotSeenLocs);
         //                //Add that location to previously seen locations
         //                MapEngine.prevSensedLocs.add(loc);
         //            }
         //            //System.out.println("HQ TEST3");
        			

        	// 	}
         //        System.out.println("HQ TEST MIDDLE 2");
                

         //        //Prepare the map for broadcasting.
        	// 	MapEngine.resetMapAndPrep();

         //        //Send the map data dict
        	// 	BroadcastSystem.prepareandsendMapDataDict(MapEngine.sensedMAINDictHQ,BroadcastSystem.dataBand);
        	// 	//Send the waypoint data dict
        	// 	BroadcastSystem.prepareandsendWaypointDict(MapEngine.waypointDictHQ);
         //        //System.out.println(MapEngine.waypointDictHQ);
         //        MapEngine.resetSensedMAINDict();

         //        for (int i=0; i<receivedPaths.size();i++){
         //            receivedPaths.set(i, 0);
         //        }
         //        //System.out.println("HQ TEST END");
                
         //        //Tell the robot the dictionaries are ready for download.
         //        for (int channel: robotChannels){
         //            BroadcastSystem.write(channel, 2);
         //        }
         //        System.out.println("HQ TEST END");
        	// }

         //    broadcastCount++;
         //    oreCount++;

            ///////

            // if (oreCount>500){
            //     oreCount = 0;
            //     //System.out.println("HQ TEST 2");
            //     BroadcastSystem.prepareandsendMapDataDict(MapEngine.sensedOREDictHQ,BroadcastSystem.dataBandORE);
            //     MapEngine.resetSensedOREDict();
            //     BroadcastSystem.write(2002, 1);
            //     //System.out.println("HQ TEST 2 END");
            // }


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
    static void trySpawn(Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = Functions.directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }
}
