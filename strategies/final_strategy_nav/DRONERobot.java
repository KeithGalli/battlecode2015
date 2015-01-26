package final_strategy_nav;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class DRONERobot extends BaseRobot {

	public static Random random = new Random();

	public static int downloadReady;

	//static int supplierID = 0;

	public DRONERobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		//System.out.println("THIS IS BEAVER:"+BroadcastSystem.myChannel);
		BroadcastSystem.write(BroadcastSystem.myInstrChannel, DRONE_CONS);
		BroadcastSystem.setCollecting();
	}

	@Override
	public void run() {
		try {
			DataCache.updateRoundVariables();
			//TELL THE HQ WHERE WE ARE
			if (DataCache.hasMovedAndCollecting()){
				MapEngine.senseQueue.add(DataCache.currentLoc);
			}

			downloadReady = BroadcastSystem.read(BroadcastSystem.myInstrChannel);

		    MapLocation currentLocation = rc.getLocation();
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.DRONE);
            if (enemyRobots.length>0) {
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemy(enemyRobots);
                }
            } else if (rc.isCoreReady()) {
	            	//IF THE DATA IS READY TO DOWNLOAD
				if (downloadReady>=25000){
					//System.out.println("BEAVER TEST");
					//rc.setIndicatorString(1, "messaging");
					BroadcastSystem.prepareandsendLocsDataList(MapEngine.senseQueue, downloadReady);
					//System.out.println(MapEngine.senseQueue);
					MapEngine.resetSenseQueue();
					BroadcastSystem.write(BroadcastSystem.myInstrChannel,0);
					//System.out.println("BEAVER TEST END");
				//	System.out.println("BEAVER TESTCHANNEL");
				} else if (downloadReady==2){
					BroadcastSystem.write(BroadcastSystem.myInstrChannel, 0);
				}
                Direction[] directions = getDirectionsToward(this.theirHQ);
                Collections.shuffle(Arrays.asList(directions));
                for (Direction dir : directions) {
                    if (rc.canMove(dir) && senseNearbyTowers(currentLocation, dir)==0) {
                        rc.move(dir);
                    }
                }
            }
            
            
            
            
//		    if (Clock.getRoundNum() < 1900) {
////		        if (rc.isCoreReady() && rc.getSupplyLevel()<60 && rc.getLocation().distanceSquaredTo(this.myHQ)<40) {
////		            RobotPlayer.tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
////		        }
//		        
//		        if (rc.isCoreReady() && senseNearbyTowers(rc.getLocation())==0) {
//
//		            int fate2 = RobotPlayer.rand.nextInt(2);
//		            if (fate2==0) {
//		                  int fate = RobotPlayer.rand.nextInt(5);
//		                  Direction[] directions = getDirectionsToward(this.theirHQ);
//		                  Direction direction = directions[fate];
//		                  RobotPlayer.tryMove(direction);
//		            } else {
//		                RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
//		            }
//		        } else {
//		            
//		        }
//		    } else {
//		        MapLocation closest  = getClosestTower();
//		        if (closest != null) {
//		            RobotPlayer.tryMove(rc.getLocation().directionTo(closest));
//		        } else {
//		            RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
//		        }
//
//		    }
            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
		    transferSpecificSupplies(RobotType.DRONE, rc, nearbyAllies);
            rc.broadcast(DRONE_CURRENT_CHAN, rc.readBroadcast(DRONE_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
	
//	public static int getSupplierID(){
//		return supplierID;
//	}
}
