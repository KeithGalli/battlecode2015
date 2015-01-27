package launchers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SUPPLIERRobot extends BaseRobot {
	
	public static int startQueue = 101;
	public static int endQueue = 101;
	public boolean isStillSupplying;
	public MapLocation locationSupplying;
	public boolean suppliedLocation;
	
	public SUPPLIERRobot(RobotController rc) throws GameActionException {
		super(rc);
		isStillSupplying = true;
		locationSupplying = null;
		suppliedLocation = true;
	}
	
	@Override
	public void run() {
		try{
			rc.setIndicatorString(0, "Supplier");
			int startSupplyQueue = rc.readBroadcast(SUPPLIER_START_QUEUE_CHAN);
			int endSupplyQueue = rc.readBroadcast(SUPPLIER_END_QUEUE_CHAN);
			System.out.println(suppliedLocation);
			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
			if(Clock.getRoundNum() < 1200){
			if(startSupplyQueue != endSupplyQueue && suppliedLocation){
				int x = rc.readBroadcast(endSupplyQueue-2);
				int y = rc.readBroadcast(endSupplyQueue -1);
				locationSupplying = new MapLocation(x,y);
				suppliedLocation = false;
				System.out.println("new Location to supply");
				rc.broadcast(SUPPLIER_END_QUEUE_CHAN, rc.readBroadcast(SUPPLIER_END_QUEUE_CHAN)-2);
			}
			System.out.println("start queue" + startSupplyQueue);
			System.out.println("end queue" + endSupplyQueue);
			if(rc.getHealth() <=10){
				isStillSupplying = false;
				if(rc.getLocation().distanceSquaredTo(this.myHQ)<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED){
					rc.transferSupplies((int) rc.getSupplyLevel(), this.myHQ);
				} else {
					RobotPlayer.tryMove(rc.getLocation().directionTo(this.myHQ));
				}
			}
			if(locationSupplying != null){
				travelToAndSupplyLocation(nearbyAllies);
			}
			} else{
				 MapLocation closest  = getClosestTower();
			        if (closest!=null) {
			            RobotPlayer.tryMove(rc.getLocation().directionTo(closest));
			        } else {
			            RobotPlayer.tryMove(rc.getLocation().directionTo(this.theirHQ));
			        }
			        transferSpecificSupplies(RobotType.DRONE, rc, nearbyAllies);
			}
//		if( startSupplyQueue != endSupplyQueue){
//			int x = rc.readBroadcast(startSupplyQueue);
//			int y = rc.readBroadcast(startSupplyQueue +1);
//			MapLocation locationToSupply = new MapLocation(x,y);
//			if(rc.getLocation().distanceSquaredTo(locationToSupply) <= 4){
//				transferSpecificSupplies(RobotType.MINER,rc);
//				rc.broadcast(SUPPLIER_START_QUEUE_CHAN, rc.readBroadcast(SUPPLIER_START_QUEUE_CHAN)+2);
//			} else if( rc.isCoreReady()){
//				if(rc.getSupplyLevel() < 1000){
//					RobotPlayer.tryMove(rc.getLocation().directionTo(this.myHQ));
//				}else{
//					RobotPlayer.tryMove(rc.getLocation().directionTo(locationToSupply));
//				} 
//			}
//		}
//			if(rc.getLocation().distanceSquaredTo(locationToSupply)<= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED){
//				int transferAmount = (int) Math.min(1000, rc.getSupplyLevel()/((endSupplyQueue-startSupplyQueue)/2));
//				rc.transferSupplies(transferAmount, locationToSupply);
//				rc.broadcast(SUPPLIER_START_QUEUE_CHAN, rc.readBroadcast(SUPPLIER_START_QUEUE_CHAN)+2);
//			} else if( rc.isCoreReady()){
//				if(rc.getSupplyLevel() < 1000){
//					RobotPlayer.tryMove(rc.getLocation().directionTo(this.myHQ));
//				}else{
//					RobotPlayer.tryMove(rc.getLocation().directionTo(locationToSupply));
//				} 
//			}
			
		 //else {
//			rc.broadcast(SUPPLIER_START_QUEUE_CHAN,104);
//			rc.broadcast(SUPPLIER_END_QUEUE_CHAN, 104);
//		}
		if(isStillSupplying){
			rc.broadcast(SUPPLIER_DRONES_CURRENT_CHAN, rc.readBroadcast(SUPPLIER_DRONES_CURRENT_CHAN)+1);
		}
		
		} catch (GameActionException e) {
			e.printStackTrace();
		}

	}
	
	public void travelToAndSupplyLocation(RobotInfo[] nearbyAllies) throws GameActionException{
		if(rc.getLocation().distanceSquaredTo(locationSupplying) <= 4){
			transferSpecificSupplies(RobotType.MINER,rc, nearbyAllies);
			suppliedLocation = true;
			locationSupplying = null;
		} else if( rc.isCoreReady()){
			if(rc.getSupplyLevel() < 1000){
				RobotPlayer.tryMove(rc.getLocation().directionTo(this.myHQ));
			}else{
				RobotPlayer.tryMove(rc.getLocation().directionTo(locationSupplying));
			} 
	}
	}

}
