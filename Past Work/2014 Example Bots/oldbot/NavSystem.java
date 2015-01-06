package oldbot;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import battlecode.common.*;

public class NavSystem{
	//initNetworkPathing creates a node-edge structure for navigation
	//ArrayList<MapLocation> testPath = findPath(2,5); gives a list of maplocations between nodes 2 and 5
	//for minions to reach nodes, minionData has directions to the closest edge.
	//once on the edge, the minion can follow it to either node. edge value e.g. 1002003: Edge from node 1 to 2, travel dir 3.
	//on the nodes, the value is -1000+nodeID

	//suggested usage: coordinated attacks
	//robots traverse network on their own, quite easily
	//HQ posts flags at nodes, such as wait here, or exit in direction 3.
	//the "wait here" flag requires the robot move aside a little to let others in, and keep checking the node post
	//this allows many robots to coordinate an attack

	//suggested usage: building towers
	//robots traversing will naturally create clumps of cows
	//the hq locates a good clump, traces it to a path location
	//the hq posts a maplocation target on a path somewhere
	//a soldier who comes by that path (by chance) checks the message array and finds the maplocation target
	//that soldier can go straight to the target and build something there, no pathing computation needed.

	public static int height;
	public static int width;
	public static int[][] voidID;
	public static int[][] mapData;

	public static MapLocation currentWaypoint = new MapLocation(-1, -1);
	public static MapLocation previousWaypoint = new MapLocation(-1, -1);

	public static boolean pathToGoal = false;

	public static boolean atGoal = false;

	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static Direction[] diagDirs = {Direction.NORTH_EAST,Direction.NORTH_WEST,Direction.SOUTH_EAST,Direction.SOUTH_WEST};

	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction allDirections[] = Direction.values();

	public static Dictionary<Integer,ArrayList<MapLocation>> waypointDictHQ = new Hashtable<Integer,ArrayList<MapLocation>>();
	public static Dictionary<Integer,MapLocation[]> waypointDict = new Hashtable<Integer,MapLocation[]>();


	public static int voidIDnum;
	public static RobotController rc;

	public static void Soldierinit(RobotController rcIn){
		rc=rcIn;
	}

	public static void HQinit(RobotController rcIn){
		rc=rcIn;
		voidIDnum = 0;
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		mapData = getMapData();
		System.out.println("done-navsystem");
		setVoidID();

		//setWaypoints();
		System.out.println("done-waypoints");


		//displayArray(mapData);
		//displayArray(voidID);
		//System.out.println(waypointDictHQ);
		//	displayArray(waypointData);
		//System.out.println(Functions.pathExists(rc.senseHQLocation(), rc.senseEnemyHQLocation()));
	}

	//2 = normal, 1= road, 0 = void
	private static int[][] getMapData() {
		mapData = new int[width][height];
		// TODO Auto-generated method stub
		for(int x=width;--x>=0;){
			for(int y=height;--y>=0;){
				mapData[x][y] = 2 - rc.senseTerrainTile(new MapLocation(x,y)).ordinal();
			}
		}
		return mapData;
	}

	private static void setVoidID() {
		voidID = new int[width][height];
		int id = 1;
		for(int x=width;--x>=0;){
			for(int y=height;--y>=0;){
				// If find unlabeled void than propagate through void labeling each square
				if (mapData[x][y]==0 && voidID[x][y]==0) {
					waypointDictHQ.put(id, new ArrayList<MapLocation>());
					propagateVoid(x,y,id);
					id++;
				}
			}
		}
	}

