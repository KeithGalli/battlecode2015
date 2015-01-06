package dumbass;

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
import battlecode.common.TerrainTile;

public class SoldierRobot extends BaseRobot {

	public SoldierJob job;
	public SoldierJob nextjob;
	public static MapLocation myLoc;
	public int groupChannel;
	public int unseenEnemyLoc;
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};


	//SOLDIER data:
	static int pathCreatedRound = -1;
	
	public static boolean foundPastrSite = false;

	public static Direction persistentRandomDirection;


	public SoldierRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.Soldierinit(rc, bigBoxSize);
		int checkJob = rc.sensePastrLocations(rc.getTeam().opponent()).length;
		if (checkJob==0){
			job = SoldierJob.FARMER;
		}
		else{
			job = SoldierJob.HUNTER;
		}
		persistentRandomDirection = dirs[(int)(8*random())];

	}

	@Override
	public void run() {
		try {
			if (rc.getTeam().equals(RobotType.PASTR)){
				
			}
			myLoc = rc.getLocation();
			DataCache.updateRoundVariables();	
			
			if (DataCache.numEnemyRobots>0){
				job=SoldierJob.FIGHT;
			}
			if (job == SoldierJob.HUNTER){
				rc.setIndicatorString(0, Integer.toString(0));
				hunterCode();
			}
			
			else if (job == SoldierJob.FARMER){
				rc.setIndicatorString(0, Integer.toString(1));
				farmerCode();
			}
			else if (job == SoldierJob.FIGHT){
				rc.setIndicatorString(0, Integer.toString(3));
				fightCode();
			}
			else if (job == SoldierJob.PASTR){
				rc.setIndicatorString(0, Integer.toString(2));
				buildPastr();
			}
			

			if (nextjob != null) {
				job = nextjob;
				nextjob = null; // clear the state for the next call of run() to use
			}
		} catch (Exception e) {
//			if (rc.getType().equals(RobotType.PASTR)){
//				System.out.println("caught exception before it killed us:");
//				e.printStackTrace();
//			}
					//	                    System.out.println("caught exception before it killed us:");
			//			                    System.out.println(rc.getRobot().getID());
			//			                    e.printStackTrace();
		}
	}
	
	public void farmerCode() throws GameActionException{
		if (DataCache.numEnemyRobots > 0) {
			nextjob = SoldierJob.FIGHT;
			// BroadcastSystem.write(groupChannel, 1);
			fightCode();
		}
		else{
			rc.setIndicatorString(1, "not pathing");
			MapLocation pastrSite = null;
			if(DataCache.alliedPastrs.length<5){
				pastrSite = findPastrSite(foundPastrSite?1:4);
				foundPastrSite=(pastrSite!=null);
			}
			if (pastrSite!=null){
				pastrCode(pastrSite);
			}
			else{
				rc.setIndicatorString(1, "pathing");
				randomPathing();
			}
		}
		
	}

	private void pastrCode(MapLocation pastrSite) throws GameActionException {
		if(myLoc.distanceSquaredTo(pastrSite)>=GameConstants.PASTR_RANGE){
			NavSystem.snailNav(rc.getLocation().directionTo(pastrSite));
		}else{
			job = SoldierJob.PASTR;
			buildPastr();
		}
	}
	
	private void buildPastr() throws GameActionException{
		if (rc.isActive()){
			rc.construct(RobotType.PASTR);
		}
	}

	public void hunterCode() throws GameActionException {
		//rc.setIndicatorString(2, Integer.toString(DataCache.numEnemyRobots));
		if (DataCache.numEnemyRobots > 0) {
			
			nextjob = SoldierJob.FIGHT;
			// BroadcastSystem.write(groupChannel, 1);
			fightCode();
		}
		else{//NAVIGATION BY DOWNLOADED PATH
			//rc.setIndicatorString(0, "team "+myBand+", path length "+path.size());
			if(path.size()<=1){
				//rc.setIndicatorString(1, "downloading new path");
				//check if a new path is available
				int broadcastCreatedRound = rc.readBroadcast(myBand);
				if(pathCreatedRound<broadcastCreatedRound){
					rc.setIndicatorString(1, "downloading path");
					pathCreatedRound = broadcastCreatedRound;
					path = BroadcastSystem.downloadPath();
				}
			}
			if(path.size()>0){
				//follow breadthFirst path
				//rc.setIndicatorString(1, "following path");
				Direction bdir = NavSystem.getNextDirection(path, bigBoxSize);
				System.out.println(bdir);
				NavSystem.snailNav(bdir);
			}
		}

	}
	
	private boolean tileOccupied(MapLocation t){
		return (rc.senseNearbyGameObjects(Robot.class,t,1,rc.getTeam()).length!=0);
	}



	public void fightCode() throws GameActionException {
		if (DataCache.numEnemyRobots == 0) {
			if(DataCache.enemyPastrs.length>0){
				nextjob = SoldierJob.HUNTER;
				hunterCode();
			}
			else{
				nextjob = SoldierJob.FARMER;
				farmerCode();
			}
			
		} else {
			// Otherwise, just keep fighting
			aggressiveMicroCode();
		}
	}

	public void aggressiveMicroCode() throws GameActionException {
		MapLocation[] robotLocations = DataCache.robotsToLocations(DataCache.enemyRobots, rc);
		MapLocation closestEnemyLoc = DataCache.findClosest(robotLocations, rc.getLocation());
		if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){//close enough to shoot
			if(rc.isActive()){
				rc.attackSquare(closestEnemyLoc);
			}
		}else{//not close enough to shoot, so try to go shoot
			Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
			NavSystem.snailNav(towardClosest);
		}
	}
	
	private MapLocation findPastrSite(int checkFraction) throws GameActionException{
		//int checkFraction = 4;//don't check all the sites every round. 

		double cowThreshold = 20.0/(1.0-GameConstants.NEUTRALS_TURN_DECAY);
		double mostCows = cowThreshold;
		double challengerCows = 0;
		MapLocation mostCowLoc = null;
		
		if(Clock.getBytecodeNum()<2000){
			MapLocation[] checkLocs = MapLocation.getAllMapLocationsWithinRadiusSq(myLoc, rc.getType().sensorRadiusSquared);
			double phase =(double) (Clock.getRoundNum()%checkFraction)/(double) checkFraction;
			int start = (int)(phase*checkLocs.length);
			int end = (int)((phase+1.0/checkFraction)*checkLocs.length);
			for(int i=start;i<end;i++){
				MapLocation m = checkLocs[i];
				challengerCows=rc.senseCowsAtLocation(m);
				if(challengerCows>mostCows){
					mostCowLoc = m;
					mostCows=challengerCows;
				}
			}
		}

		if(mostCowLoc!=null){
			//check that there is no allied robot about to take that tile
			if(tileOccupied(mostCowLoc))
				return null;
			
			//check for existing pastrs that cover the area in question
			int outsidePastrRange = GameConstants.PASTR_RANGE+1;
			for(MapLocation m:rc.sensePastrLocations(rc.getTeam())){
				if(mostCowLoc.distanceSquaredTo(m)<outsidePastrRange){
					return null;
				}
			}
			
			//a cow location is only valid if it is accessible by a straight line.
			MapLocation currentLoc = mostCowLoc;
			while(!rc.senseTerrainTile(currentLoc).equals(TerrainTile.VOID)){//testing for open path toward the mostCowLoc
				Direction d = currentLoc.directionTo(myLoc);
				currentLoc = currentLoc.add(d);
				if(currentLoc.equals(myLoc)){
					//rc.setIndicatorString(2, "viable location found at "+mostCowLoc.x+","+mostCowLoc.y);
					return mostCowLoc;
				}
			}
		}
		
		return null;
	}
	
	private static void randomPathing() throws GameActionException{
		if(random()>0.9){
			if(random()>0.5){
				persistentRandomDirection = persistentRandomDirection.rotateLeft();
			}else{
				persistentRandomDirection = persistentRandomDirection.rotateRight();
			}
		}
		if(!NavSystem.canMove(persistentRandomDirection, true, rc)){
			TerrainTile ahead = rc.senseTerrainTile(myLoc.add(persistentRandomDirection));
			if(ahead.equals(TerrainTile.OFF_MAP)||ahead.equals(TerrainTile.VOID)){
				if(random()>0.5){
					persistentRandomDirection = persistentRandomDirection.rotateLeft();
				}else{
					persistentRandomDirection = persistentRandomDirection.rotateRight();
				}
			}
		}
		//rc.setIndicatorString(2, ""+persistentRandomDirection);
		NavSystem.snailNav(persistentRandomDirection);//run around at random
	}
	private static double random(){
		double d = (Math.random()*rc.getRobot().getID()*Clock.getRoundNum());
		return d-(int)d;
	}


}
