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


	public static Direction tarDir;

	public static MapLocation tile;
	public static MapLocation[] visibleTiles;

	public static MapLocation goal;

	public static Random random = new Random();

	public static int downloadReady;
	public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		goal = rc.senseEnemyHQLocation();
	}

	@Override
	public void run() {
		try {
			rc.setIndicatorString(1, goal.toString());

			//UPDATE THE ROUND VARIABLES
			DataCache.updateRoundVariables();
			//TELL THE HQ WHERE WE ARE
			rc.broadcast(TESTCHANNEL, Functions.locToInt(Functions.locToInternalLoc(DataCache.currentLoc)));


			//IF WERE AT OUR CURRENT GOAL PICK A NEW ONE AT RANDOM
			if (DataCache.currentLoc.distanceSquaredTo(goal)<5){
				int x = random.nextInt(30)-15;
				int y = random.nextInt(30)-15;
				x = MapEngine.xdim/2-x;
				y = MapEngine.ydim/2-y;
				goal = Functions.internallocToLoc(new MapLocation(x,y));
				NavSystem.resetNav();
				// System.out.println(goal);
				// System.out.println("/////////////////////////");
    //     		Functions.displayArray(MapEngine.map);
	   //    		System.out.println("/////////////////////////");
			}


			/
			
			downloadReady = BroadcastSystem.read(2001);

			//IF THE DATA IS READY TO DOWNLOAD
			if (downloadReady==1){
				System.out.println("Test");
				BroadcastSystem.receiveMapDataDict();
				// System.out.println("/////////////////////////");
    //     		Functions.displayArray(MapEngine.map);
	   //    		System.out.println("/////////////////////////");
				MapEngine.waypointDict = BroadcastSystem.receiveWaypointDict();
				System.out.println("Test2");
				BroadcastSystem.write(2001, 0);
			}
			
			NavSystem.smartNav(goal);




		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
