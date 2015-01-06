package dumbass;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class NavSystem {
	static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
	static Direction allDirections[] = Direction.values();
		
	public static RobotController rc;
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int bigBoxSize;
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;
	public static int[][] coarseMap;
	public static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	//public static Direction[] dirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static boolean shortestPathLocated;
	public static RobotController rci;
	
	//pathTo(myLoc,enemy,1000000);
	
	public static void Soldierinit(RobotController rci, int bigBoxSizeIn){
		rc = rci;
		bigBoxSize = bigBoxSizeIn;
		width = rc.getMapWidth()/bigBoxSize;
		height = rc.getMapHeight()/bigBoxSize;
	}
	
	public static void HQinit(RobotController rci,int bigBoxSizeIn){
		rc = rci;
		bigBoxSize = bigBoxSizeIn;
		width = rc.getMapWidth()/bigBoxSize;
		height = rc.getMapHeight()/bigBoxSize;
		
		assessMap(rci);
		updateInternalMap(rc);
		//MapAssessment.printCoarseMap();
		//MapAssessment.printBigCoarseMap(rci);
		
		
	}
	
	private static int getMapData(int x, int y){
		return mapData[x+1][y+1];
	}
	
	public static int getMapData(MapLocation m){
		return getMapData(m.x,m.y);
	}
	
	private static void setMapData(int x, int y, int val){
		mapData[x+1][y+1] = val;
	}
	
	private static void updateInternalMap(RobotController rc){//can take several rounds, but ultimately saves time
		mapData = new int[width+2][height+2];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int val = coarseMap[x][y];
				if(val==bigBoxSize*bigBoxSize){//completely filled with voids
					val=0;//if it's zero, consider it non-traversible
				}else{
					val+=10000;//if it's >= 10000, consider it on-map
				}
				setMapData(x,y,val);//0 off map, >= 10000 on-map, with val-10000 obstacles in the box
			}
		}
	}
	
	@SuppressWarnings("unused")
	public static ArrayList<MapLocation> pathTo(MapLocation start,MapLocation goal, int maxSearchDist) {
		//clear path info for next computation
		shortestPathLocated = false;
		path = new ArrayList<MapLocation>();
		pathingData = new Direction[width][height];//direction to arrive at this tile fastest
		distanceData = new int[width][height];//closest distance to this tile
		ArrayList<MapLocation> outermost = new ArrayList<MapLocation>();
		outermost.add(start);
		distanceData[start.x][start.y] = -maxSearchDist*10;//the 10 allows a multiple of 14 for diagonals
		while(!shortestPathLocated&&outermost.size()>0){
			//System.out.println("outermost Length is "+outermost.size());
			outermost = getNewOutermost(outermost,start,goal);
		}
		listDirections(start,goal);//write maplocations to "path"
		return path;
	}
	
	private static ArrayList<MapLocation> getNewOutermost(ArrayList<MapLocation> outermost,MapLocation start,MapLocation goal){
		//this function locates new outermost tiles to examine (like flood fill)
		ArrayList<MapLocation> newOutermost = new ArrayList<MapLocation>();
		ArrayList<Proposal> props = new ArrayList<Proposal>();//new proposed outermost tiles
		//propose tiles adjacent to the outermost ones
		for(MapLocation m:outermost){
			Proposal.generateProposals(m, distanceData[m.x][m.y],1, props, dirs);//all proposals are traversible
		}
		//evaluate those proposed tiles
		for(Proposal p:props){
				if(p.dist<distanceData[p.loc.x][p.loc.y]){//if the proposal is good,
					if(distanceData[p.loc.x][p.loc.y]!=0){//overwrite a previous proposal
						newOutermost.remove(p.loc);
					}
					distanceData[p.loc.x][p.loc.y]=p.dist;//update closest distance infomap
					pathingData[p.loc.x][p.loc.y]=p.dir;//update direction infomap
					newOutermost.add(p.loc);//add this to the list of outermost to consider
				}
				//the following is commented: keeps searching even when goal has been reached once
//			if(p.loc.equals(goal)){
//				shortestPathLocated = true;
//				break;
//			}
		}
		return newOutermost;
		//if there were no new outermost, and the goal was not reached, should look for closest accessible tile?
		//end condition: record closest dist to goal. when all other active "threads" exceed this, end.
		//simple end condition: end when the goal is reached by any thread.
		
	}
	
	private static void listDirections(MapLocation start,MapLocation end){
		//a badly named function. It compiles a list of maplocations now.
		//(path used to be an ArrayList of Directions)
		MapLocation currentLoc = end;
		while(!currentLoc.equals(start)){
			Direction d = pathingData[currentLoc.x][currentLoc.y];
			path.add(0,currentLoc);
			currentLoc = currentLoc.add(d.opposite());//current location moves backwards to start
		}
		//System.out.println("located goal in "+path.size()+" dir steps!");
	}

	//once you have a path, you want to get the next direction you need to go in.
	//this function should truncate the path as you move along it, and also give the next direction.
	public static Direction getNextDirection(ArrayList<MapLocation> path, int bigBoxSize){
		//just check the bottom member of the path, to see if it needs truncating
		if(DataCache.mldivide(rc.getLocation(),bigBoxSize).equals(path.get(0))
				&&path.size()>1){//will not delete the path entirely
			path.remove(0);
		}
		return rc.getLocation().directionTo(DataCache.bigBoxCenter(path.get(0),bigBoxSize));
	}
	
	//mapData[0][0], array query apparently costs 6 bytecodes
	//using getMapData, which adds an offset, costs 14 bytecodes (8 extra, confirmed two ways)
	//checking whether x and y are in bounds, four comparisons, costs a lot, like 10. (2x4 comparisons, 2 accesses ?)
	//one logical comparison costs 3. Maybe that's 2 for the comparison and 1 for the access. nope!
	//even with more accesses, it still costs 9 for three if statements
	//addition costs 2 as well. . .
	//assignment apparently costs 2 also
	//rotateright costs 2. 
	//1d array access costs 2.
	//for(int i=5;i>0;i--) is cheaper than 
	//for(int i=0;i<5;i++)
	//because comparison with zero is cheaper by 1 bc. But only if the zero appears second!
	
	public static void assessMap(RobotController rc){

		int coarseWidth = rc.getMapWidth()/bigBoxSize;
		int coarseHeight = rc.getMapHeight()/bigBoxSize;
		coarseMap = new int[coarseWidth][coarseHeight];
		for(int x=0;x<coarseWidth*bigBoxSize;x++){
			for(int y=0;y<coarseHeight*bigBoxSize;y++){
				coarseMap[x/bigBoxSize][y/bigBoxSize]+=countObstacles(x,y,rc);
			}
		}
	}

	public static int countObstacles(int x, int y,RobotController rc){//returns a 0 or a 1
		int terrainOrdinal = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
		return (terrainOrdinal<2?0:1);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//Snail Navigation
	///////////////////////////////////////////////////////////////////////////
	public static void snailNav(Direction chosenDirection) throws GameActionException{
		tryToMove(chosenDirection, true, rc, directionalLooks, allDirections);
	}
	
	static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();
	
	static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
		//include both rc.canMove and the snail Trail requirements
		if(selfAvoiding){
			MapLocation resultingLocation = rc.getLocation().add(dir);
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)||resultingLocation.distanceSquaredTo(DataCache.enemyHQLocation)<=16){
						rc.setIndicatorString(2, "adjacentto");
						return false;
					}
				}
			}
		}
		//if you get through the loop, then dir is not adjacent to the icky snail trail
		//rc.setIndicatorString(2, "canmove in " + dir);
		return rc.canMove(dir);
	}
	
	private static void tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(rc.isActive()){
			snailTrail.remove(0);
			snailTrail.add(rc.getLocation());
			for(int directionalOffset:directionalLooks){
//				rc.setIndicatorString(0, "notmoving");
//				rc.setIndicatorString(2, "notmoving in a direction");
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding,rc)){
//					rc.setIndicatorString(0, "moving in" + trialDir);
//					rc.setIndicatorString(2, String.valueOf(rc.canMove(trialDir)));
//					
					rc.move(trialDir);
					//snailTrail.remove(0);
					//snailTrail.add(rc.getLocation());
					break;
				}
				
				//rc.setIndicatorString(2, String.valueOf(rc.canMove(trialDir)));
			}
			//System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	//Simple Navigation
	///////////////////////////////////////////////////////////////////////////
	public static void simpleNav(Direction chosenDirection) throws GameActionException{
		if(rc.isActive()){
			for(int directionalOffset:directionalLooks){
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
				if(rc.canMove(trialDir)){
					rc.move(trialDir);
					break;
				}
			}
		}
	}
}
