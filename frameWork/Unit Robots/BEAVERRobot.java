package team010;

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

	public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
	}

	@Override
	public void run() {
		try {

			DataCache.updateRoundVariables();
			
			tarDir = rc.getLocation().directionTo(DataCache.enemyHQ);
			visibleTiles = MapEngine.unitScan(DataCache.currentLoc);
			//System.out.println(visibleTiles);
			for (MapLocation tile: visibleTiles){
				if (!DataCache.seenLocs.contains(tile)){
					newLocs.add(tile);
				}
			}
			DataCache.updateSeenLocs(newLocs);
			newLocs=new ArrayList<MapLocation>();
			//DataCache.displaySeenLocs();
			
			NavSystem.snailNav(tarDir);




		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
