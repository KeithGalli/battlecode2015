package oldbot;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Functions {
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	
	public static int[] xdirs = {0, 1, 1, 1, 0, -1, -1, -1};
	public static int[] ydirs = {1, 1, 0, -1, -1, -1, 0, 1};
	private static RobotController rc;
	
	public int[][] mapData;

	public static void init(RobotController rcIn){
		rc = rcIn;
	}
	
	
	public static int cheapPathExists(MapLocation start, MapLocation finish){
		MapLocation current = start;
		while (!current.equals(finish)){
			Direction dir = current.directionTo(finish);
			MapLocation trial = add(current, dir);
			if (NavSystem.mapData[trial.x][trial.y]==0){
				return NavSystem.voidID[trial.x][trial.y];
			}
			current=trial;
		}
		return 0;
	}
	
	public static MapLocation add(MapLocation start, Direction dir){
		return new MapLocation(start.x+xdirs[dir.ordinal()], start.y + ydirs[dir.ordinal()]);
	}
	
	public static int locsToInt(MapLocation[] pastrs){
		 int pastrInt = 0;
		 int multiplier = 1;
		 for (MapLocation pastr: pastrs){
			 pastrInt += locToInt(pastr)*multiplier;
			 multiplier*=10000;
		 }
		 return pastrInt;
	 }
	
	public static int locsToInt(ArrayList<MapLocation> locs){
		 int locInt = 0;
		 int multiplier = 1;
		 for (int i=0; i<locs.size(); i++){
			 locInt += locToInt(locs.get(i))*multiplier;
			 multiplier*=10000;
		 }
		 return locInt;
	 }


	 public static MapLocation[] intToLocs(int num){
		 MapLocation[] pastrs = new MapLocation[DataCache.numEnemyPastrs];
		 for (int i=0; i<DataCache.numEnemyPastrs; i++){
			 pastrs[i] = intToLoc(num%10000);
			 num/=10000;
		 }
		 return pastrs;
	 }
	 
		public static MapLocation findClosest(MapLocation[] manyLocs, MapLocation point){
			int closestDist = 10000000;
			int challengerDist = closestDist;
			MapLocation closestLoc = null;
			for(MapLocation m:manyLocs){
				challengerDist = point.distanceSquaredTo(m);
				if(challengerDist<closestDist){
					closestDist = challengerDist;
					closestLoc = m;
				}
			}
			return closestLoc;
		}
		public static MapLocation mladd(MapLocation m1, MapLocation m2){
			return new MapLocation(m1.x+m2.x,m1.y+m2.y);
		}
		
		public static MapLocation mlsubtract(MapLocation m1, MapLocation m2){
			return new MapLocation(m1.x-m2.x,m1.y-m2.y);
		}
		
		public static MapLocation mldivide(MapLocation bigM, int divisor){
			return new MapLocation(bigM.x/divisor, bigM.y/divisor);
		}
		
		public static MapLocation mlmultiply(MapLocation bigM, int factor){
			return new MapLocation(bigM.x*factor, bigM.y*factor);
		}
		
		public static int locToInt(MapLocation m){
			return (m.x*100 + m.y);
		}
		
		public static MapLocation intToLoc(int i){
			return new MapLocation(i/100,i%100);
		}
		
		public static void printPath(ArrayList<MapLocation> path, int bigBoxSize){
			for(MapLocation m:path){
				MapLocation actualLoc = bigBoxCenter(m,bigBoxSize);
				System.out.println("("+actualLoc.x+","+actualLoc.y+")");
			}
		}
		public static MapLocation bigBoxCenter(MapLocation bigBoxLoc, int bigBoxSize){
			return mladd(mlmultiply(bigBoxLoc,bigBoxSize),new MapLocation(bigBoxSize/2,bigBoxSize/2));
		}
		public static MapLocation[] robotsToLocations(Robot[] robotList,RobotController rc, boolean ignoreHQ) throws GameActionException{
			if(robotList.length==0)
				return new MapLocation[]{};
			ArrayList<MapLocation> robotLocs = new ArrayList<MapLocation>();
			for(int i=0;i<robotList.length;i++){
				Robot anEnemy = robotList[i];
				RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
				if(!ignoreHQ||anEnemyInfo.type!=RobotType.HQ)
					robotLocs.add(anEnemyInfo.location);
			}
			return robotLocs.toArray(new MapLocation[]{});
		}
		public static MapLocation meanLocation(MapLocation[] manyLocs){
			if(manyLocs.length==0)
				return null;
			MapLocation runningTotal = new MapLocation(0,0);
			for(MapLocation m:manyLocs){
				runningTotal = mladd(runningTotal,m);
			}
			return mldivide(runningTotal,manyLocs.length);
		}
		 public static int[] getClosestEnemy(Robot[] enemyRobots) throws GameActionException {
			 int closestDist = rc.getLocation().distanceSquaredTo(DataCache.enemyHQLocation);
			 MapLocation closestEnemy=rc.senseEnemyHQLocation(); // default to HQ

			 int dist = 0;
			 for (int i = enemyRobots.length; --i >= 0; ) {
				 RobotInfo arobotInfo = rc.senseRobotInfo(enemyRobots[i]);
				 dist = arobotInfo.location.distanceSquaredTo(rc.getLocation());
				 if (dist < closestDist){
					 closestDist = dist;
					 closestEnemy = arobotInfo.location;
				 }
			 }
			 int[] output = new int[4];
			 output[0] = closestDist;
			 output[1] = closestEnemy.x;
			 output[2] = closestEnemy.y;                
			 return output;
		 }

}