	private static int propagateVoid(int x, int y, int id) {
		// Make sure square is void and it has yet to be labeled
		if (x>-1 & y>-1 & x<DataCache.mapWidth & y<DataCache.mapHeight){
			if (mapData[x][y] == 0){
				if(voidID[x][y] == 0) {

					int sum = 0; 
					voidID[x][y] = id;
					sum |= propagateVoid(x-1, y, id);
					sum |= propagateVoid(x, y-1, id) << 1;
					sum |= propagateVoid(x+1, y, id) << 2;
					sum |= propagateVoid(x, y+1, id) << 3;
					// add waypoint if we are at corner of void

					switch (sum) {
					case 0:
						addWayPoint(x+1, y-1, id);
						addWayPoint(x+1, y+1, id);
						addWayPoint(x-1, y-1, id);
						addWayPoint(x-1, y+1, id);
						break;
					case 1:
						addWayPoint(x+1, y-1, id);
						addWayPoint(x+1, y+1, id);
						break;
					case 2:
						addWayPoint(x-1, y+1, id);
						addWayPoint(x+1, y+1, id);
						break;
					case 3: 
						addWayPoint(x+1,y+1,id);
						break;
					case 4:
						addWayPoint(x-1, y-1, id);
						addWayPoint(x-1, y+1, id);
						break;
					case 6: 
						addWayPoint(x-1,y+1,id);
						break;
					case 8:
						addWayPoint(x-1, y-1, id);
						addWayPoint(x+1, y-1, id);
						break;
					case 9: 
						addWayPoint(x+1,y-1,id);
						break;
					case 12: 
						addWayPoint(x-1,y-1,id);
						break;
					}
				}
				return 1;
			}

			return 0;
		}
		return 0;
	}

	private static void addWayPoint(int x, int y, int id) {
		if (x>-1 & y>-1 & x<DataCache.mapWidth & y<DataCache.mapHeight) {
			ArrayList<MapLocation> locs = waypointDictHQ.get(id);
			if(!locs.contains(new MapLocation(x,y))){
			locs.add(new MapLocation(x,y));
			waypointDictHQ.put(id, locs);
			}
			else{
				locs.remove(new MapLocation(x,y));
				waypointDictHQ.put(id, locs);
			}
		}
	}

	private static void setVoidIDold() {
		voidID = new int[width][height];
		for(int x=0;x<width;x++){
			
			for(int y=0;y<height;y++){
				if (mapData[x][y]==0){ //if a void
					MapLocation current = new MapLocation(x, y);
					for (Direction dir: orthoDirs){
						MapLocation trial = current.add(dir);
						if (trial.x<width&&trial.y<height&&trial.x>0&&trial.y>0){
							if ((Integer)voidID[trial.x][trial.y]!=0){
								voidID[current.x][current.y]=voidID[trial.x][trial.y];
							}
						}
					}
					if ((Integer) voidID[current.x][current.y] == 0){
						voidIDnum++;
						voidID[current.x][current.y] = voidIDnum;
					}
				}

			}
		}
	}

