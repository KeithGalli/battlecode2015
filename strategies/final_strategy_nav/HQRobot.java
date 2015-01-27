package final_strategy_nav;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
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

    public static boolean sentBroadcast = false;

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
//			if(Clock.getRoundNum()%50==0){
//			    rc.broadcast(200, 0);
//			}

			robotCount = BroadcastSystem.read(BroadcastSystem.robotCountBand);///////////////////////////////

            updateRobots();
           // System.out.println("HQ START");
            if (aliveTurnCount>400){
             //   System.out.println("TEST CHECK -1");

                checkAlive();
             //   System.out.println("TEST CHECK 0");
                aliveTurnCount=0;
            }

			hqTransferAllSuppliesForRestOfGame(rc);
			
			rc.broadcast(200, 0);
			
		    int numMinerFactories = rc.readBroadcast(MINER_FACT_CURRENT_CHAN);
		    rc.broadcast(MINER_FACT_CURRENT_CHAN, 0);
		    int numMiners = rc.readBroadcast(MINER_CURRENT_CHAN);
		    rc.broadcast(MINER_CURRENT_CHAN, 0);
		    int numBeavers = rc.readBroadcast(BEAVER_CURRENT_CHAN);
            rc.broadcast(BEAVER_CURRENT_CHAN, 0);
            int numHelipads = rc.readBroadcast(HELIPAD_CURRENT_CHAN);
            rc.broadcast(HELIPAD_CURRENT_CHAN, 0);
            int numDrones = rc.readBroadcast(DRONE_CURRENT_CHAN);
            rc.broadcast(DRONE_CURRENT_CHAN, 0);
            int numSupplierDrones = rc.readBroadcast(SUPPLIER_DRONES_CURRENT_CHAN);
            rc.broadcast(SUPPLIER_DRONES_CURRENT_CHAN, 0);
            int numSupplyDepots = rc.readBroadcast(SUPPLY_DEPOT_CURRENT_CHAN);
            rc.broadcast(SUPPLY_DEPOT_CURRENT_CHAN, 0);
            int numTanks = rc.readBroadcast(TANK_CURRENT_CHAN);
            rc.broadcast(TANK_CURRENT_CHAN, 0);
            
            rc.broadcast(MINER_FACT_PREVIOUS_CHAN, numMinerFactories);
            rc.broadcast(MINER_PREVIOUS_CHAN, numMiners);
            rc.broadcast(BEAVER_PREVIOUS_CHAN, numBeavers);
            rc.broadcast(HELIPAD_PREVIOUS_CHAN, numHelipads);
            rc.broadcast(DRONE_PREVIOUS_CHAN, numDrones);
            rc.broadcast(SUPPLIER_DRONES_PREVIOUS_CHAN, numSupplierDrones);
            rc.broadcast(SUPPLY_DEPOT_PREVIOUS_CHAN, numSupplyDepots);
            rc.broadcast(TANK_PREVIOUS_CHAN, numTanks);
            
            RobotInfo[] enemies = getEnemiesInAttackingRange(RobotType.HQ);
            if(enemies.length>0 && rc.isWeaponReady()){
            	attackLeastHealthEnemy(enemies);
            } else if (rc.isCoreReady() && rc.hasSpawnRequirements(RobotType.BEAVER) && rc.readBroadcast(BEAVER_PREVIOUS_CHAN)<5) {
                RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
            }

            if (broadcastCount>30000 && Clock.getRoundNum()<1000){
               // System.out.println("TEST CHECK 0");
                mod = 0;

                for (int i=0; i<robotCount;i++){///////////////////////////////
                    if (robotAlive.get(i)){
                        if (BroadcastSystem.read(robotCollectingChannels.get(i))==1){
                            collectingPaths.set(i, 1);///////////////////////////////
                            BroadcastSystem.write(robotInstrChannels.get(i), BroadcastSystem.bigBand+mod);
                            dynamicRobotChannels.set(i, BroadcastSystem.bigBand+mod);
                            mod+=00; ///////////////////////////////
                        }
                    }
                }
                //System.out.println("TEST CHECK 1");
               // System.out.println(dynamicRobotChannels);
              //  System.out.println(collectingPaths);
                receivedAllPaths = false;
                if (mod>0){
                    broadcastMode = true;
                } else{
                     broadcastMode = false;
                }
               
                broadcastCount = 0;
                //System.out.println("HQ TEST");
            }

            if (broadcastMode == true){
                if (!receivedAllPaths){
                   // System.out.println("/////TANKING PART 1///////////");
                   //System.out.println("TEST CHECK 2");
                   //System.out.println(receivedPaths);

                    receivedAllPaths = true;
                    for (int i=0; i<robotCount;i++){
                        if (collectingPaths.get(i)==1){ ////////////
                            if (robotAlive.get(i)){//////////////////////////////////
                                if (receivedPaths.get(i)==0){
                                    //System.out.println("Test");
                                    // System.out.println("/////TEST//////////");
                                    // System.out.println(i);
                                    // System.out.println(robotTypes.get(i));
                                    // System.out.println("/////TEST//////////");
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
                   // System.out.println("TEST CHECK 2 END");
                } else {
                   
                //   System.out.println("//////TANKING PART 2////////");
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
                   // System.out.println("HQ TEST 2 END");
                    
                    //Tell the robot the dictionaries are ready for download.
                    for (int channel: robotInstrChannels){
                        BroadcastSystem.write(channel, 2);
                    }
                    broadcastMode = false;
                    // System.out.println("///////Main Map////////");
                    // Functions.displayWallArray(MapEngine.map);
                    // System.out.println("///////////////////////");
                // //    System.out.println("HQ TEST END");
                }
            } else {
                broadcastCount++;
                oreLocCount++;
                aliveTurnCount++;
            }
           // System.out.println("HQ END");

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
}
