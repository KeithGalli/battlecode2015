package launchers;

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

	public static int xdim;
	public static int ydim;

	public static int[][] map;

	public static List<MapLocation> senseQueue=new ArrayList<MapLocation>();

	public static void HQinit(RobotController myRC) {
		rc = myRC;
		getMapParams();
		getMapEdges();
		setMapDim();
		//System.out.println("test");
		Functions.displayArray(map);
		//System.out.println("test2");
		//System.out.println(xdim);
		//System.out.println(ydim);
	}
	public static void STRUCTinit(RobotController myRC) {
		rc = myRC;

	}


	public static void UNITinit(RobotController myRC) {
		rc = myRC;
	}

	public static void scanTiles(MapLocation[] inputTiles){
		
	}



	public static MapLocation[] unitScan(MapLocation currLoc){
		return MapLocation.getAllMapLocationsWithinRadiusSq(currLoc, 24);
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
	