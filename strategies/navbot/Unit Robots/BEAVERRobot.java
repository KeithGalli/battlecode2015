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


	public static int myChannel;
	public static int myLocChannel;

	public static MapLocation tile;
	public static MapLocation[] visibleTiles;

	public static MapLocation goal;

	public static Random random = new Random();

	public static int downloadReady;
	public static int oreReady;
	public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public static int indx = 0;
	public static boolean hqFound = false;
	public static boolean ourTowersFound = false;
	public static boolean enemyTowersFound = false;

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		goal = rc.senseEnemyHQLocation();
		myChannel = BroadcastSystem.read(DISTRIBUTECHANNEL);
		myLocChannel = myChannel+5;

	}

	@Override
	public void run() {
		try {
			rc.setIndicatorString(1, goal.toString());

			//UPDATE THE ROUND VARIABLES
			DataCache.updateRoundVariables();
			//TELL THE HQ WHERE WE ARE
			if (DataCache.hasMoved()){
				//System.out.println(DataCache.currentLoc);
				MapEngine.senseQueue.add(DataCache.currentLoc);
			}
			//rc.broadcast(TESTCHANNEL, Functions.locToInt(Functions.locToInternalLoc(DataCache.currentLoc)));


			//IF WERE AT OUR CURRENT GOAL PICK A NEW ONE AT RANDOM
			if (DataCache.currentLoc.distanceSquaredTo(goal)<5){
				if (hqFound){
					if (enemyTowersFound){
						if (ourTowersFound){
							//
						}
						else{
							if (indx==DataCache.ourTowers.length){
								ourTowersFound = true;
								indx = 0;
								goal = Functions.internallocToLoc(MapEngine.internalMapCenter);
							}
							else{
								indx++;
								goal = DataCache.ourTowers[indx];
							}
						}
					}
					else{
						if (indx==DataCache.enemyTowers.length){
							enemyTowersFound = true;
							indx = 0;
							goal = DataCache.ourTowers[indx];
						}
						else{
							indx++;
							goal = DataCache.enemyTowers[indx];
						}
					}
				}
				else {
					hqFound = true;
					goal = DataCache.enemyTowers[indx];
				}
			}


			
			
			downloadReady = BroadcastSystem.read(myChannel);

			oreReady = BroadcastSystem.read(2002);
			//IF THE DATA IS READY TO DOWNLOAD
			if (downloadReady==1){
				System.out.println("BEAVER TEST");
				BroadcastSystem.prepareandsendLocsDataList(MapEngine.senseQueue, myLocChannel);
				//System.out.println(MapEngine.senseQueue);
				MapEngine.resetSenseQueue();
				BroadcastSystem.write(myChannel,0);
				System.out.println("BEAVER TESTCHANNEL");
			}
			if (downloadReady==2){
				//System.out.println("Test");

				System.out.println("BEAVER TEST 2");
				BroadcastSystem.receiveMapDataDict(BroadcastSystem.dataBand);
				// System.out.println("/////////////////////////");
    //     		Functions.displayOREArray(MapEngine.map);
	      		System.out.println("/////////////////////////");
	      		Functions.displayWallArray(MapEngine.map);
	      		// //System.out.println(MapEngine.waypointDict);
	      		System.out.println("/////////////////////////");
				MapEngine.waypointDict = BroadcastSystem.receiveWaypointDict();
				System.out.println("BEAVER TEST 2 END");
				//System.out.println(MapEngine.waypointDict);
				//System.out.println("Test2");
				BroadcastSystem.write(myChannel, 0);
			}

			if (oreReady==1){
				System.out.println("BEAVER TEST 3");
				BroadcastSystem.receiveMapDataDict(BroadcastSystem.dataBandORE);
				System.out.println("BEAVER TEST 3 END");
				BroadcastSystem.write(2002, 0);
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
