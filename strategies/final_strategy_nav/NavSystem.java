package final_strategy_nav;
import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class NavSystem {

	public static RobotController rc;

	public static MapLocation prevGoal = new MapLocation(-1, -1);

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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// PATH BUILDING
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	//INPUTS: start and finish locations (INTERNAL LOCATIONS)
	//OUTPUT: voidID if there is a wall in the way, 0 otherwise
	//HOW IT WORKS: essentially draws a line from the start loc to finish loc. if the line intersects a void, return the voidID
	//USED IN: Called by smartPathTo to determine whether there's a void in the way.
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

	//INPUTS: voidID
	//OUTPUT: MapLocation that is the closest "best" corner point of the given wall.
	//USED IN: Called by smartPathTo to determine the best way to get around a void
	public static MapLocation closestWaypoint(int voidID){
		int minDistance = 10000;
		MapLocation bestWaypoint = null;

		for (MapLocation loc: MapEngine.waypointDict.get(voidID)){
			int testdist = DataCache.internalLoc.distanceSquaredTo(loc);
			if (testdist<minDistance && pathExists(DataCache.internalLoc, loc)==0 && !previousWaypoint.equals(loc)&&!DataCache.internalLoc.equals(loc)){

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

	//INPUT: Internal MapLocation goal
	//PURPOSE: Determines whether we should be moving towards the final goal, or an intermediate waypoint.
	//USED IN: called by smartNav to get which location to move towards.
	public static void smartPathTo(MapLocation internalGoal) throws GameActionException{
		
		int obstacle = pathExists(DataCache.internalLoc, internalGoal);
		if (obstacle == 0){
			currentWaypoint = internalGoal;
			pathToGoal = true;
		}
		else{

			currentWaypoint = closestWaypoint(obstacle);
			pathToGoal = false;
		}
	}

	//RESET THE NAVIGATION VARIABLES. CALL WHEN AT GOAL
	public static void resetNav() throws GameActionException{
		currentWaypoint = new MapLocation(-1, -1);
		previousWaypoint = new MapLocation(-1, -1);
		pathToGoal = false;
		atGoal = false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Smart Navigation
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void smartNav(MapLocation goal, boolean vip) throws GameActionException{
		if (!goal.equals(prevGoal)){
			currentWaypoint=new MapLocation(-1, -1);
			previousWaypoint=new MapLocation(-1, -1);
		}

		prevGoal = goal;


		MapLocation internalGoal = Functions.locToInternalLoc(goal);

		atGoal = false;

		if(DataCache.internalLoc.equals(internalGoal)){
			resetNav();
			atGoal = true;
			return;
		}
		if (currentWaypoint.equals(new MapLocation(-1, -1))){
			smartPathTo(internalGoal);
			if(pathToGoal){
				Direction dir = DataCache.currentLoc.directionTo(goal);
				if (vip){
					snailVIPNav(dir);
				} else{ 
					snailNav(dir);
				}
				
			}
			else{
				Direction dir = DataCache.currentLoc.directionTo(Functions.internallocToLoc(currentWaypoint));
				if (vip){
					snailVIPNav(dir);
				} else{ 
					snailNav(dir);
				}
			}
		}
		if (DataCache.internalLoc.equals(currentWaypoint)){
			if(DataCache.internalLoc.equals(internalGoal)){
				currentWaypoint=new MapLocation(-1, -1);
				previousWaypoint=new MapLocation(-1, -1);
				atGoal =true;
			}
			else{
				smartPathTo(internalGoal);
				if(pathToGoal){

					Direction dir = DataCache.currentLoc.directionTo(goal);
					if (vip){
						snailVIPNav(dir);
					} else{ 
						snailNav(dir);
					}	
				}
				else{
					Direction dir = DataCache.currentLoc.directionTo(Functions.internallocToLoc(currentWaypoint));
					if (vip){
						snailVIPNav(dir);
					} else{ 
						snailNav(dir);
					}
				}
			}

		}
		else{
			smartPathTo(internalGoal);
			if(pathToGoal){
				Direction dir = DataCache.currentLoc.directionTo(goal);
				if (vip){
					snailVIPNav(dir);
				} else{ 
					snailNav(dir);
				}
			}
			/////STANDIN IDEALLY WE NEED ANOTHER BREAKPOINT HERE FOR BLIND
			//
			/////
			else{
				smartPathTo(currentWaypoint);
				Direction dir = DataCache.currentLoc.directionTo(Functions.internallocToLoc(currentWaypoint));
				if (vip){
					snailVIPNav(dir);
				} else{ 
					snailNav(dir);
				}
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Snail Navigation
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void dumbNav(MapLocation loc) throws GameActionException{
		snailNav(DataCache.currentLoc.directionTo(loc));
	}

	public static void dumbVIPNav(MapLocation loc) throws GameActionException{
		snailVIPNav(DataCache.currentLoc.directionTo(loc));
	}

	public static void snailNav(Direction chosenDirection) throws GameActionException{
		tryToMove(chosenDirection, true, rc, directionalLooks, allDirections);
	}

	public static void snailVIPNav(Direction chosenDirection) throws GameActionException{
		tryToVIPMove(chosenDirection, true, rc, directionalLooks, allDirections);
	}

	static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();


	static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
		if(selfAvoiding){
			MapLocation resultingLocation = rc.getLocation().add(dir);
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
						return false;
					}
				}
			}
		}
		return rc.canMove(dir);
	}

	static boolean canVIPMove(Direction dir, boolean selfAvoiding,RobotController rc){
		if(selfAvoiding){
			MapLocation resultingLocation = rc.getLocation().add(dir);
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)||DataCache.withinStructRange(resultingLocation)){
						return false;
					}
				}
			}
		}
		return rc.canMove(dir);
	}

	private static void tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(rc.isCoreReady()){
			snailTrail.remove(0);
			snailTrail.add(rc.getLocation());
			for(int directionalOffset:directionalLooks){

				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding,rc)){

					rc.move(trialDir);

					break;
				}

			}
		}
	}

	private static void tryToVIPMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(rc.isCoreReady()){
			snailTrail.remove(0);
			snailTrail.add(rc.getLocation());
			for(int directionalOffset:directionalLooks){

				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding,rc)){

					rc.move(trialDir);

					break;
				}

			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Simple Navigation
	////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	