package final_strategy_nav;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class BEAVERRobot extends BaseRobot {


    public static Direction tarDir;

    public static MapLocation tile;
    public static MapLocation[] visibleTiles;


	public static MapLocation goal;

	public static Random random = new Random();

	public static int downloadReady;
	public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public static int indx = 0;
	public static boolean hqFound = false;
	public static boolean ourTowersFound = false;
	public static boolean enemyTowersFound = false;

	public static int minx;
	public static int maxx;
	public static int miny;
	public static int maxy;

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
	    NavSystem.UNITinit(rc);
	    MapEngine.UNITinit(rc);
	    BroadcastSystem.write(BroadcastSystem.myInstrChannel, BEAVER_CONS);
	    BroadcastSystem.setNotCollecting();
	}

	@Override
	public void run() {
		try {
		    boolean building = false;
			if(rc.isCoreReady()){
				DataCache.updateRoundVariables();
				downloadReady = BroadcastSystem.read(BroadcastSystem.myInstrChannel);


				double ore = rc.getTeamOre();
//				int minerFactories = rc.readBroadcast(MINER_FACT_PREVIOUS_CHAN);
				int minerFactoriesBuilt = rc.readBroadcast(40);
				int barracksBuilt = rc.readBroadcast(41);
				int tankFactoriesBuilt = rc.readBroadcast(42);
				int helipadsBuilt = rc.readBroadcast(43);
				int supplyDepotsBuilt = rc.readBroadcast(44);
				int aerospaceLabsBuilt = rc.readBroadcast(45);
				RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.BEAVER);
				

			    if (enemyRobots.length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(enemyRobots);
	                }
			    } else if (rc.hasBuildRequirements(RobotType.MINERFACTORY) && minerFactoriesBuilt <2) {
			        //System.out.println(building);
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.MINERFACTORY);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.MINERFACTORY);
                        rc.broadcast(40, minerFactoriesBuilt+1);
                        building=true;
                    }			   
                    //System.out.println(building);
			    } else if (rc.hasBuildRequirements(RobotType.HELIPAD) && helipadsBuilt < 1) {
			        //System.out.println(building);
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.HELIPAD);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.HELIPAD);
                        rc.broadcast(43, helipadsBuilt+1);
                        building=true;
                    }
                    //System.out.println(building);
			    } else if (rc.hasBuildRequirements(RobotType.SUPPLYDEPOT) && supplyDepotsBuilt < 2) {
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.SUPPLYDEPOT);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.SUPPLYDEPOT);
                        rc.broadcast(44, supplyDepotsBuilt+1);
                        building=true;
                    }     			        
			    } else if (rc.hasBuildRequirements(RobotType.BARRACKS) && barracksBuilt < 1) {
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.BARRACKS);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.BARRACKS);
                        rc.broadcast(41, barracksBuilt+1);
                        building=true;
                    }       
			    } else if (rc.hasBuildRequirements(RobotType.TANKFACTORY) && tankFactoriesBuilt < 4) {
			        Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.TANKFACTORY);
			        if (buildDirection != null) {
			            rc.build(buildDirection, RobotType.TANKFACTORY);
			            rc.broadcast(42, tankFactoriesBuilt+1);
			            building=true;
			        }
			    } 
			    if (!building) {
			        if (downloadReady>=25000){
						//System.out.println("BEAVER TEST");
						//rc.setIndicatorString(1, "messaging");
						BroadcastSystem.prepareandsendLocsDataList(MapEngine.senseQueue, downloadReady);
						//System.out.println(MapEngine.senseQueue);
						MapEngine.resetSenseQueue();
						BroadcastSystem.write(BroadcastSystem.myInstrChannel,0);
						//System.out.println("BEAVER TEST END");
					//	System.out.println("BEAVER TESTCHANNEL");
					}
					if (downloadReady==2){
						//System.out.println("BEAVER TEST 2");

						//rc.setIndicatorString(1, "downloading");
						BroadcastSystem.receiveMapDataDict(BroadcastSystem.dataBand);
						// System.out.println("/////////////////////////");
		    // // //     		Functions.displayOREArray(MapEngine.map);
			   //    		System.out.println("/////////////////////////");
			   //    		Functions.displayWallArray(MapEngine.map);
			   // //    		// //System.out.println(MapEngine.waypointDict);
			   //    		System.out.println("/////////////////////////");
						MapEngine.waypointDict = BroadcastSystem.receiveWaypointDict();
						//System.out.println("BEAVER TEST 2 END");
						//System.out.println(MapEngine.waypointDict);
						//System.out.println("Test2");
						//rc.setIndicatorString(1, "not downloading");
						BroadcastSystem.write(BroadcastSystem.myInstrChannel, 0);
					}
			        //System.out.println("I'm here!!!");
	                if(rc.senseOre(rc.getLocation())>5){
	                    rc.mine();
	                } else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation())> 22){
	                    RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
	                } else{
	                   // System.out.println("inside move randomly");
	                    moveRandomly();
	                }			        
			    }


			}
			//transferSupplies(rc);
			rc.broadcast(BEAVER_CURRENT_CHAN, rc.readBroadcast(BEAVER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
	
	private void constructBuilding(RobotType type) throws GameActionException {
	    Direction buildDirection = getBuildDirection(type);
	    if (buildDirection != null) {
	        rc.build(buildDirection, type);
	    }
	}
}
