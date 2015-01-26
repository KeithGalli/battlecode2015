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

    public static int prevRobotCount = 0;
	public static int robotCount = 0;
    public static int structCount = 0;
	public static int broadcastCount = 0;
    public static int oreLocCount = 0;
    public static int aliveTurnCount = 0;

    public static int mod = 0;

	public static MapLocation testRobotLoc;
	public static MapLocation testRobotInternalLoc;
	public static MapLocation[] testHQLocs;
	public static MapLocation[] testRobotSeenLocs;
	public static int broadcastReady;

    public static boolean receivedAllPaths = false;

    public static boolean broadcastMode = false;

    public static boolean waitingOnPaths = false;

    public static List<Integer> collectingPaths = new ArrayList<Integer>();
    public static List<Integer> receivedPaths = new ArrayList<Integer>();

    public static List<Boolean> robotAlive = new ArrayList<Boolean>();

    public static List<Integer> robotPrevCount = new ArrayList<Integer>();

    public static List<Integer> robotInstrChannels = new ArrayList<Integer>();
    public static List<Integer> robotAliveChannels = new ArrayList<Integer>();
    public static List<Integer> robotCollectingChannels = new ArrayList<Integer>();

    public static List<Integer> dynamicRobotChannels = new ArrayList<Integer>();
    public static List<RobotType> robotTypes = new ArrayList<RobotType>();

	public static int[][] testmap;

    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};



	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);

		//Init Systems//
		NavSystem.HQinit(rc);
		MapEngine.HQinit(rc);

		BroadcastSystem.myInstrChannel = 220;
		testHQLocs = MapEngine.structScan(rc.getLocation());
		MapEngine.scanTiles(testHQLocs);

	}

	@Override
	public void run() {
		try {
           // System.out.println("TEST CHECK -1");
            robotCount = BroadcastSystem.read(BroadcastSystem.robotCountBand);///////////////////////////////

            updateRobots();
            
            if (aliveTurnCount>40){
             //   System.out.println("TEST CHECK -1");

                checkAlive();
             //   System.out.println("TEST CHECK 0");
                aliveTurnCount=0;
            }
            //System.out.println("TEST CHECK 1");

           
           

			if (rc.isCoreReady() && rc.getTeamOre() >= 100) {
				rc.setIndicatorString(0, "trying to spawn");
				trySpawn(rc.getLocation().directionTo(DataCache.enemyHQ),RobotType.BEAVER);
   //             receivedPaths.add(0);
   //             robotAlive.add(true);
   //             robotChannels.add(distributeMod);
     //           BroadcastSystem.write(BroadcastSystem.distributionChannel, distributeMod);
     //           distributeMod+=200;
	//			count ++;
        	}
            // System.out.println("Beginning of test");
            // System.out.println(receivedPaths);
            // System.out.println(robotAlive);
            // System.out.println(robotInstrChannels);
            // System.out.println(robotTypes);
            // System.out.println("End of test");
            // if (oreLocCount>500){
            //     oreLocCount=0;
            //     System.out.println("///////START OF ORE FUNCTION////////");


            //     MapEngine.makeOreDensityMap();
            //     MapEngine.setBestOreLocs();
            //     // System.out.println("///////Main Map////////");
            //     // Functions.displayWallArray(MapEngine.map);
            //     // System.out.println("///////ORE Map////////");
            //     // Functions.displayOREArray(MapEngine.map);
            //     // System.out.println("///////ORE LOCS////////");
            //     //System.out.println(Functions.internallocToLoc(DataCache.bestOreLoc));
            //  //   System.out.println(Functions.internallocToLoc(DataCache.secondbestOreLoc));
            //   //  System.out.println(Functions.internallocToLoc(DataCache.thirdbestOreLoc));
            //     System.out.println("///////END OF ORE FUNCTION////////");



            // }

            //ARBITRARY 100 RD Counter
            // SPLIT ORE COUNT
            if (broadcastCount>100){
                //System.out.println("TEST CHECK 0");
                mod = 0;
                BroadcastSystem.write(robotCollectingChannels.get(0), 1);

                for (int i=0; i<robotCount;i++){///////////////////////////////
                    if (robotAlive.get(i)){
                        if (BroadcastSystem.read(robotCollectingChannels.get(i))==1){
                            collectingPaths.set(i, 1);///////////////////////////////
                            BroadcastSystem.write(robotInstrChannels.get(i), BroadcastSystem.bigBand+mod);
                            dynamicRobotChannels.set(i, BroadcastSystem.bigBand+mod);
                            mod+=200; ///////////////////////////////
                        }
                    }
                }
                //System.out.println("TEST CHECK 1");
               // System.out.println(dynamicRobotChannels);
              //  System.out.println(collectingPaths);
                receivedAllPaths = false;
                broadcastMode = true;
                broadcastCount = 0;
                System.out.println("HQ TEST");
            }

            if (broadcastMode == true){
                if (!receivedAllPaths){
                   System.out.println("TEST CHECK 2");
                   // System.out.println(receivedPaths);

                    receivedAllPaths = true;
                    for (int i=0; i<robotCount;i++){
                        if (collectingPaths.get(i)==1){ ////////////
                            if (robotAlive.get(i)){//////////////////////////////////
                                if (receivedPaths.get(i)==0){
                                    //System.out.println("Test");
                                    receivedPaths.set(i, BroadcastSystem.receiveLocsDataList(dynamicRobotChannels.get(i)));///////
                                    if (receivedPaths.get(i)==0){
                                        receivedAllPaths = false;
                                    }
                                }
                            } else{
                                receivedPaths.set(i, 1);
                            }
                        }
                    }
                    System.out.println("TEST CHECK 2 END");
                } else {
                   
                   System.out.println("HQ TEST 2");
                    //System.out.println(MapEngine.senseQueueHQ);


                    //senseQueueHQ: [(x1,y1), (x2,y2)] of locations that a robot has been at
                    for (MapLocation loc: MapEngine.senseQueueHQ){

                        //MapEngine.rescanOreMapGivenLoc(loc);
                        //System.out.println("HQ TEST CHECK");
                        //If its a new location
                            //Get all visible tiles from that location
                        testRobotSeenLocs = MapEngine.unitScan(loc);
                        //Scan all visible tiles from that location
                        MapEngine.scanTiles(testRobotSeenLocs);
                        //Add that location to previously seen locations
                        MapEngine.prevSensedLocs.add(loc);
                        

                    }

                    //Prepare the map for broadcasting.
                    MapEngine.resetMapAndPrep();

                 //   System.out.println(MapEngine.sensedMAINDictHQ);
                 //   System.out.println(MapEngine.waypointDictHQ);
                    //Send the map data dict
                    BroadcastSystem.prepareandsendMapDataDict(MapEngine.sensedMAINDictHQ,BroadcastSystem.dataBand);
                    //Send the waypoint data dict
                    BroadcastSystem.prepareandsendWaypointDict(MapEngine.waypointDictHQ);
                    //System.out.println(MapEngine.waypointDictHQ);
                    MapEngine.resetSensedMAINDict();

                    for (int i=0; i<receivedPaths.size();i++){
                        receivedPaths.set(i, 0);
                    }
                    System.out.println("HQ TEST 2 END");
                    
                    //Tell the robot the dictionaries are ready for download.
                    for (int channel: robotInstrChannels){
                        BroadcastSystem.write(channel, 2);
                    }
                    broadcastMode = false;
                    System.out.println("///////Main Map////////");
                    Functions.displayWallArray(MapEngine.map);
                    System.out.println("///////////////////////");
                //    System.out.println("HQ TEST END");
                }
            } else {
                broadcastCount++;
                oreLocCount++;
                aliveTurnCount++;
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

            ///////



		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}

    static void updateRobots(){
        if (robotCount!=prevRobotCount){
            //System.out.println("TEST CHECK 2");
            for (int i = prevRobotCount+1;i<=robotCount;i++){
                receivedPaths.add(0);
                collectingPaths.add(0);
                robotAlive.add(true);
                robotPrevCount.add(0);
                robotInstrChannels.add(BroadcastSystem.unitInstrRefBand+i);
                robotAliveChannels.add(BroadcastSystem.unitAliveRefBand+i);
                robotCollectingChannels.add(BroadcastSystem.unitCollectingRefBand+i);
                dynamicRobotChannels.add(0);
                robotTypes.add(Functions.consToRobot(BroadcastSystem.read(BroadcastSystem.unitInstrRefBand+i)));
                BroadcastSystem.write(BroadcastSystem.unitInstrRefBand+i,0);
            }

        }
        prevRobotCount = robotCount;
    }

    static void checkAlive(){
        for (int i=0;i<robotCount;i++){
            if (robotAlive.get(i)){
                int newRobotCount = BroadcastSystem.read(robotAliveChannels.get(i));
                if (newRobotCount == robotPrevCount.get(i)){
                    robotAlive.set(i,false);
                } else{
                    robotPrevCount.set(i, newRobotCount);
                }
            }
        }
    }

    static void updateStructures(){
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
            int count = BroadcastSystem.read(BroadcastSystem.robotCountBand);
            BroadcastSystem.write(BroadcastSystem.distributionBand,BroadcastSystem.unitInstrRefBand+count+1);
            rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
            BroadcastSystem.write(BroadcastSystem.robotCountBand, count+1);
        }
    }
}
