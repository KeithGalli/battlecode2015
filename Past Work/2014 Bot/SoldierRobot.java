package team115;

import java.util.Dictionary;
import java.util.Hashtable;

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
	public int groupChannel;
	public int unseenEnemyLoc;
	public static double runAwayHitpoints = 50;
	public static boolean fleeing = false;
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};


	//SOLDIER data:
	static int pathCreatedRound = -1;
	public static int jobNumber;
	
	public static boolean sneak = false;

	public static boolean foundPastrSite = false;
	public static boolean dataDownloaded=false;
	public static boolean pastrsDownloaded=false;
	public static boolean timeToTower = false;
	public static boolean timeToPastr = false;

	public static MapLocation guarding;
	public static MapLocation rallyPoint;
	public static boolean offensive;
	public static boolean disrupting;

	public static boolean arrived;
	public static boolean inPosition;

	public static boolean shouldDownload=true;


	public static Direction persistentRandomDirection;

	public static MapLocation pastrSite;
	public static MapLocation secondSite;

	public static int pastrChannel;




	public SoldierRobot(RobotController rc) throws GameActionException {
		super(rc);
		Functions.init(rc);
		NavSystem.Soldierinit(rc);
		rallyPoint = Functions.mladd(Functions.mldivide(Functions.mlsubtract(rc.senseEnemyHQLocation(),rc.senseHQLocation()),3),rc.senseHQLocation());

		int myChannel = BroadcastSystem.read(1);

		jobNumber = BroadcastSystem.read(myChannel);
		
		int jobNumber2 = BroadcastSystem.read(200);

		if (jobNumber2 == 0){
			job = SoldierJob.DEFENDER;
		}
		if (jobNumber2 == 1){
			job = SoldierJob.TOWER;
			BroadcastSystem.write(200, 0);
		}
		if (jobNumber2 == 2){
			job = SoldierJob.PASTR;
			BroadcastSystem.write(200, 0);
		}
		if (jobNumber2 == 3){
			job = SoldierJob.HUNTER;
		}
		//		
		//		
		//		if (jobnum==0){
		//			job = SoldierJob.PASTR;
		//			BroadcastSystem.write(1, BroadcastSystem.read(1)+1);
		//			shouldDownload = false;
		//		}
		//		else if (jobnum==1){
		//			job = SoldierJob.TOWER;
		//			BroadcastSystem.write(1, BroadcastSystem.read(1)+1);
		//			shouldDownload = false;
		//		}
		//		else if (jobnum>1 && jobnum <5){
		//			job = SoldierJob.OFFENSE;
		//			BroadcastSystem.write(1, BroadcastSystem.read(1)+1);
		//			guarding = Functions.intToLoc(BroadcastSystem.read(10));
		//			shouldDownload = true;
		//			
		//		}
		//job = SoldierJob.DEFENDER;

		//		
		//		else if (jobnum ==5){
		//			job = SoldierJob.OFFENSIVE_TOWER;
		//			BroadcastSystem.write(1, BroadcastSystem.read(1)+1);
		//			
		//		}
		//		else if (jobnum >5){
		//			job = SoldierJob.DEFENDER;
		//			BroadcastSystem.write(1, BroadcastSystem.read(1)+1);
		//			guarding = Functions.intToLoc(BroadcastSystem.read(10));
		//			shouldDownload = false;
		//		}
		persistentRandomDirection = dirs[(int)(8*random())];
		rc.setIndicatorString(0, Integer.toString(jobNumber));


	}

	@Override
	public void run() {
		try {
			if(shouldDownload){
				if (!pastrsDownloaded){
					if(BroadcastSystem.read(5)==0){
						DataCache.pastrLocs[0] = Functions.intToLoc(BroadcastSystem.read(2));
						DataCache.pastrLocs[1] = Functions.intToLoc(BroadcastSystem.read(3));
						pastrSite=DataCache.pastrLocs[jobNumber];
						secondSite = pastrSite.add(pastrSite.directionTo(DataCache.enemyHQLocation).opposite());
						
						pastrsDownloaded = true;
					}
				}

				if(!dataDownloaded){
					if(BroadcastSystem.read(4)==0){
						int[][][] data = BroadcastSystem.download2MapArray(mapDataBand);
						NavSystem.mapData = data[1];
						NavSystem.voidID = data[0];
						NavSystem.waypointDict = BroadcastSystem.receiveMapDataDict(BroadcastSystem.read(waypointBand));
						//NavSystem.displayArray(NavSystem.mapData);
						//NavSystem.displayArray(NavSystem.voidID);

						dataDownloaded = true;

					}
				}
			}

//			if (!timeToTower){
//				if(BroadcastSystem.read(pastrChannel)==1){
//					if (DataCache.currentLocation.equals(pastrSite)){
//						job = SoldierJob.TOWER;
//						buildTower();
//					}
//					sneak = true;
//					timeToTower = true;
//				}
//			}
//			if (!timeToPastr){
//				if(BroadcastSystem.read(pastrChannel)==2){
//					if (DataCache.currentLocation.equals(secondSite)){
//						job = SoldierJob.PASTR;
//						buildPastr();
//					}
//					timeToPastr = true;
//					
//				}
//			}

			DataCache.updateRoundVariables();	

			if (DataCache.numEnemyRobots>0 && job!=SoldierJob.OFFENSE && job!=SoldierJob.OFFENSIVE_TOWER){
				job=SoldierJob.FIGHT;
			}

			if (job == SoldierJob.TOWER){
				towerCode();
			}

			else if (job == SoldierJob.DEFENDER){
				defenderCode();
			}

			else if (job == SoldierJob.OFFENSE){
				offenseCode();
			}

			else if (job == SoldierJob.PASTR_IDLE){
				pastrIdleCode();
			}

			else if (job == SoldierJob.OFFENSIVE_TOWER){
				offensiveTowerCode();
			}


			else if (job == SoldierJob.FIGHT){
				fightCode();
			}
			else if (job == SoldierJob.PASTR){
				specificPastrCode();
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

	public void pastrIdleCode() throws GameActionException{
		if(dataDownloaded){
			NavSystem.blindNav(pastrSite);
		}
		else{
			randomPathing();
		}
	}

	public void offenseCode() throws GameActionException{
		NavSystem.blindNav(DataCache.enemyHQLocation);
		//		if (DataCache.numEnemyRobots>0){
		//			simpleFight();
		//		}
		//		if(!offensive){
		//			rc.setIndicatorString(2, "not offensive");
		//			//NavSystem.blindNav(rallyPoint);
		//		}
		//		else{
		//			rc.setIndicatorString(2, "offensive");
		//			if(!disrupting){
		//				NavSystem.blindNav(DataCache.enemyHQLocation);
		//			}
		//			else{
		//				nextjob = SoldierJob.DEFENDER;
		//				guarding = Functions.intToLoc(BroadcastSystem.read(11));
		//				arrived = false;
		//				inPosition = false;
		//			}
		//		}
	}

	public void offensiveTowerCode() throws GameActionException{
		if (DataCache.numEnemyRobots>0){
			simpleFight();
		}
		if (DataCache.currentLocation.distanceSquaredTo(DataCache.enemyHQLocation)<320){
			disrupting = true;
			BroadcastSystem.write(11, Functions.locToInt(DataCache.currentLocation));
			buildTower();
		}
		else{
			NavSystem.blindNav(DataCache.enemyHQLocation);
		}

	}

	public void defenderCode() throws GameActionException{
		rc.setIndicatorString(1, Boolean.toString(arrived));

		if (DataCache.currentLocation.distanceSquaredTo(pastrSite)<=2){
			arrived = true;
		}

		if (DataCache.currentLocation.distanceSquaredTo(pastrSite)<=25){
			inPosition = true;
		}

		if (arrived){
			if (!inPosition){
				Direction dir = DataCache.currentLocation.directionTo(pastrSite);
				NavSystem.simpleNav(dir);
			}
			else{
				Direction dir = DataCache.currentLocation.directionTo(DataCache.enemyHQLocation);
				MapLocation trialLoc = DataCache.currentLocation.add(dir);
				if (trialLoc.distanceSquaredTo(pastrSite)<25){
					NavSystem.simpleNav(dir);
				}
				else{
					holdPosition();
				}
			}
		}
		else{
			if (dataDownloaded){
				NavSystem.blindNav(pastrSite);
			}	
			else{
				Direction dir = DataCache.currentLocation.directionTo(pastrSite);
				NavSystem.snailNav(dir);
			}


		}


	}

	private void holdPosition() throws GameActionException{
		Direction dir = DataCache.currentLocation.directionTo(DataCache.enemyHQLocation);
		rc.sneak(dir);
		inPosition=false;

	}

	public void farmerCode() throws GameActionException{
		if (DataCache.numEnemyRobots > 0) {
			nextjob = SoldierJob.FIGHT;
			// BroadcastSystem.write(groupChannel, 1);
			fightCode();
		}
		if(dataDownloaded){
			NavSystem.blindNav(DataCache.enemyHQLocation);
		}
		else{


			//rc.setIndicatorString(1, "not pathing");
			MapLocation pastrSite = null;
			if(DataCache.alliedPastrs.length<5){
				pastrSite = findPastrSite(foundPastrSite?1:4);
				foundPastrSite=(pastrSite!=null);
			}
			if (pastrSite!=null){
				pastrCode(pastrSite);
			}
			else{
				//rc.setIndicatorString(1, "pathing");
				randomPathing();
			}
		}
	}



	private void towerCode() throws GameActionException {
		if (DataCache.currentLocation.equals(pastrSite)){
			buildTower();
		}
		else if (dataDownloaded){
			NavSystem.blindNav(pastrSite);
		}
		else{
			NavSystem.snailNav(DataCache.currentLocation.directionTo(pastrSite));
		}


	}

	private void buildTower() throws GameActionException{
		if (rc.isActive()){
			rc.construct(RobotType.NOISETOWER);
		}
	}

	private void specificPastrCode() throws GameActionException {
		if (DataCache.currentLocation.equals(secondSite)){
			buildPastr();
		}
		else if (dataDownloaded){
			NavSystem.blindNav(secondSite);
		}
		else{
			NavSystem.snailNav(DataCache.currentLocation.directionTo(secondSite));
		}


	}
	private void pastrCode(MapLocation pastrSite) throws GameActionException {
		if(DataCache.currentLocation.distanceSquaredTo(pastrSite)>=GameConstants.PASTR_RANGE){
			NavSystem.snailNav(rc.getLocation().directionTo(pastrSite));
		}else{
			job = SoldierJob.PASTR;

			buildPastr();
		}
	}

	private void buildPastr() throws GameActionException{
		if (rc.isActive()){
			BroadcastSystem.write(10, Functions.locToInt(rc.getLocation()));
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
		else{
			randomPathing();
		}

	}

	private boolean tileOccupied(MapLocation t){
		return (rc.senseNearbyGameObjects(Robot.class,t,1,rc.getTeam()).length!=0);
	}



	public void fightCode() throws GameActionException {
		if (DataCache.numEnemyRobots == 0) {
			nextjob = SoldierJob.DEFENDER;

			inPosition = false;
			defenderCode();

		} else {
			if (rc.getHealth()>runAwayHitpoints){
				MapLocation[] enemyRobotLocations = Functions.robotsToLocations(DataCache.enemyRobots, rc, true);
				if(enemyRobotLocations.length==0){//only HQ is in view
					if (DataCache.numNearbyAlliedRobots>0){
						MapLocation[] alliedRobotLocations = Functions.robotsToLocations(DataCache.nearbyAlliedRobots, rc, false);
						MapLocation alliedRobotCenter = Functions.meanLocation(alliedRobotLocations);
						Direction towardAllies = rc.getLocation().directionTo(alliedRobotCenter);
						NavSystem.snailNav(towardAllies);
					}
					else{
						Direction dir = DataCache.currentLocation.directionTo(DataCache.ourHQLocation);
						NavSystem.snailNav(dir);
					}
				}else{//shootable robots are in view
					MapLocation closestEnemyLoc = Functions.findClosest(enemyRobotLocations, rc.getLocation());
					boolean closeEnoughToShoot = closestEnemyLoc.distanceSquaredTo(rc.getLocation())<=rc.getType().attackRadiusMaxSquared;
					if((DataCache.nearbyAlliedRobots.length+1)>=DataCache.enemyRobots.length){//attack when you have superior numbers
						attackClosest(closestEnemyLoc);
					}else{//otherwise regroup
						regroup(DataCache.enemyRobots,DataCache.nearbyAlliedRobots,closestEnemyLoc);
					}
				}
			}
			else{
				if (DataCache.numNearbyAlliedRobots>0){
					MapLocation[] alliedRobotLocations = Functions.robotsToLocations(DataCache.nearbyAlliedRobots, rc, false);
					MapLocation alliedRobotCenter = Functions.meanLocation(alliedRobotLocations);
					Direction towardAllies = rc.getLocation().directionTo(alliedRobotCenter);
					NavSystem.snailNav(towardAllies);
				}
				else{
					Direction dir = DataCache.currentLocation.directionTo(DataCache.ourHQLocation);
					NavSystem.snailNav(dir);
				}
			}
		}
	}

	public void simpleFight() throws GameActionException {
		MapLocation[] enemyRobotLocations = Functions.robotsToLocations(DataCache.enemyRobots, rc, true);
		MapLocation closestEnemyLoc = Functions.findClosest(enemyRobotLocations, rc.getLocation());
		boolean closeEnoughToShoot = closestEnemyLoc.distanceSquaredTo(rc.getLocation())<=rc.getType().attackRadiusMaxSquared;
		if (closeEnoughToShoot){
			rc.attackSquare(closestEnemyLoc);
		}
	}

	public void attackClosest(MapLocation closestEnemyLoc) throws GameActionException {
		if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){//close enough to shoot
			if(rc.isActive()){
				rc.attackSquare(closestEnemyLoc);
			}
		}else{//not close enough to shoot, so try to go shoot
			Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
			NavSystem.snailNav(towardClosest);
		}
	}

	private static void regroup(Robot[] enemyRobots, Robot[] alliedRobots,MapLocation closestEnemyLoc) throws GameActionException {
		int enemyAttackRangePlusBuffer = (int) Math.pow((Math.sqrt(rc.getType().attackRadiusMaxSquared)+1),2);
		if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<=enemyAttackRangePlusBuffer){//if within attack range, back up
			Direction awayFromEnemy = rc.getLocation().directionTo(closestEnemyLoc).opposite();
			NavSystem.snailNav(awayFromEnemy);
		}else{//if outside attack range, group up with allied robots
			MapLocation[] alliedRobotLocations = Functions.robotsToLocations(alliedRobots, rc, false);
			MapLocation alliedRobotCenter = Functions.meanLocation(alliedRobotLocations);
			Direction towardAllies = rc.getLocation().directionTo(alliedRobotCenter);
			NavSystem.snailNav(towardAllies);
		}
	}

	private MapLocation findPastrSite(int checkFraction) throws GameActionException{
		//int checkFraction = 4;//don't check all the sites every round. 

		double cowThreshold = 20.0/(1.0-GameConstants.NEUTRALS_TURN_DECAY);
		double mostCows = cowThreshold;
		double challengerCows = 0;
		MapLocation mostCowLoc = null;

		if(Clock.getBytecodeNum()<2000){
			MapLocation[] checkLocs = MapLocation.getAllMapLocationsWithinRadiusSq(DataCache.currentLocation, rc.getType().sensorRadiusSquared);
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
				Direction d = currentLoc.directionTo(DataCache.currentLocation);
				currentLoc = currentLoc.add(d);
				if(currentLoc.equals(DataCache.currentLocation)){
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
			TerrainTile ahead = rc.senseTerrainTile(DataCache.currentLocation.add(persistentRandomDirection));
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
