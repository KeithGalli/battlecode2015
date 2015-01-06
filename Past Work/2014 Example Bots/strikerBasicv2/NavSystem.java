package strikerBasicv2;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class NavSystem {
	
	public static RobotController rc;
	
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
	public static int[][] voidID;//unique ID for each contiguous void
	public static int[][] waypointID;//unique ID for each contiguous void
	public static int[][] minionData;//unique ID for each contiguous void
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	
	public static ArrayList<MapLocation> nodes;
	public static ArrayList<ArrayList<Integer>> nodeConnections;
	public static Dictionary<Integer,ArrayList<MapLocation>> pathingDictionary = new Hashtable<Integer,ArrayList<MapLocation>>();
	public static Dictionary<Integer,ArrayList<Direction>> directionDictionary = new Hashtable<Integer,ArrayList<Direction>>();
	public static Dictionary<Integer,ArrayList<Direction>> direction2Dictionary = new Hashtable<Integer,ArrayList<Direction>>();
	
	public static int[] distances;
	public static int[] fromNode;
	
	public static void initNavSystem(BaseRobot myRobot) throws GameActionException{
		rc = myRobot.rc;
		width = DataCache.mapHeight;
		height = DataCache.mapWidth;
		rc.broadcast(height*width-1, -9999);//tell minions that the minionData array has not yet been uploaded
		//load all terrain info into local arrays
		updateInternalMap(rc);//3 rounds
		System.out.println();
		displayArray(mapData);
		
		displayArray(waypointID);
		//locate effective pathways and directions to those pathways
		makeMap();//54 rounds (30x30 map). Interrupt this with other actions.
		//make a network out of the pathways
		//makeNetwork(rc);//25 rounds.
		//assemble minion data for broadcasting
		//prepareMinionData();//4 rounds
		//post minion data to broadcast network
		//broadcastMinionData(rc);//1 round
		//print the visual
		//runTests();
	}
	
	private static void updateInternalMap(RobotController rc){//can take several rounds, but ultimately saves time
		mapData = new int[width+4][height+4];
		//move battlecode map representation to an internal integer array to save bytecode
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				setMapData(x,y,3-rc.senseTerrainTile(new MapLocation(x,y)).ordinal());//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
			}
		}
		//put traversible tiles outside the map so that the contiguous void checker doesn't go there.
		setOuterRing(3);
	}
	
	private static void setOuterRing(int val){
		for(int x=-2;x<width+2;x++){
			setMapData(x,-2,val);
			setMapData(x,height+1,val);
		}
		for(int y=-2;y<width+2;y++){
			setMapData(-2,y,val);
			setMapData(width+1,y,val);
		}
	}
	
	private static void makeMap() throws GameActionException{
		//want to find corridors between contiguous voids
		pathingData = new Direction[width][height];//direction to arrive at this tile fastest
		distanceData = new int[width][height];//closest distance to this tile
		voidID = new int[width+4][height+4];//unique ID for each contiguous void
		ArrayList<MapLocation> contiguousVoids = new ArrayList<MapLocation>();
		int voidID = 1;
		for(int x=-1;x<=width;x++){
			for(int y=-1;y<=height;y++){
				if(getMapData(x,y)<2&&getVoidID(x,y)==0){//void or offmap && voidID is not set (default 0)
					//find all contiguous voids
					findContiguousVoids(contiguousVoids,new MapLocation(x,y),voidID);
					voidID++;
					//TODO if there less than 3 void tiles in a clump, it's not a significant obstacle. Skip it here, and it should simplify pathing computations later. It can be handled with bug.
					//System.out.println("contig void reached "+contiguousVoids.size());
				}
			}
		}
		
		//fill contiguous voids until they intersect
		//findWaypoints(contiguousVoids);
	}
	
	private static void findContiguousVoids(ArrayList<MapLocation> contiguousVoids,MapLocation start,int voidID) throws GameActionException{
		ArrayList<MapLocation> outermost = new ArrayList<MapLocation>();
		outermost.add(start);
		addContiguousVoid(contiguousVoids,start,voidID);
		while(outermost.size()>0){
			outermost = findOutermostVoids(contiguousVoids, outermost,voidID);
			//RobotPlayer.rc.yield();//TODO this slows down the computation?
			//RobotPlayer.tryToSpawn();//this method takes a while, so keep up with the spawning.
		}
	}
	
	private static ArrayList<MapLocation> findOutermostVoids(ArrayList<MapLocation> contiguousVoids,ArrayList<MapLocation> outermost,int voidID) throws GameActionException{
		ArrayList<MapLocation> newOutermost = new ArrayList<MapLocation>();
		
		for(MapLocation current:outermost){
			int waypointChecker = 0;
			//get new outermost
			for (Direction d: orthoDirs){
				waypointChecker*=10;
				MapLocation trial = current.add(d);
				//System.out.println("trying ("+trial.x+","+trial.y+") ... mapdata "+(getMapData(trial)<2)+", voidID "+(getVoidID(trial)==0));
				if(getMapData(trial)<2&&getVoidID(trial)==0){//void or offmap && voidID is not set (default 0)
					newOutermost.add(trial);
					addContiguousVoid(contiguousVoids,trial,voidID);
				}
				else{
					waypointChecker+=1;
				}
				
			}
			if (waypointChecker==1100){
				setWaypointID(current.x, current.y, voidID);
			}
			else if (waypointChecker==1001){
				setWaypointID(current.x, current.y, voidID);
			}
			else if (waypointChecker==110){
				setWaypointID(current.x, current.y, voidID);
			}
			else if (waypointChecker==11){
				setWaypointID(current.x, current.y, voidID);
			}
		}
		return newOutermost;
	}
	
	private static void addContiguousVoid(ArrayList<MapLocation> contiguousVoids,MapLocation site, int voidID){
		//for each void, set the ID and add it to the contiguous voids list, which will start the "flood"
		setVoidID(site.x,site.y,voidID);
		contiguousVoids.add(site);
	}
	

	//access map info in local arrays with an offset to accommodate tiles outside the map
		private static int getMapData(int x, int y){
			return mapData[x+2][y+2];
		}	
		private static int getMapData(MapLocation m){
			return getMapData(m.x,m.y);
		}
		private static void setMapData(int x, int y, int val){
			mapData[x+2][y+2] = val;
		}
		private static int getVoidID(int x, int y){
			return voidID[x+2][y+2];
		}
		private static int getVoidID(MapLocation m){
			return getVoidID(m.x,m.y);
		}
		private static void setVoidID(int x, int y, int val){
			voidID[x+2][y+2] = val;
		}
		private static void setWaypointID(int x, int y, int voidID){
			waypointID[x+2][y+2] = voidID;
		}
		
		private static void displayArray(int[][] intArray){
			for(int y = 0;y<intArray.length;y++){
				String line = "";
				for(int x=0;x<intArray[0].length;x++){
					//line+=(voidID[x][y]==-1)?"_":".";
					int i = intArray[x][y];
					if(i==-1){//a path
						line+="-";
					}else if (i==-9999){//open terrain
						line+=".";
					}else if (i==-1337){//a void
						line+="X";
					}else{
						line+=i;
					}
				}
				System.out.println(line);
			}
		}

}