	private static void setWaypoints(){
		//waypointData = new int[width][height];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				if (mapData[x][y]!=0){
					int[] associatedVoid = new int[4];
					int voidcount = 0;
					MapLocation current = new MapLocation(x, y);
					int cornerCheck = 0;
					for (Direction dir: diagDirs){
						MapLocation trial = current.add(dir);
						if (trial.x<width&&trial.y<height&&trial.x>=0&&trial.y>=0){
							if (mapData[trial.x][trial.y]==0){
								cornerCheck++;
								associatedVoid[voidcount] = voidID[trial.x][trial.y];
								voidcount++;
							}
						}
					}
					for (Direction dir: orthoDirs){
						MapLocation trial = current.add(dir);
						if (trial.x<width&&trial.y<height&&trial.x>=0&&trial.y>=0){
							if (mapData[trial.x][trial.y]==0){
								cornerCheck+=10;
							}
						}
					}
					if (cornerCheck>0 && cornerCheck<=4){
						for (int i=0; i<4; i++){
							for (int j=0; j<4; j++){
								if(associatedVoid[j]==associatedVoid[i]&&j!=i){
									associatedVoid[j]=0;
									associatedVoid[i]=0;
								}
							}
						}
						for (int voidnum: associatedVoid){
							if(voidnum != 0){
								ArrayList<MapLocation> locs = waypointDictHQ.get(voidnum);
								if (locs!=null&&!locs.contains(voidnum)){
									locs.add(new MapLocation(current.x, current.y));
									waypointDictHQ.put(voidnum, locs);
								}
								else{
									ArrayList<MapLocation> newloc = new ArrayList<MapLocation>();
									newloc.add(new MapLocation(current.x, current.y));
									waypointDictHQ.put(voidnum, newloc);
								}
							}
						}
					}
				}
			}
		}
	}
	//////////////////////////////////////////////
	/// PATH BUILDING
	//////////////////////////////////////////////
	public static int pathExists(MapLocation start, MapLocation finish){
		MapLocation current = start;
		while (!current.equals(finish)){
			Direction dir = current.directionTo(finish);
			MapLocation trial = current.add(dir);
			if (NavSystem.mapData[trial.x][trial.y]==0){
				return NavSystem.voidID[trial.x][trial.y];
			}
			current=trial;
		}
		return 0;
	}

	public static MapLocation closestWaypoint(int voidID){
		int minDistance = 10000;
		MapLocation bestWaypoint = null;

		for (MapLocation loc: waypointDict.get(voidID)){
			int testdist = DataCache.currentLocation.distanceSquaredTo(loc);
			if (testdist<minDistance && pathExists(DataCache.currentLocation, loc)==0 && !previousWaypoint.equals(loc)&&!DataCache.currentLocation.equals(loc)){
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
			bestWaypoint = Functions.findClosest(waypointDict.get(voidID), DataCache.currentLocation);
		}
		return bestWaypoint;
	}

	public static void blindPathTo(MapLocation goal) throws GameActionException{
		int obstacle = pathExists(DataCache.currentLocation, goal);
		if (obstacle == 0){
			currentWaypoint = goal;
			pathToGoal = true;
		}
		else{
			currentWaypoint = closestWaypoint(obstacle);
			pathToGoal = false;
			//System.out.println(previousWaypoint);
			//System.out.println(currentWaypoint);
		}
	}

	public static void blindNav(MapLocation goal) throws GameActionException{
		rc.setIndicatorString(1, Integer.toString(Functions.locToInt(currentWaypoint)));
		atGoal = false;
		if (currentWaypoint.equals(new MapLocation(-1, -1))){
			blindPathTo(goal);
			if(pathToGoal){
				Direction dir = DataCache.currentLocation.directionTo(goal);
				snailNav(dir);
			}
			else{
				Direction dir = DataCache.currentLocation.directionTo(currentWaypoint);
				snailNav(dir);
			}
		}
		if (DataCache.currentLocation.equals(currentWaypoint)){
			rc.setIndicatorString(2, Integer.toString(pathExists(DataCache.currentLocation, goal)));
			if(DataCache.currentLocation.equals(goal)){
				currentWaypoint=new MapLocation(-1, -1);
				previousWaypoint=new MapLocation(-1, -1);
				atGoal =true;
			}
			else{
				blindPathTo(goal);
				if(pathToGoal){

					Direction dir = DataCache.currentLocation.directionTo(goal);
					snailNav(dir);
				}
				else{
					Direction dir = DataCache.currentLocation.directionTo(currentWaypoint);
					snailNav(dir);
				}
			}

		}
		else{
			Direction dir = DataCache.currentLocation.directionTo(currentWaypoint);
			snailNav(dir);
		}
	}


	static void displayArray(int[][] intArray){
		for(int y = 0;y<intArray.length;y++){
			String line = "";
			for(int x=0;x<intArray[0].length;x++){
				//line+=(voidID[x][y]==-1)?"_":".";
				int i = intArray[x][y];
				if((Integer)i==null){//a path
					line+="X";
				}else{
					line+=i;
				}
			}
			System.out.println(line);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	//Snail Navigation
	///////////////////////////////////////////////////////////////////////////
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
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)||resultingLocation.distanceSquaredTo(DataCache.enemyHQLocation)<=16){
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
		if(rc.isActive()){
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
		if(rc.isActive()){
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