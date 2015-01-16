package navbot;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class MapEngine {

	public static RobotController rc;

	public static int minx = 100000000;
	public static MapLocation minxloc = null;
	public static String minxteam = null;
	public static int maxx = -100000000;
	public static MapLocation maxxloc = null;
	public static String maxxteam = null;

	public static int miny = 100000000;
	public static MapLocation minyloc = null;
	public static String minyteam = null;
	public static int maxy = -100000000;
	public static MapLocation maxyloc = null;
	public static String maxyteam = null;

	public static int xmod = 0;
	public static int ymod = 0;

	public static int voidID=3;

	public static int xdim;
	public static int ydim;

	public static int[][] map;

	public static MapLocation internalMapCenter;

	public static List<MapLocation> senseQueue=new ArrayList<MapLocation>();

	public static Dictionary<Integer,ArrayList<MapLocation>> waypointDictHQ = new Hashtable<Integer,ArrayList<MapLocation>>();
	public static Dictionary<Integer,MapLocation[]> waypointDict = new Hashtable<Integer,MapLocation[]>();



	public static void HQinit(RobotController myRC) {
		rc = myRC;
		getMapParams();
		getMapEdges();
		setMapDim();
		BroadcastSystem.write(BroadcastSystem.xdimBand, xdim);
		BroadcastSystem.write(BroadcastSystem.ydimBand, ydim);
		//System.out.println("test");
		//Functions.displayArray(map);
		//System.out.println("test2");
		//System.out.println(xdim);
		//System.out.println(ydim);
		//System.out.println(Functions.locToInternalLoc(DataCache.ourHQ));
	}
	public static void STRUCTinit(RobotController myRC) {
		rc = myRC;

	}


	public static void UNITinit(RobotController myRC) {
		rc = myRC;
		xdim = BroadcastSystem.read(BroadcastSystem.xdimBand);
		ydim = BroadcastSystem.read(BroadcastSystem.ydimBand);
		internalMapCenter = new MapLocation(xdim/2, ydim/2);
	}

	public static void resetMapAndPrep(){
		senseQueue = new ArrayList<MapLocation>();
		waypointDictHQ = new Hashtable<Integer,ArrayList<MapLocation>>();

		resetVoidNums();
		setVoidNums();
		BroadcastSystem.write(2001, 1);
	}

	public static void scanTiles(MapLocation[] inputTiles){
		for (MapLocation tile: inputTiles){
			MapLocation internalTile = Functions.locToInternalLoc(tile);
			if (internalTile.x<xdim && internalTile.y<ydim){
			if (map[internalTile.x][internalTile.y] == 0){
				TerrainTile tileType = rc.senseTerrainTile(tile);
				if (tileType == TerrainTile.NORMAL){
					map[internalTile.x][internalTile.y] = 1;
				} else if (tileType == TerrainTile.VOID){
					map[internalTile.x][internalTile.y] = 2;
				} else if (tileType == TerrainTile.OFF_MAP){
					map[internalTile.x][internalTile.y] = -1;
				}

			}
		}
		}
	}

	public static void resetVoidNums(){
		for(int x=xdim;--x>=0;){
			for(int y=ydim;--y>=0;){
				if (map[x][y]>1){
					map[x][y]=2;
				}
			}
		}
	}

	public static void setVoidNums(){
		voidID = 3;
		for(int x=xdim;--x>=0;){
			for(int y=ydim;--y>=0;){
				// If find unlabeled void than propagate through void labeling each square
				if (map[x][y]==2) {
					waypointDictHQ.put(voidID, new ArrayList<MapLocation>());
					propagateVoid(x,y,voidID);
					voidID++;
				}
			}
		}
	}

	private static int propagateVoid(int x, int y, int id) {
		// Make sure square is void and it has yet to be labeled
		if (x>-1 & y>-1 & x<xdim & y<ydim){
			if (map[x][y]>1){
				if (map[x][y]==2){

					map[x][y] = id;
					int sum = 0; 
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
		if (x>-1 & y>-1 & x<xdim & y<ydim) {
			ArrayList<MapLocation> locs = waypointDictHQ.get(id);
			if(!locs.contains(new MapLocation(x,y))){
			locs.add(new MapLocation(x,y));
			waypointDictHQ.put(id, locs);
			}
			//1/15/15 why is this here?
			else{
				locs.remove(new MapLocation(x,y));
				waypointDictHQ.put(id, locs);
			}
		}
	}

	// public static void scanTiles(MapLocation[] inputTiles){
	// 	for (MapLocation tile: inputTiles){
	// 		MapLocation internalTile = Functions.locToInternalLoc(tile);
	// 		if (internalTile.x<xdim && internalTile.y<ydim){
	// 		if (map[internalTile.x][internalTile.y] == 0){
	// 			TerrainTile tileType = rc.senseTerrainTile(tile);
	// 			if (tileType == TerrainTile.NORMAL){
	// 				map[internalTile.x][internalTile.y] = 1;
	// 			} else if (tileType == TerrainTile.VOID){
	// 				int voidNum = checkAdjVoidTiles(internalTile);
	// 				if (voidNum==0){
	// 					map[internalTile.x][internalTile.y] = voidCounter;
	// 					voidCounter++;
	// 				} else{
	// 					map[internalTile.x][internalTile.y] = voidNum;
	// 				}
	// 			} else if (tileType == TerrainTile.OFF_MAP){
	// 				map[internalTile.x][internalTile.y] = -1;
	// 			}

	// 		}
	// 	}
	// 	}
	// 	resetVoidNums();
	// 	BroadcastSystem.write(2001, 1);

	// }

	// public static int checkAdjVoidTiles(MapLocation inputTile){
	// 	if (map[inputTile.x-1][inputTile.y]>1){
	// 		return map[inputTile.x-1][inputTile.y];
	// 	} else if (map[inputTile.x+1][inputTile.y]>1){
	// 		return map[inputTile.x+1][inputTile.y];
	// 	} else if (map[inputTile.x][inputTile.y-1]>1){
	// 		return map[inputTile.x][inputTile.y-1];
	// 	} else if (map[inputTile.x][inputTile.y+1]>1){
	// 		return map[inputTile.x][inputTile.y+1];
	// 	} else {
	// 		return 0;
	// 	}
	// }

	// public static void resetVoidNums(){
	// 	//this is probably useless
	// 	for (int x=1;x<xdim;x++){
	// 		if (map[x][0]>1){
	// 			if (map[x-1][0]>1 && map[x][0]!=map[x-1][0]){
	// 				map[x][0] = map[x-1][0];
	// 			}
	// 		}
	// 	}
	// 	//this is probably useless too
	// 	for (int y=1;y<ydim;y++){
	// 		if (map[0][y]>1){
	// 			if (map[0][y-1]>1 && map[0][y]!=map[0][y-1]){
	// 				map[0][y] = map[0][y-1];
	// 			}
	// 		}
	// 	}

	// 	for (int x=1;x<xdim;x++){
	// 		for (int y=1;y<ydim;y++){
	// 			if (map[x][y]>1){
	// 				if (map[x-1][y]>1 && map[x][y]!=map[x-1][y]){
	// 					map[x][y] = map[x-1][y];
	// 				}
	// 				else if (map[x][y-1]>1 && map[x][y]!=map[x][y-1]){
	// 					map[x][y] = map[x][y-1];
	// 				}
	// 			}
	// 		}
	// 	}
	// }



	public static MapLocation[] unitScan(MapLocation unitLoc){
		return MapLocation.getAllMapLocationsWithinRadiusSq(unitLoc, 24);
	}

	public static MapLocation[] structScan(MapLocation unitLoc){
		return MapLocation.getAllMapLocationsWithinRadiusSq(unitLoc, 35);
	}

	public static void setMapDim(){
		if (xmod != 0){
			xdim = (xmod - 2)*2 + (Math.abs(maxx-minx)+1);
		} else{
			xdim = 120;
		}

		if (ymod != 0){
			ydim = (ymod - 2)*2 + (Math.abs(maxy-miny)+1);
		} else{
			ydim = 120;
		}
		//initialized to 0. we'll do 1 for void and 2 for normal. 0 for unknown.
		map = new int[xdim][ydim];
		internalMapCenter = new MapLocation(xdim/2, ydim/2);

	}

	public static void getMapEdges(){
		if (minxteam == "our"){
			TerrainTile testTile = null;
			for (int i=1;i<8;i++){
				testTile = rc.senseTerrainTile(minxloc.add(-i,0));
				if (testTile == TerrainTile.OFF_MAP){
					xmod = i;
				}
			}
		} else{
			TerrainTile testTile = null;
			for (int i=1;i<8;i++){
				testTile = rc.senseTerrainTile(maxxloc.add(i,0));
				if (testTile == TerrainTile.OFF_MAP){
					xmod = i;
				}
			}
		}

		if (minyteam == "our"){
			TerrainTile testTile = null;
			for (int i=1;i<8;i++){
				testTile = rc.senseTerrainTile(minyloc.add(0,-i));
				if (testTile == TerrainTile.OFF_MAP){
					ymod = i;
				}
			}
		} else{
			TerrainTile testTile = null;
			for (int i=1;i<8;i++){
				testTile = rc.senseTerrainTile(maxyloc.add(0,i));
				if (testTile == TerrainTile.OFF_MAP){
					ymod = i;
				}
			}
		}
	}

	public static void getMapParams(){
		

		if(DataCache.ourHQ.x<minx){
			minx=DataCache.ourHQ.x;
			minxloc = DataCache.ourHQ;
			minxteam = "our";
		}
		if(DataCache.ourHQ.x>maxx){
			maxx=DataCache.ourHQ.x;
			maxxloc = DataCache.ourHQ;
			maxxteam = "our";
		}
		
		if(DataCache.ourHQ.y<miny){
			miny=DataCache.ourHQ.y;
			minyloc = DataCache.ourHQ;
			minyteam = "our";
		}
		if(DataCache.ourHQ.y>maxy){
			maxy=DataCache.ourHQ.y;
			maxyloc = DataCache.ourHQ;
			maxyteam = "our";
		}

		if(DataCache.enemyHQ.x<minx){
			minx=DataCache.enemyHQ.x;
			minxloc = DataCache.enemyHQ;
			minxteam = "enemy";
		}
		if(DataCache.enemyHQ.x>maxx){
			maxx=DataCache.enemyHQ.x;
			maxxloc = DataCache.enemyHQ;
			maxxteam = "enemy";
		}

		if(DataCache.enemyHQ.y<miny){
			miny=DataCache.enemyHQ.y;
			minyloc = DataCache.enemyHQ;
			minyteam = "enemy";
		}
		if(DataCache.enemyHQ.y>maxy){
			maxy=DataCache.enemyHQ.y;
			maxyloc = DataCache.enemyHQ;
			maxyteam = "enemy";
		}

		for (MapLocation loc: DataCache.enemyTowers){
			if(loc.x<minx){
				minx=loc.x;
				minxloc = loc;
				minxteam = "enemy";
			} else if(loc.x>maxx){
				maxx=loc.x;
				maxxloc = loc;
				maxxteam = "enemy";
			}
			if(loc.y<miny){
				miny=loc.y;
				minyloc = loc;
				minyteam = "enemy";
			}else if(loc.y>maxy){
				maxy=loc.y;
				maxyloc = loc;
				maxyteam = "enemy";
			}
		}

		for (MapLocation loc: DataCache.ourTowers){
			if(loc.x<minx){
				minx=loc.x;
				minxloc = loc;
				minxteam = "us";
			} else if(loc.x>maxx){
				maxx=loc.x;
				maxxloc = loc;
				maxxteam = "our";
			}
			if(loc.y<miny){
				miny=loc.y;
				minyloc = loc;
				minyteam = "our";
			}else if(loc.y>maxy){
				maxy=loc.y;
				maxyloc = loc;
				maxyteam = "our";
			}
		}
	}



}
	