package strikerBasic;

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
        public static int numAlliedSoldiers;
        public static int numAlliedPastures;
        
        public static int numNearbyAlliedRobots;
        public static int numNearbyAlliedSoldiers;
        public static int numNearbyAlliedPastures;

        public static int numEnemyRobots;
        // Enemy robots
        
        public static Robot[] nearbyEnemyRobots;
        
        public static int numNearbyEnemySoldiers;
        
        public static MapLocation[] enemyPastures;
        public static int numEnemyPastures;
    	public static MapLocation closestPastr;
        
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
//                onCycle = Clock.getRoundNum() % GameConstants.CHANNEL_CYCLE == 0 && Clock.getRoundNum() > 0;
                
                  numAlliedRobots = rc.senseRobotCount();
//                numAlliedEncampments = rc.senseEncampmentSquares(rc.getLocation(), 10000, rc.getTeam()).length;
 //               numAlliedSoldiers = numAlliedRobots - numAlliedEncampments - 1 - EncampmentJobSystem.numEncampmentsNeeded;
                
                  numNearbyAlliedRobots = rc.senseNearbyGameObjects(Robot.class, 14, rc.getTeam()).length;
//                numNearbyAlliedEncampments = rc.senseEncampmentSquares(rc.getLocation(), 14, rc.getTeam()).length;
//                numNearbyAlliedSoldiers = numNearbyAlliedRobots - numNearbyAlliedEncampments;
                
                  nearbyEnemyRobots = rc.senseNearbyGameObjects(Robot.class, 25, rc.getTeam().opponent());
                
                numNearbyEnemySoldiers = 0;
                for (int i = nearbyEnemyRobots.length; --i >= 0; ) {
                        RobotInfo robotInfo = rc.senseRobotInfo(nearbyEnemyRobots[i]);
                        if (robotInfo.type == RobotType.SOLDIER) {
                                numNearbyEnemySoldiers++;
                        }
                }
                
                numEnemyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam().opponent()).length;

                
                
                
        }
        
        public static boolean HQupdate() throws GameActionException {
        	numAlliedRobots = rc.senseRobotCount();
        	enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
            numEnemyPastures = enemyPastures.length;
            if (numEnemyPastures>0){
            	return true;
            }
            return false;
        }
        
        public static void broadcastPastrs(int channel){
        	BroadcastSystem.write(channel, pastrToInt(enemyPastures));
        }
        
        public static void receivePastrs(int channel){
        	enemyPastures = intToPastrs(BroadcastSystem.read(channel));
        	numEnemyPastures = enemyPastures.length;
        	closestPastr = findClosest(enemyPastures, rc.getLocation());
        }
        
        public static int pastrToInt(MapLocation[] pastrs){
        	int pastrInt = 0;
        	int multiplier = 1;
        	for (MapLocation pastr: pastrs){
        		pastrInt += locToInt(pastr)*multiplier;
        		multiplier*=10000;
        	}
			return pastrInt;
        	
        }
        
        public static MapLocation[] intToPastrs(int num){
        	MapLocation[] pastrs = new MapLocation[numEnemyPastures];
        	for (int i=0; i<numEnemyPastures; i++){
        		pastrs[i] = intToLoc(num%10000);
        		num/=10000;
        	}
        	return pastrs;
        }
        
    	public static int locToInt(MapLocation m){
    		return (m.x*100 + m.y);
    	}
    	
    	public static MapLocation intToLoc(int i){
    		return new MapLocation(i/100,i%100);
    	}
    	
    	public static MapLocation mladd(MapLocation m1, MapLocation m2){
    		return new MapLocation(m1.x+m2.x,m1.y+m2.y);
    	}
    	

    	public static MapLocation mldivide(MapLocation bigM, int divisor){
    		return new MapLocation(bigM.x/divisor, bigM.y/divisor);
    	}
    	
    	public static MapLocation mlmultiply(MapLocation bigM, int factor){
    		return new MapLocation(bigM.x*factor, bigM.y*factor);
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