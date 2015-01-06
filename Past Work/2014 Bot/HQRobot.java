package team115;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class HQRobot extends BaseRobot {

	public static double[][] cowDataRough;
	public static int bigBoxSize = 5;
	public static int[] dirOffsets = new int[]{4, -3, 3, -2, 2, -1, 1, 0};
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	
	public static int robotCount;





	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);
		spawnSoldier();
		robotCount = 1;
		BroadcastSystem.write(4, -1);
		BroadcastSystem.write(5, -1);
		BroadcastSystem.write(1, 100);
		BroadcastSystem.write(100, 0);
		BroadcastSystem.write(200, 0);
		setCowMapData();
		//System.out.println(cowDataRough[0].length);
		//

		//printCoarseMap();
		DataCache.pastrLocs[0] = getBestPastrLoc();
		DataCache.pastrLocs[1] = getSecondBestPastrLocs(DataCache.pastrLocs[0]);
		System.out.println(DataCache.pastrLocs[0]);
		System.out.println(DataCache.pastrLocs[1]);
		BroadcastSystem.write(2, Functions.locToInt(DataCache.pastrLocs[0]));
		BroadcastSystem.write(3, Functions.locToInt(DataCache.pastrLocs[1]));
		BroadcastSystem.write(5, 0);

		

		NavSystem.HQinit(rc);
		//System.out.println("printing data");
		//NavSystem.displayArray(NavSystem.mapData);
		//NavSystem.displayArray(NavSystem.voidID);
		//System.out.println(NavSystem.waypointDictHQ);
		//System.out.println("preparing data");
		//int mapdataint = BroadcastSystem.prepareMapDataArray(NavSystem.mapData);
		//System.out.println(mapdataint);

		BroadcastSystem.broadcast2MapArrays(mapDataBand, NavSystem.voidID, NavSystem.mapData);
		BroadcastSystem.prepareandsendMapDataDict(NavSystem.waypointDictHQ);
		//System.out.println("broadcasting data");
		BroadcastSystem.write(4, 0);

	}

	@Override
	public void run() {
		try {
			if (robotCount==1){
				BroadcastSystem.write(200, 1);
			}
			else if (robotCount==2){
				BroadcastSystem.write(200, 2);
			}
			else if (robotCount==3){
				BroadcastSystem.write(200, 1);
			}
			else if (robotCount==4){
				BroadcastSystem.write(200, 2);
			}
			else if (robotCount>20){
				BroadcastSystem.write(200, 3);
			}
			if(robotCount==3 || robotCount ==4 || robotCount>14){
				BroadcastSystem.write(100, 1);
			}
			else{
				BroadcastSystem.write(100, 0);
			}

			DataCache.updateRoundVariables();

			rc.setIndicatorString(0, Integer.toString(DataCache.numEnemyPastrs));

			if (DataCache.numEnemyRobots>0){
				int[] closestEnemyInfo = Functions.getClosestEnemy(DataCache.enemyRobots);
				MapLocation closestEnemyLocation = new MapLocation(closestEnemyInfo[1], closestEnemyInfo[2]);
				if(closestEnemyLocation.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
					rc.setIndicatorString(1, "trying to shoot");
					if(rc.isActive()){
						rc.setIndicatorString(2, "rcactive");
						rc.attackSquare(closestEnemyLocation);
					}
				}
			}
			if (rc.isActive() ) {
				spawnSoldier();
				
			}

		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}

	public static void spawnAnySoldier() throws GameActionException {
		if (rc.senseRobotCount()<3){
			spawnConstructionSoldier();
		}
		else{
			spawnSoldier();
		}
	}

	public static void spawnConstructionSoldier() throws GameActionException {    	
		Direction desiredDir = rc.getLocation().directionTo(DataCache.enemyHQLocation).opposite();
		Direction dir = getSpawnDirection(desiredDir);
		if (dir != null) {
			rc.spawn(dir);
			
		}
	}

	public static void spawnSoldier() throws GameActionException {    	
		Direction desiredDir = rc.getLocation().directionTo(DataCache.enemyHQLocation);
		Direction dir = getSpawnDirection(desiredDir);
		if (dir != null) {
			//int robotChannel = BroadcastSystem.read(1);
		//	int job = getJob();
			//BroadcastSystem.write(robotChannel, message);
			rc.spawn(dir);
			robotCount++;
		}
	}
	
//	public static int getJob(){
//		
//	}

	/**
	 * helper fcn to see what direction to actually go given a desired direction
	 * @param rc
	 * @param dir
	 * @return
	 */
	private static Direction getSpawnDirection(Direction dir) {
		Direction canMoveDirection = null;
		int desiredDirOffset = dir.ordinal();
		for (int i = dirOffsets.length; --i >= 0; ) {
			int dirOffset = dirOffsets[i];
			Direction currentDirection = DataCache.directionArray[(desiredDirOffset + dirOffset + 8) % 8];
			if (rc.canMove(currentDirection)) {
				if (canMoveDirection == null) {
					canMoveDirection = currentDirection;
				}
			}                        
		}
		return canMoveDirection;
	}


	private static void setCowMapData(){
		DataCache.cowData = rc.senseCowGrowth();
		int coarseWidth = DataCache.mapWidth/bigBoxSize;
		int coarseHeight = DataCache.mapHeight/bigBoxSize;
		cowDataRough = new double[coarseWidth][coarseHeight];
		for(int x=0;x<coarseWidth*bigBoxSize;x++){
			for(int y=0;y<coarseHeight*bigBoxSize;y++){
				cowDataRough[x/bigBoxSize][y/bigBoxSize]+=DataCache.cowData[x][y];
			}
		}
	}

	public static MapLocation getBestPastrLoc(){
		double growthRate = 0;
		MapLocation pastrLoc = DataCache.enemyHQLocation;
		for(int x=0;x<cowDataRough.length;x++){
			for(int y=0;y<cowDataRough[0].length;y++){
				if (cowDataRough[x][y]>=growthRate){
					MapLocation trial = Functions.bigBoxCenter(new MapLocation(x, y), bigBoxSize);
					if (DataCache.ourHQLocation.distanceSquaredTo(trial)<=(DataCache.enemyHQLocation.distanceSquaredTo(trial))){
//						System.out.println("testing");
//						System.out.println(trial);
						growthRate = cowDataRough[x][y];
						pastrLoc = trial;
					}
				}
			}
		}
//		if (NavSystem.mapData[pastrLoc.x][pastrLoc.y]==0){
//			for (int i = dirs.length; --i >= 0; ) {
//				Direction dir = dirs[i];
//				MapLocation trial2 = pastrLoc.add(dir);
//				if (NavSystem.mapData[trial2.x][trial2.y]!=0){
//					return trial2;
//				}           
//			}
//		}
		return pastrLoc;
	}
	
	public static MapLocation getSecondBestPastrLocs(MapLocation bestPastrLoc){
		System.out.println("bestPastr="+bestPastrLoc);
		double growthRate = 0;
		MapLocation pastrLoc = DataCache.enemyHQLocation;
		for(int x=0;x<cowDataRough.length;x++){
			for(int y=0;y<cowDataRough[0].length;y++){
				if (cowDataRough[x][y]>=growthRate){
					MapLocation trial = Functions.bigBoxCenter(new MapLocation(x, y), bigBoxSize);
					System.out.println("trial="+trial);
					if ((DataCache.ourHQLocation.distanceSquaredTo(trial)<=(DataCache.enemyHQLocation.distanceSquaredTo(trial)+5))
							&& (!trial.equals(bestPastrLoc))
							&& (bestPastrLoc.distanceSquaredTo(trial)>=200)){
						System.out.println("trial2="+trial);
						//System.out.println("testing");
						//System.out.println(trial);
						growthRate = cowDataRough[x][y];
						pastrLoc = trial;
					}
				}
			}
		}
//		if (NavSystem.mapData[pastrLoc.x][pastrLoc.y]==0){
//			for (int i = dirs.length; --i >= 0; ) {
//				Direction dir = dirs[i];
//				MapLocation trial2 = pastrLoc.add(dir);
//				if (NavSystem.mapData[trial2.x][trial2.y]!=0){
//					return trial2;
//				}           
//			}
//		}
		return pastrLoc;
	}

	public static void printCoarseMap(){
		System.out.println("Coarse map:");
		for(int x=0;x<cowDataRough.length;x++){
			for(int y=0;y<cowDataRough[0].length;y++){
				double cowGrowth = cowDataRough[x][y];
				System.out.print(cowGrowth+" ");
			}
			System.out.println();
		}
	}

}
