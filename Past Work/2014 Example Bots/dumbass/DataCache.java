package dumbass;

import java.util.ArrayList;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class DataCache {

	public static BaseRobot robot;
	public static RobotController rc;

	public static MapLocation currentLocation;
	public static MapLocation ourHQLocation;
	public static MapLocation enemyHQLocation;
	public static int rushDistSquared;
	public static int rushDist;

	public static Direction[] directionArray = Direction.values();

	public static boolean onCycle;

	// Map width
	public static int mapWidth;
	public static int mapHeight;

	// Round variables - army sizes
	// Allied robots
	public static int numAlliedRobots;


	public static int numNearbyAlliedRobots;

	public static int numEnemyRobots;
	// Enemy robots

	public static Robot[] nearbyEnemyRobots;

	public static int numNearbyEnemySoldiers;
	
	public static Robot[] enemyRobots;
	
	public static MapLocation[] alliedPastrs;
	public static int numAlliedPastrs;
	public static MapLocation closestAlliedPastr;

	public static MapLocation[] enemyPastrs;
	public static int numEnemyPastrs;
	public static MapLocation closestEnemyPastr;
	
	public static MapLocation[] potentialPastrs;

	public static Team team;


	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;

		currentLocation = rc.getLocation();
		team = rc.getTeam();
		ourHQLocation = rc.senseHQLocation();
		enemyHQLocation = rc.senseEnemyHQLocation();
		rushDistSquared = ourHQLocation.distanceSquaredTo(enemyHQLocation);
		rushDist = (int) Math.sqrt(rushDistSquared);

		mapWidth = rc.getMapWidth();
		mapHeight = rc.getMapHeight();
	}

	/**
	 * A function that updates round variables
	 */
	 public static void updateRoundVariables() throws GameActionException {
		currentLocation = rc.getLocation();

		numAlliedRobots = rc.senseRobotCount();
		enemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent());
		numEnemyRobots = enemyRobots.length;

		numNearbyAlliedRobots = rc.senseNearbyGameObjects(Robot.class, 14, rc.getTeam()).length;
		
		alliedPastrs = rc.sensePastrLocations(rc.getTeam());
		enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());

		numNearbyEnemySoldiers = 0;
		for (int i = enemyRobots.length; --i >= 0; ) {
			RobotInfo robotInfo = rc.senseRobotInfo(enemyRobots[i]);
			if (robotInfo.type == RobotType.SOLDIER) {
				numNearbyEnemySoldiers++;
			}
		}
	 }
	 public static void broadcastPastrs(){
		 BroadcastSystem.write(robot.enemyPastrChannel, pastrsToInt(enemyPastrs));
		 BroadcastSystem.write(robot.alliedPastrChannel, pastrsToInt(alliedPastrs));
	 }

	 public static void receivePastrs(){
		 enemyPastrs = intToPastrs(BroadcastSystem.read(robot.enemyPastrChannel));
		 numEnemyPastrs = enemyPastrs.length;
		 closestEnemyPastr = findClosest(enemyPastrs, rc.getLocation());
		 alliedPastrs = intToPastrs(BroadcastSystem.read(robot.alliedPastrChannel));
		 numAlliedPastrs = alliedPastrs.length;
		 closestAlliedPastr = findClosest(alliedPastrs, rc.getLocation());
	 }

	 public static int pastrsToInt(MapLocation[] pastrs){
		 int pastrInt = 0;
		 int multiplier = 1;
		 for (MapLocation pastr: pastrs){
			 pastrInt += locToInt(pastr)*multiplier;
			 multiplier*=10000;
		 }
		 return pastrInt;
	 }

	 public static MapLocation[] intToPastrs(int num){
		 MapLocation[] pastrs = new MapLocation[numEnemyPastrs];
		 for (int i=0; i<numEnemyPastrs; i++){
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
		public static MapLocation[] robotsToLocations(Robot[] robotList,RobotController rc) throws GameActionException{
			MapLocation[] robotLocations = new MapLocation[robotList.length];
			for(int i=0;i<robotList.length;i++){
				Robot anEnemy = robotList[i];
				RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
				robotLocations[i] = anEnemyInfo.location;
			}
			return robotLocations;
		}
		 public static int[] getClosestEnemy(Robot[] enemyRobots) throws GameActionException {
			 int closestDist = rc.getLocation().distanceSquaredTo(enemyHQLocation);
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