package navbot;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class NavSystem {

	public static RobotController rc;

	public static MapLocation currentWaypoint = new MapLocation(-1, -1);
	public static MapLocation previousWaypoint = new MapLocation(-1, -1);

	public static boolean pathToGoal = false;

	public static boolean atGoal = false;

	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static Direction[] diagDirs = {Direction.NORTH_EAST,Direction.NORTH_WEST,Direction.SOUTH_EAST,Direction.SOUTH_WEST};

	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction allDirections[] = Direction.values();


	public static void HQinit(RobotController rcIn) {
		rc = rcIn;
	}

	public static void TOWERinit(RobotController rcIn) {
		rc = rcIn;
	}

	public static void UNITinit(RobotController rcIn) {
		rc = rcIn;
	}
	
	//////////////////////////////////////////////
	/// PATH BUILDING
	//////////////////////////////////////////////
	public static int pathExists(MapLocation start, MapLocation finish){
		MapLocation current = start;
		while (!current.equals(finish)){
			Direction dir = current.directionTo(finish);
			MapLocation trial = current.add(dir);
			if (MapEngine.map[trial.x][trial.y]>1){
				return MapEngine.map[trial.x][trial.y];
			} 
			current=trial;
		}
		return 0;
	}

	public static MapLocation closestWaypoint(int voidID){
		int minDistance = 10000;
		MapLocation bestWaypoint = null;

		for (MapLocation loc: MapEngine.waypointDict.get(voidID)){
			//System.out.println("test");
			int testdist = DataCache.internalLoc.distanceSquaredTo(loc);
			if (testdist<minDistance && pathExists(DataCache.internalLoc, loc)==0 && !previousWaypoint.equals(loc)&&!DataCache.internalLoc.equals(loc)){
				//System.out.println("yahoo");
				//System.out.println(previousWaypoint);

				//System.out.println("going to" + loc);
				//System.out.println();
				minDistance = testdist;
				bestWaypoint = loc;
			}
		}
		previousWaypoint = currentWaypoint;
		if (bestWaypoint == null){
			bestWaypoint = Functions.findClosest(MapEngine.waypointDict.get(voidID), DataCache.internalLoc);
		}
		return bestWaypoint;
	}

	public static void smartPathTo(MapLocation internalGoal) throws GameActionException{
		
		int obstacle = pathExists(DataCache.internalLoc, internalGoal);
		//System.out.println(obstacle);
		if (obstacle == 0){
			currentWaypoint = internalGoal;
			pathToGoal = true;
		}
		else{

			currentWaypoint = closestWaypoint(obstacle);
			pathToGoal = false;
			//System.out.println(previousWaypoint);
			//System.out.println(currentWaypoint);
		}
	}

	public static void resetNav() throws GameActionException{
		currentWaypoint = new MapLocation(-1, -1);
		previousWaypoint = new MapLocation(-1, -1);
		pathToGoal = false;
		atGoal = false;
	}

	public static void smartNav(MapLocation goal) throws GameActionException{
		MapLocation internalGoal = Functions.locToInternalLoc(goal);
		//rc.setIndicatorString(1, Integer.toString(Functions.locToInt(internalGoal)));
		//rc.setIndicatorString(1, Integer.toString(Functions.locToInt(currentWaypoint)));
		//rc.setIndicatorString(2, Integer.toString(pathExists(DataCache.internalLoc, internalGoal)));
		//rc.setIndicatorString(2, Integer.toString(Functions.locToInt(DataCache.internalLoc)));

		atGoal = false;
		//System.out.println(DataCache.internalLoc);
		//System.out.println(internalGoal);
		
		if(DataCache.internalLoc.equals(internalGoal)){
			return;
		}
		if (currentWaypoint.equals(new MapLocation(-1, -1))){
			smartPathTo(internalGoal);
			if(pathToGoal){
				Direction dir = DataCache.currentLoc.directionTo(goal);
				snailNav(dir);
			}
			else{
				Direction dir = DataCache.currentLoc.directionTo(Functions.internallocToLoc(currentWaypoint));
				snailNav(dir);
			}
		}
		if (DataCache.internalLoc.equals(currentWaypoint)){
			//rc.setIndicatorString(2, Integer.toString(pathExists(DataCache.internalLoc, internalGoal)));
			if(DataCache.internalLoc.equals(internalGoal)){
				currentWaypoint=new MapLocation(-1, -1);
				previousWaypoint=new MapLocation(-1, -1);
				atGoal =true;
			}
			else{
				smartPathTo(internalGoal);
				if(pathToGoal){

					Direction dir = DataCache.currentLoc.directionTo(goal);
					snailNav(dir);
				}
				else{
					Direction dir = DataCache.currentLoc.directionTo(Functions.internallocToLoc(currentWaypoint));
					snailNav(dir);
				}
			}

		}
		else{
			
			/////STAND IN IDEALLY WE NEED ANOTHER BREAKPOINT HERE FOR BLIND
			smartPathTo(currentWaypoint);
			/////
			Direction dir = DataCache.currentLoc.directionTo(Functions.internallocToLoc(currentWaypoint));
			snailNav(dir);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	//Snail Navigation
	///////////////////////////////////////////////////////////////////////////
	public static void dumbNav(MapLocation loc) throws GameActionException{
		snailNav(DataCache.currentLoc.directionTo(loc));
	}


	public static void snailNav(Direction chosenDirection) throws GameActionException{
		tryToMove(chosenDirection, true, rc, directionalLooks, allDirections);
	}

	static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();

	static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
		//include both rc.canMove and the snail Trail requirements
		if(selfAvoiding){
			MapLocation resultingLocation = rc.getLocation().add(dir);
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
						//rc.setIndicatorString(2, "adjacentto");
						return false;
					}
				}
			}
		}
		//if you get through the loop, then dir is not adjacent to the icky snail trail
		//rc.setIndicatorString(2, "canmove in " + dir);
		return rc.canMove(dir);
	}

	private static void tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(rc.isCoreReady()){
			snailTrail.remove(0);
			snailTrail.add(rc.getLocation());
			for(int directionalOffset:directionalLooks){
				//rc.setIndicatorString(0, "notmoving");
				//rc.setIndicatorString(2, "notmoving in a direction");
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding,rc)){
					//rc.setIndicatorString(0, "moving in" + trialDir);
					//rc.setIndicatorString(2, String.valueOf(rc.canMove(trialDir)));
					//
					rc.move(trialDir);
					//snailTrail.remove(0);
					//snailTrail.add(rc.getLocation());
					break;
				}

				//rc.setIndicatorString(2, String.valueOf(rc.canMove(trialDir)));
			}
			//System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
		}
	}

	///////////////////////////////////////////////////////////////////////////
	//Simple Navigation
	///////////////////////////////////////////////////////////////////////////
	public static void simpleNav(Direction chosenDirection) throws GameActionException{
		if(rc.isCoreReady()){
			for(int directionalOffset:directionalLooks){
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(rc.canMove(trialDir)){
					rc.move(trialDir);
					break;
				}
			}
		}
	}


}
	