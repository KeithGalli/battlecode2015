package navbot;


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class BroadcastSystem {

	public static BaseRobot robot;
	public static RobotController rc;

	static int[] lengthOfEachPath = new int[100];
	
	//channel that we begin sending waypoints (corners)
	public static int waypointBand = 10000;

	//channel that we send map data
	public static int dataBand = 30000;

	//sending the map dimensions
	public static int xdimBand = 205;
	public static int ydimBand = 206;


	//INITIALIZATION
	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;
	}

	//READ BROADCAST w/ error message
	public static int read(int channel) {
		try {
			if (rc != null) {
				int message = rc.readBroadcast(channel);
				return message;
			}
			return -9999;
		} catch (Exception e) {
			return -9999;
		}
	}

	//WRITE BROADCAST w/ error message
	public static void write(int channel, int message) {
		if (rc != null) {
			try {
				rc.broadcast(channel, message);
			} catch (Exception e) {
				                  e.printStackTrace();
			}
		}
	}

	//Broadcast a 2D array of information
	//CHANNELS: refchannel + ydim*xdim (~10,000)
	//USED IN: old method for hq to broadcast map information
	static void broadcastMapArray(int refchannel, int[][] dataArray) throws GameActionException{
		for(int x=MapEngine.xdim;--x>=0;){
			for(int y=MapEngine.ydim;--y>=0;){
				int index = y*MapEngine.xdim+x+refchannel;
				rc.broadcast(index, dataArray[x][y]);
			}
		}
	}

	//Download a 2D array of information
	//CHANNELS: refchannel + ydim*xdim (~10,000)
	//USED IN: old method for robots to download map information
	static int[][] downloadMapArray(int refchannel) throws GameActionException {
		int[][] dataArray = new int[MapEngine.xdim][MapEngine.ydim];
		for(int x=MapEngine.xdim;--x>=0;){

			for(int y=MapEngine.ydim;--y>=0;){
				int index = y*MapEngine.xdim+x+refchannel;
				dataArray[x][y] = rc.readBroadcast(index);
			}

		}
		return dataArray;
	}

	
	// static void broadcast2MapArrays(int refchannel, int[][] dataArray1,  int[][] dataArray2) throws GameActionException{
	// 	for(int x=DataCache.mapWidth;--x>=0;){
	// 		for(int y=DataCache.mapHeight;--y>=0;){
	// 			int index = y*DataCache.mapWidth+x+refchannel;
	// 			rc.broadcast(index, dataArray1[x][y]*1000 + dataArray2[x][y]);
	// 		}
	// 	}
	// }
	
	// static int[][][] download2MapArray(int refchannel) throws GameActionException {
	// 	int[][][] dataArray = new int[2][DataCache.mapWidth][DataCache.mapHeight];
	// 	for(int x=DataCache.mapWidth;--x>=0;){
	// 		for(int y=DataCache.mapHeight;--y>=0;){
	// 			int index = y*DataCache.mapWidth+x+refchannel;
	// 			dataArray[0][x][y] = rc.readBroadcast(index)/1000;
	// 			dataArray[1][x][y] = rc.readBroadcast(index)%1000;
	// 		}
	// 	}
	// 	return dataArray;
	// }
	
	
	
	//BROADCAST MAP DATA DICTIONARY
	//dataDict = {(x1,y1):1, (x2,y2):1, ... (xn,yn):4}
	//USED IN: method for HQ to send map information (normals, void numbers, etc)
	//CHANNELS: dataBand + 2*dataDict.size() + 1

	public static void prepareandsendMapDataDict(Dictionary<MapLocation, Integer> dataDict){
		write(dataBand, dataDict.size());
		int count = 1;

		Enumeration<MapLocation> enumKey = dataDict.keys();
		while(enumKey.hasMoreElements()){
			MapLocation key = enumKey.nextElement();
			Integer val = dataDict.get(key);
			write(dataBand+count, Functions.locToInt(key));

			count++;
			write(dataBand+count, val);

			count++;
		}
	}

	//DOWNLOAD MAP DATA DICTIONARY
	//USED IN: method for robots to efficiently download map information
	//CHANNELS: dataBand + 2*dataDict.size() + 1

	public static void receiveMapDataDict(){
		int size = BroadcastSystem.read(dataBand);
		//System.out.println(size);
		int count = 1;
		for (int i=0; i<size; i++){


			MapLocation loc = Functions.intToLoc(read(dataBand+count));
			count++;
			int val = read(dataBand+count);

			count++;
			MapEngine.map[loc.x][loc.y] = val;
		}
	}

	//BROADCAST WAYPOINT DATA DICTIONARY
	//USED IN: method for hq to efficiently broadcast corner information
	//dataDict = {3: [(x1,y1)...(xn,yn)], 4: [(x1,y1)...(xn,yn)]... n: [(x1,y1)...(xn,yn)]}
	//CHANNELS: dataBand + dataDict.size() + sum(dataDict.get(i).size()) for all i
	
	public static void prepareandsendWaypointDict(Dictionary<Integer,ArrayList<MapLocation>> dataDict){

		write(waypointBand, MapEngine.voidID);

		int count = 1;
		for (int i = 3; i<MapEngine.voidID; i++){
			write(waypointBand+count, dataDict.get(i).size());
			count++;
			for (int j=0; j<dataDict.get(i).size(); j++){
				write(waypointBand+count, Functions.locToInt(dataDict.get(i).get(j)));
				count++;
			}
		}
	}

	//DOWNLOAD WAYPOINT DATA DICTIONARY
	//USED IN: method for robot to efficiently download corner information
	//dataDict = {3: [(x1,y1)...(xn,yn)], 4: [(x1,y1)...(xn,yn)]... n: [(x1,y1)...(xn,yn)]}
	//CHANNELS: dataBand + dataDict.size() + sum(dataDict.get(i).size()) for all i

	public static Dictionary<Integer,MapLocation[]> receiveWaypointDict(){
		Dictionary<Integer,MapLocation[]> datadict = new Hashtable<Integer,MapLocation[]>();
		int size = BroadcastSystem.read(waypointBand);
		int arraysize = 0;
		int count = 1;
		for (int i = 3; i<size; i++){
			arraysize = read(waypointBand+count);
			MapLocation[] locs = new MapLocation[arraysize];
			count++;
			for (int j = 0; j<arraysize; j++){
				locs[j] = Functions.intToLoc(read(waypointBand+count));
				count++;
			}
			datadict.put(i, locs);
			
		}
		return datadict;
		
		
	}
}
	