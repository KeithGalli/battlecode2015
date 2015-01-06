package dumbass;

import java.util.ArrayList;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class BroadcastSystem {
	public static BaseRobot robot;
	public static RobotController rc;
	static int[] lengthOfEachPath = new int[100];

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
	
	public static ArrayList<MapLocation> downloadPath() throws GameActionException {
		ArrayList<MapLocation> downloadedPath = new ArrayList<MapLocation>();
		int locationInt = rc.readBroadcast(robot.myBand+1);
		while(locationInt>=0){
			downloadedPath.add(DataCache.intToLoc(locationInt));
			locationInt = rc.readBroadcast(robot.myBand+1+downloadedPath.size());
		}
		robot.myBand = -locationInt*100;
		return downloadedPath;
	}
	

	public static void findPathAndBroadcast(int bandID,MapLocation start, MapLocation goal, int bigBoxSize, int joinSquadNo) throws GameActionException{
		//tell robots where to go
		//the unit will not pathfind if the broadcast goal (for this band ID) is the same as the one currently on the message channel
		int band = bandID*100;
		MapLocation pathGoesTo = DataCache.intToLoc(rc.readBroadcast(band+lengthOfEachPath[bandID]));
		if(!pathGoesTo.equals(DataCache.mldivide(goal,bigBoxSize))){
			ArrayList<MapLocation> path = NavSystem.pathTo(DataCache.mldivide(rc.getLocation(),bigBoxSize), DataCache.mldivide(goal,bigBoxSize), 100000);
			
			rc.broadcast(band, Clock.getRoundNum());
			for(int i=path.size()-1;i>=0;i--){
				System.out.println(DataCache.mlmultiply(path.get(i), bigBoxSize));
				rc.broadcast(band+i+1, DataCache.locToInt(path.get(i)));
			}
			lengthOfEachPath[bandID]= path.size();
			rc.broadcast(band+lengthOfEachPath[bandID]+1, -joinSquadNo);
		}
	}

}
