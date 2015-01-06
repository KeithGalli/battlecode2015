package paulTheConqueror;

import java.util.ArrayList;

import battlecode.common.*;

public class Comms{
	
	static RobotController rc;
	static int[] lengthOfEachPath = new int[100];

	public static ArrayList<MapLocation> downloadPath() throws GameActionException {
		ArrayList<MapLocation> downloadedPath = new ArrayList<MapLocation>();
		int locationInt = rc.readBroadcast(RobotPlayer.myBand+1);
		while(locationInt>=0){
			downloadedPath.add(VectorFunctions.intToLoc(locationInt));
			locationInt = rc.readBroadcast(RobotPlayer.myBand+1+downloadedPath.size());
		}
		RobotPlayer.myBand = -locationInt*100;
		return downloadedPath;
	}
	

	public static void findPathAndBroadcast(int bandID,MapLocation start, MapLocation goal, int bigBoxSize, int joinSquadNo) throws GameActionException{
		//tell robots where to go
		//the unit will not pathfind if the broadcast goal (for this band ID) is the same as the one currently on the message channel
		int band = bandID*100;
		MapLocation pathGoesTo = VectorFunctions.intToLoc(rc.readBroadcast(band+lengthOfEachPath[bandID]));
		if(!pathGoesTo.equals(VectorFunctions.mldivide(goal,bigBoxSize))){
			ArrayList<MapLocation> path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
			rc.broadcast(band, Clock.getRoundNum());
			for(int i=path.size()-1;i>=0;i--){
				rc.broadcast(band+i+1, VectorFunctions.locToInt(path.get(i)));
			}
			lengthOfEachPath[bandID]= path.size();
			rc.broadcast(band+lengthOfEachPath[bandID]+1, -joinSquadNo);
		}
	}
	
}