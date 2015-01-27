package launchers;


import java.util.ArrayList;
import java.util.List;

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

    public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
	      NavSystem.UNITinit(rc);
	      MapEngine.UNITinit(rc);
	}

	@Override
	public void run() {
		try {
			if(rc.isCoreReady()){
				double ore = rc.getTeamOre();
//				int minerFactories = rc.readBroadcast(MINER_FACT_PREVIOUS_CHAN);
				int minerFactoriesBuilt = rc.readBroadcast(40);
				int barracksBuilt = rc.readBroadcast(41);
				int tankFactoriesBuilt = rc.readBroadcast(42);
				int helipadsBuilt = rc.readBroadcast(43);
				int supplyDepotsBuilt = rc.readBroadcast(44);
				int aerospaceLabsBuilt = rc.readBroadcast(45);
				int techInstsBuilt = rc.readBroadcast(46);
				int trainFieldBuilt = rc.readBroadcast(47);

				RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.BEAVER);
				

			    if (enemyRobots.length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(enemyRobots);
	                }
			    } else if (rc.hasBuildRequirements(RobotType.MINERFACTORY) && minerFactoriesBuilt <2) {
                    Direction buildDirection = getBuildDirection(RobotType.MINERFACTORY);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.MINERFACTORY);
                        rc.broadcast(40, minerFactoriesBuilt+1);
                    }			        
			    } else if (rc.hasBuildRequirements(RobotType.HELIPAD) && helipadsBuilt < 1) {
                    Direction buildDirection = getBuildDirection(RobotType.HELIPAD);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.HELIPAD);
                        rc.broadcast(43, helipadsBuilt+1);
                    }    
			    } else if(rc.hasBuildRequirements(RobotType.AEROSPACELAB) && aerospaceLabsBuilt < 3){
			    	Direction buildDirection = getBuildDirection(RobotType.AEROSPACELAB);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.AEROSPACELAB);
                        rc.broadcast(45, aerospaceLabsBuilt+1);
                    } 
			    } else if (rc.hasBuildRequirements(RobotType.SUPPLYDEPOT) && supplyDepotsBuilt < 2 && minerFactoriesBuilt >0) {
                    Direction buildDirection = getBuildDirection(RobotType.SUPPLYDEPOT);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.SUPPLYDEPOT);
                        rc.broadcast(44, supplyDepotsBuilt+1);

                    }
//			    } else if(rc.hasBuildRequirements(RobotType.TECHNOLOGYINSTITUTE) && minerFactoriesBuilt > 0 && techInstsBuilt < 1){
//			    	Direction buildDirection = getBuildDirection(RobotType.TECHNOLOGYINSTITUTE);
//                    if (buildDirection!=null) {
//                        rc.build(buildDirection, RobotType.TECHNOLOGYINSTITUTE);
//                        rc.broadcast(46, techInstsBuilt+1);
//                    }
//			    } else if(rc.hasBuildRequirements(RobotType.TRAININGFIELD) && minerFactoriesBuilt > 0 && trainFieldBuilt < 1){
//			    	Direction buildDirection = getBuildDirection(RobotType.TRAININGFIELD);
//                    if (buildDirection!=null) {
//                        rc.build(buildDirection, RobotType.TRAININGFIELD);
//                        rc.broadcast(47, techInstsBuilt+1);
//                    }
			    } else if (rc.hasBuildRequirements(RobotType.BARRACKS) && barracksBuilt < 1 && minerFactoriesBuilt> 0) {
                    Direction buildDirection = getBuildDirection(RobotType.BARRACKS);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.BARRACKS);
                        rc.broadcast(41, barracksBuilt+1);
                    }       
//			    } else if (rc.hasBuildRequirements(RobotType.TANKFACTORY) && tankFactoriesBuilt < 4 && minerFactoriesBuilt > 0) {
//			        Direction buildDirection = getBuildDirection(RobotType.TANKFACTORY);
//			        if (buildDirection != null) {
//			            rc.build(buildDirection, RobotType.TANKFACTORY);
//			            rc.broadcast(42, tankFactoriesBuilt+1);
//			        }
			    } else if(rc.senseOre(rc.getLocation())>2){
				    rc.mine();
				} else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation())> 22){
			        RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
				} else{
					RobotPlayer.tryMove(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)]);

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
