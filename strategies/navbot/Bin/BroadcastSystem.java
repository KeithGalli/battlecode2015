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
	
	public static int waypointBand = 10000;

	public static int xdimBand = 205;
	public static int ydimBand = 206;

	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;
	}

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

	public static void write(int channel, int message) {
		if (rc != null) {
			try {
				rc.broadcast(channel, message);

			} catch (Exception e) {
				                  e.printStackTrace();
			}
		}
	}

	static void broadcastMapArray(int refchannel, int[][] dataArray) throws GameActionException{
		for(int x=MapEngine.xdim;--x>=0;){

			for(int y=MapEngine.ydim;--y>=0;){

				int index = y*MapEngine.xdim+x+refchannel;

				rc.broadcast(index, dataArray[x][y]);
			}
		}
		// System.out.println("////////broadcast////////");
		// Functions.displayArray(dataArray);
		// System.out.println("/////////////////////////");
	}
	
	// static void broadcast2MapArrays(int refchannel, int[][] dataArray1,  int[][] dataArray2) throws GameActionException{
	// 	for(int x=DataCache.mapWidth;--x>=0;){
	// 		for(int y=DataCache.mapHeight;--y>=0;){
	// 			int index = y*DataCache.mapWidth+x+refchannel;
	// 			rc.broadcast(index, dataArray1[x][y]*1000 + dataArray2[x][y]);
	// 		}
	// 	}
	// }
	
	static int[][] downloadMapArray(int refchannel) throws GameActionException {
		int[][] dataArray = new int[MapEngine.xdim][MapEngine.ydim];
		for(int x=MapEngine.xdim;--x>=0;){

			for(int y=MapEngine.ydim;--y>=0;){
				int index = y*MapEngine.xdim+x+refchannel;
				dataArray[x][y] = rc.readBroadcast(index);
			}
		}
		// System.out.println("////////download/////////");
		// Functions.displayArray(dataArray);
		// System.out.println("/////////////////////////");
		return dataArray;
	}
	
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
	
	
	public static Dictionary<Integer,MapLocation[]> receiveMapDataDict(){
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
	
	public static void prepareandsendMapDataDict(Dictionary<Integer,ArrayList<MapLocation>> dataDict){

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
}
	