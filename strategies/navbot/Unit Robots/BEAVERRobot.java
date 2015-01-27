package navbot;

import battlecode.common.Clock;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class BEAVERRobot extends BaseRobot {



	public static MapLocation tile;
	public static MapLocation[] visibleTiles;

	public static MapLocation goal;

	public static Random random = new Random();

	public static int downloadReady;
	public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public static int indx = 0;
	public static boolean hqFound = false;
	public static boolean ourTowersFound = false;
	public static boolean enemyTowersFound = false;

	public static int minx;
	public static int maxx;
	public static int miny;
	public static int maxy;


	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		goal = rc.senseEnemyHQLocation();
		//System.out.println("THIS IS BEAVER:"+BroadcastSystem.myChannel);
		BroadcastSystem.write(BroadcastSystem.myInstrChannel, BEAVER_CONS);
		BroadcastSystem.setNotCollecting();

		minx = BroadcastSystem.read(BroadcastSystem.minxBand);
		maxx = BroadcastSystem.read(BroadcastSystem.maxxBand);
		miny = BroadcastSystem.read(BroadcastSystem.minyBand);
		maxy = BroadcastSystem.read(BroadcastSystem.maxyBand);
		random.setSeed((long) rc.getID());


	}

	@Override
	public void run() {
		try {
			rc.setIndicatorString(1, goal.toString());

			//UPDATE THE ROUND VARIABLES
			DataCache.updateRoundVariables();
			//TELL THE HQ WHERE WE ARE
			if (DataCache.hasMoved()){
				MapEngine.senseQueue.add(DataCache.currentLoc);
			}
			//rc.broadcast(TESTCHANNEL, Functions.locToInt(Functions.locToInternalLoc(DataCache.currentLoc)));


			if (DataCache.currentLoc.distanceSquaredTo(goal)<5){
				boolean goalFound = false;
				while (!goalFound){
					int tempx = random.nextInt(maxx-minx)+minx;
					int tempy = random.nextInt(maxy-miny)+miny;
					MapLocation internalTempLoc = Functions.locToInternalLoc(new MapLocation(tempx,tempy));

					if (MapEngine.map[internalTempLoc.x][internalTempLoc.y]==0 || MapEngine.map[internalTempLoc.x][internalTempLoc.y]<-1){
						goal = new MapLocation(tempx, tempy);
						goalFound = true;
					}
				}
				
			}

			// //IF WERE AT OUR CURRENT GOAL PICK A NEW ONE AT RANDOM
			// if (DataCache.currentLoc.distanceSquaredTo(goal)<5){
			// 	if (hqFound){
			// 		if (enemyTowersFound){
			// 			if (ourTowersFound){
			// 				//
			// 			}
			// 			else{
			// 				if (indx==DataCache.ourTowers.length){
			// 					ourTowersFound = true;
			// 					indx = 0;
			// 					goal = Functions.internallocToLoc(MapEngine.internalMapCenter);
			// 				}
			// 				else{
			// 					indx++;
			// 					goal = DataCache.ourTowers[indx];
			// 				}
			// 			}
			// 		}
			// 		else{
			// 			if (indx==DataCache.enemyTowers.length){
			// 				enemyTowersFound = true;
			// 				indx = 0;
			// 				goal = DataCache.ourTowers[indx];
			// 			}
			// 			else{
			// 				indx++;
			// 				goal = DataCache.enemyTowers[indx];
			// 			}
			// 		}
			// 	}
			// 	else {
			// 		hqFound = true;
			// 		goal = DataCache.enemyTowers[indx];
			// 	}
			// }


			
			
			downloadReady = BroadcastSystem.read(BroadcastSystem.myInstrChannel);

			//IF THE DATA IS READY TO DOWNLOAD
			if (downloadReady>=25000){
				//System.out.println("BEAVER TEST");
				//rc.setIndicatorString(1, "messaging");
				BroadcastSystem.prepareandsendLocsDataList(MapEngine.senseQueue, downloadReady);
				//System.out.println(MapEngine.senseQueue);
				MapEngine.resetSenseQueue();
				BroadcastSystem.write(BroadcastSystem.myInstrChannel,0);
				//System.out.println("BEAVER TEST END");
			//	System.out.println("BEAVER TESTCHANNEL");
			}
			if (downloadReady==2){
				//System.out.println("BEAVER TEST 2");

				//rc.setIndicatorString(1, "downloading");
				BroadcastSystem.receiveMapDataDict(BroadcastSystem.dataBand);
				// System.out.println("/////////////////////////");
    // // //     		Functions.displayOREArray(MapEngine.map);
	   //    		System.out.println("/////////////////////////");
	   //    		Functions.displayWallArray(MapEngine.map);
	   // //    		// //System.out.println(MapEngine.waypointDict);
	   //    		System.out.println("/////////////////////////");
				MapEngine.waypointDict = BroadcastSystem.receiveWaypointDict();
				//System.out.println("BEAVER TEST 2 END");
				//System.out.println(MapEngine.waypointDict);
				//System.out.println("Test2");
				//rc.setIndicatorString(1, "not downloading");
				BroadcastSystem.write(BroadcastSystem.myInstrChannel, 0);
			}

			//System.out.println("BEAVER TEST");
			rc.setIndicatorString(1,goal.toString());
			NavSystem.smartNav(goal, false);
			//System.out.println("BEAVER TEST END");



		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
