package final_strategy_nav;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import battlecode.common.*;

public class MINERRobot extends BaseRobot {

    public static Random random = new Random();

    public static int downloadReady;

	static Random rand = new Random();
	public final static int MINER_COST = 60;
	
    private final static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	private boolean supplied;
	
	public MINERRobot(RobotController rc) throws GameActionException {
		super(rc);
	    NavSystem.UNITinit(rc);
	    MapEngine.UNITinit(rc);
		supplied = false;
        BroadcastSystem.write(BroadcastSystem.myInstrChannel, MINER_CONS);
        BroadcastSystem.setNotCollecting();
	}

	@Override
	public void run() {

		
		try {
		    DataCache.updateRoundVariables();
            //TELL THE HQ WHERE WE ARE
            if (DataCache.hasMoved()){
                MapEngine.senseQueue.add(DataCache.currentLoc);
            }

            downloadReady = BroadcastSystem.read(BroadcastSystem.myInstrChannel);

			RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.MINER);
	        MapLocation currentLocation = rc.getLocation();
	        double oreCurrentLocation = rc.senseOre(currentLocation);
	        double supplyLevel = rc.getSupplyLevel();
            double oreDensity = getOreDensity(currentLocation);
            if (oreDensity > rc.readBroadcast(200) && oreDensity>150) {
                rc.broadcast(200, (int)oreDensity);
                rc.broadcast(201, currentLocation.x);
                rc.broadcast(202, currentLocation.y);
            }
			if(rc.isCoreReady()) {
			    if (enemyRobots.length>0 && rc.isWeaponReady()) {
			        attackLeastHealthEnemy(enemyRobots);
			    }
			    else if (!supplied) {
			        NavSystem.smartNav(myHQ, false);
			        if (supplyLevel>100 || Clock.getRoundNum()>1000) {
			            supplied = true;
			        }
			    } 
			    else if (rc.senseOre(rc.getLocation())>4 && rc.canMine()) {
                    rc.mine();
                } else if (downloadReady>=25000){
                    //System.out.println("BEAVER TEST");
                    //rc.setIndicatorString(1, "messaging");
                    BroadcastSystem.prepareandsendLocsDataList(MapEngine.senseQueue, downloadReady);
                    //System.out.println(MapEngine.senseQueue);
                    MapEngine.resetSenseQueue();
                    BroadcastSystem.write(BroadcastSystem.myInstrChannel,0);
                    //System.out.println("BEAVER TEST END");
                //  System.out.println("BEAVER TESTCHANNEL");
                } else if (downloadReady==2){
                    //System.out.println("BEAVER TEST 2");

                    //rc.setIndicatorString(1, "downloading");
                    BroadcastSystem.receiveMapDataDict(BroadcastSystem.dataBand);
                    // System.out.println("/////////////////////////");
        // // //            Functions.displayOREArray(MapEngine.map);
           //           System.out.println("/////////////////////////");
           //           Functions.displayWallArray(MapEngine.map);
           // //            // //System.out.println(MapEngine.waypointDict);
           //           System.out.println("/////////////////////////");
                    MapEngine.waypointDict = BroadcastSystem.receiveWaypointDict();
                    //System.out.println("BEAVER TEST 2 END");
                    //System.out.println(MapEngine.waypointDict);
                    //System.out.println("Test2");
                    //rc.setIndicatorString(1, "not downloading");
                    BroadcastSystem.write(BroadcastSystem.myInstrChannel, 0);
                } else {
                    Direction[] directions = getDirectionsAway(this.myHQ);
                    List<Integer> directionList = moveToMaxOrRandomList(directions);
                    int ore = directionList.get(1);
                    Direction direction = RobotPlayer.directions[directionList.get(0)];
                    if(rc.canMove(direction) && senseNearbyTowers(currentLocation, direction)==0 && ore > 4) {
                        rc.move(direction);
                    } else {
                        if (oreDensity <100 ) {
                            int xCoordinate = rc.readBroadcast(201);
                            int yCoordinate = rc.readBroadcast(202);
                            MapLocation oreLocation = new MapLocation(xCoordinate, yCoordinate); 
                                if (rc.readBroadcast(200) !=0) {
                                    NavSystem.smartNav(oreLocation, false);
                                } else {
                                    int fate = RobotPlayer.rand.nextInt(100);
                                    if (fate<50) {
                                        int rv = RobotPlayer.rand.nextInt(8);
                                        Direction randomDirection = RobotPlayer.directions[rv];
                                        if (senseNearbyTowers(currentLocation, randomDirection)==0) {
                                            if (rc.canMove(randomDirection)) {
                                                rc.move(randomDirection);
                                            }
                                        }
                                    } else {
                                        int rv = RobotPlayer.rand.nextInt(5);
                                        Direction randomDirection = getDirectionsAway(this.myHQ)[rv];
                                        if (senseNearbyTowers(currentLocation, randomDirection)==0) {
                                            if (rc.canMove(randomDirection)) {
                                                rc.move(randomDirection);
                                            }
                                        }
                                    }

                                }
                        } else {
                            int rv = RobotPlayer.rand.nextInt(8);
                            Direction randomDirection = RobotPlayer.directions[rv];
                            if (senseNearbyTowers(currentLocation, randomDirection)==0) {
                                if (rc.canMove(randomDirection)) {
                                    rc.move(randomDirection);
                                }
                            }
                        }

                    }
                }			    
			}
			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
			//transferSpecificSupplies(RobotType.MINER, rc, nearbyAllies);
			rc.broadcast(MINER_CURRENT_CHAN, rc.readBroadcast(MINER_CURRENT_CHAN)+1);
			
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
    
    private List<Integer> moveToMaxOrRandomList(Direction[] dirs) throws GameActionException {
        MapLocation myLocation = rc.getLocation();      
        int fate = RobotPlayer.rand.nextInt(5);
        Direction dir = dirs[fate];
        Collections.shuffle(Arrays.asList(directions));
        
        for (Direction direction : directions) {
            if (!rc.canMove(dir) || (rc.canMove(direction)&& rc.senseOre(myLocation.add(direction))> rc.senseOre(myLocation.add(dir)))) {
                dir = direction;
                
            }
        }
        int dirInt = RobotPlayer.directionToInt(dir);
        int oreAmount = (int) Math.floor(rc.senseOre(myLocation.add(dir)));
        List<Integer> returnList = Arrays.asList(dirInt, oreAmount);
        return returnList;
    }

	private Direction getDirectionAwayFromHQ() {
		return (rc.getLocation().directionTo(rc.senseHQLocation()).opposite());
	}
	  
    static int directionToInt(Direction d) {
        switch(d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }
}
