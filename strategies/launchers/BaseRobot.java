package launchers;

import java.util.ArrayList;

import battlecode.common.*;

public abstract class BaseRobot {
    
    public final static int BEAVER_PREVIOUS_CHAN = 1, BEAVER_CURRENT_CHAN = 2;
    public final static int SUPPLY_DEPOT_PREVIOUS_CHAN = 3, SUPPLY_DEPOT_CURRENT_CHAN = 4;
    public final static int TECH_INST_PREVIOUS_CHAN = 5, TECH_INST_CURRENT_CHAN = 6;
    public final static int COMP_PREVIOUS_CHAN = 7, COMP_CURRENT_CHAN = 8;
    public final static int TRAIN_PREVIOUS_CHAN = 9, TRAIN_CURRENT_CHAN = 10;
    public final static int COMMANDER_PREVIOUS_CHAN = 11, COMMANDER_CURRENT_CHAN = 12;
    public final static int BARRACKS_PREVIOUS_CHAN = 13, BARRACKS_CURRENT_CHAN = 14;
    public final static int SOLDIER_PREVIOUS_CHAN = 15, SOLDIER_CURRENT_CHAN = 16;
    public final static int TANK_FACT_PREVIOUS_CHAN = 17, TANK_FACT_CURRENT_CHAN = 18;
    public final static int TANK_PREVIOUS_CHAN = 19, TANK_CURRENT_CHAN = 20;
    public final static int BASHER_PREVIOUS_CHAN = 21, BASHER_CURRENT_CHAN = 22;
    public final static int HELIPAD_PREVIOUS_CHAN = 23, HELIPAD_CURRENT_CHAN = 24;
    public final static int DRONE_PREVIOUS_CHAN = 25, DRONE_CURRENT_CHAN = 26;
    public final static int AERO_LAB_PREVIOUS_CHAN = 27, AERO_LAB_CURRENT_CHAN = 28;
    public final static int LAUNCHER_PREVIOUS_CHAN = 29, LAUNCHER_CURRENT_CHAN = 30;
    public final static int HAND_STATION_PREVIOUS_CHAN = 31, HAND_STATION_CURRENT_CHAN = 32;
    public final static int MINER_FACT_PREVIOUS_CHAN = 33, MINER_FACT_CURRENT_CHAN = 34;
    public final static int MINER_PREVIOUS_CHAN = 35, MINER_CURRENT_CHAN = 36;
    public final static int SUPPLIER_DRONES_PREVIOUS_CHAN = 37, SUPPLIER_DRONES_CURRENT_CHAN= 38;
    public final static int SOLDIERS_MADE = 39;
    
    public final static int SUPPLIER_NEEDED = 100;
    public final static int SUPPLIER_START_QUEUE_CHAN = 101;
    public final static int SUPPLIER_END_QUEUE_CHAN = 102;
    public final static int SUPPLIER_ID_CHAN = 103;
    public final static int SUPPLIER_TWO_ID_CHAN = 99;
    
    public final static int COMMANDER_START_QUEUE_CHAN = 200;
    public final static int COMMANDER_END_QUEUE_CHAN = 201;
    
    public final static int MINERS_TO_ATTACK_X = 50;
    public final static int MINERS_TO_ATTACK_Y = 51;
    public final static int NUM_MINERS_IN_POSITION = 52;
    
    public final static int LAUNCHERS_ATTACK = 53;
    
    public static int TESTCHANNEL = 2000;
    
	public static int SUPPLYDEPOT_COST = 100;
	public static int TECHNOLOGYINSTITUTE_COST = 200;
	public static int BARRACKS_COST = 300;
	public static int HELIPAD_COST = 300;
	public static int TRAININGFIELD_COST = 200;
	public static int TANKFACTORY_COST = 500;
	public static int MINERFACTORY_COST = 500;
	public static int HANDWASHSTATION_COST = 200;
	public static int AEROSPACELAB_COST = 500;
	public static int BEAVER_COST = 100;
	public static int COMPUTER_COST = 10;
	public static int SOLDIER_COST = 60;
	public static int BASHER_COST = 80;
	public static int MINER_COST = 60;
	public static int DRONE_COST = 125;
	public static int TANK = 250;
	public static int COMMANDER = 100;
	public static int LAUNCHER = 400;

    public static RobotController rc;
    public static int id;
    static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;
    public int startSupplierQueue;
    public int endSupplierQueue;
    public int supplierID;
    //private static HashSet<MapLocation> enemyTerritory = new HashSet<MapLocation>();
    


    // Default constructor
    public BaseRobot(RobotController myRC) {
        rc = myRC;
        id = rc.getID();
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        this.startSupplierQueue = 101;
        this.endSupplierQueue =101;
        this.supplierID = 0;

        DataCache.init(this); //MUST COME FIRST
        BroadcastSystem.init(this);
        Functions.init(this);
        
    }
    public int numMiners(RobotInfo[] enemies){
    	int miners = 0;
    	for(RobotInfo enemy : enemies){
    		if(enemy.type == RobotType.MINER){
    			miners ++;
    		}
    	}
    	return miners;
    }
    public MapLocation getOurClosestTowerToThem() {
        MapLocation[] ourTowers = rc.senseTowerLocations();
        int distanceToClosest = this.theirHQ.distanceSquaredTo(ourTowers[0]);
        MapLocation closest = ourTowers[0];
        for (MapLocation tower : ourTowers) {
            int distanceToTower = this.theirHQ.distanceSquaredTo(tower);
            if (distanceToTower<distanceToClosest) {
                distanceToClosest = distanceToTower;
                closest = tower;
            }
        }
        MapLocation closestOffset = closest.add(rc.getLocation().directionTo(this.theirHQ), 2);
        return closestOffset;
    }
    
    public static MapLocation getClosestTower() {
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        if (enemyTowers.length ==0) {
            return null;
        } else {
            int distanceToClosest = rc.getLocation().distanceSquaredTo(enemyTowers[0]);
            MapLocation closest = enemyTowers[0];
            for (MapLocation tower: enemyTowers) {
                int distanceToTower = rc.getLocation().distanceSquaredTo(tower);
                if (distanceToTower<distanceToClosest) {
                    distanceToClosest = distanceToTower;
                    closest = tower;
                }
            }
            return closest;            
        }

    }
    
    public MapLocation getFurthestTower() {
    	MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
    	if(enemyTowers.length == 0){
    		return null;
    	} else {
    		int distanceToFurthest = rc.getLocation().distanceSquaredTo(enemyTowers[0]);
    		MapLocation furthest = enemyTowers[0];
    		for ( MapLocation tower : enemyTowers) {
    			int distanceToTower = rc.getLocation().distanceSquaredTo(tower);
    			if(distanceToTower > distanceToFurthest) {
    				distanceToFurthest = distanceToTower;
    				furthest = tower;
    			}
    		}
    		return furthest;
    	}
    }
    
    public MapLocation[] getFurthestTowersFromEachOther() {
    	MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
    	MapLocation[] furthestTwo = new MapLocation[2];
    	int largestDistance = 0;
    	for(MapLocation loc : enemyTowers){
    		for(MapLocation loc2 : enemyTowers){
    			int distance = loc.distanceSquaredTo(loc2);
    			if(distance > largestDistance){
    				largestDistance = distance;
    				furthestTwo[0] = loc;
    				furthestTwo[1] = loc2;
    			}
    		}
    	}
    	return furthestTwo;
    }
    
    public void move(Direction dirToMove, MapLocation location) throws GameActionException {
    	
    	if(dirToMove != null && senseNearbyTowers(location.add(dirToMove))==0 ){
    		rc.move(dirToMove);
    	}
    }
    public void moveLauncher(Direction dirToMove, MapLocation location) throws GameActionException {
    	if(dirToMove != null && senseNearbyTowers(location.add(dirToMove))==0 && tankFreeLoc(rc.getLocation(),rc.senseNearbyRobots(24,myTeam),dirToMove) ){
    		rc.move(dirToMove);
    	}
    }
    public int senseNearbyTowers(MapLocation currentLocation, Direction direction) {
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation newLocation = currentLocation.add(direction);
        int count = 0;
        for (MapLocation tower : enemyTowers) {
            if (newLocation.distanceSquaredTo(tower)<=24)
                count += 1;
        }
        if (newLocation.distanceSquaredTo(this.theirHQ)<=24) {
            count+=1;
        }
        return count;
    }
    
    public int senseTowersClose(MapLocation location) {
    	MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        int count = 0;

        for (MapLocation tower : enemyTowers) {
            if (location.distanceSquaredTo(tower)<=40)
                count += 1;

        }
        if (location.distanceSquaredTo(this.theirHQ)<=40) {
            count+=1;
        }
        return count;
    }
    
    public boolean pathToFree(MapLocation location, RobotInfo[] allies, MapLocation locationAttacking) throws GameActionException{
    	for(int i=2 ; i<=3; i++){
    		MapLocation newLoc = location.add(location.directionTo(locationAttacking),i);
//    		if(rc.isLocationOccupied(newLoc)){
//    			return false;
//    		}
    		for(RobotInfo ally : allies){
    			if(newLoc.distanceSquaredTo(ally.location)<=2){
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    public static boolean tankFreeLoc(MapLocation location, RobotInfo[] allies, Direction toMove){
    	MapLocation newLoc = location.add(toMove);
    	for(RobotInfo ally : allies){
    		if(ally.type == RobotType.TANK && newLoc.distanceSquaredTo(ally.location)<=16){
    			return false;
    		}
    	}
    	return true;
    }
    
    public int senseNearbyTowers(MapLocation location) {
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        int count = 0;
        MapLocation newLocation = location.add(location.directionTo(this.theirHQ));
        MapLocation newLocation2 = location.add(location.directionTo(getClosestTower()));
        for (MapLocation tower : enemyTowers) {
            if (newLocation.distanceSquaredTo(tower)<=24)
                count += 1;
            if (newLocation2.distanceSquaredTo(tower)<=24)
                count+= 1;
        }
        if (newLocation.distanceSquaredTo(this.theirHQ)<=24) {
            count+=1;
        }
        return count;
    } 
    
    public static int senseNearbyTowersStat(MapLocation location) {
    	MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        int count = 0;
        MapLocation newLocation = location.add(location.directionTo(rc.senseEnemyHQLocation()));
        MapLocation newLocation2 = location.add(location.directionTo(getClosestTower()));
        for (MapLocation tower : enemyTowers) {
            if (newLocation.distanceSquaredTo(tower)<=24)
                count += 1;
            if (newLocation2.distanceSquaredTo(tower)<=24)
                count+= 1;
        }
        if (newLocation.distanceSquaredTo(rc.senseEnemyHQLocation())<=24) {
            count+=1;
        }
        return count;
    }
    
    public static boolean senseTanksAndLaunchersToAvoid(RobotInfo[] enemies, Direction dirToMove , MapLocation loc){
    	//boolean safeToTraverse = false;
    	MapLocation newLoc = loc.add(dirToMove);
    	for(RobotInfo enemy : enemies){
    		if(enemy.type == RobotType.TANK || enemy.type == RobotType.LAUNCHER && enemy.location.distanceSquaredTo(newLoc)<=16){
    			return false;
    		}
    	}
    	return true;
    }

    
    public boolean withinRange(int unitCount1, int unitCount2, double idealValue, double threshold) {
        return ((unitCount1*1.0/unitCount2-idealValue)<threshold);
    }
    
    public Direction[] getDirectionsAway(MapLocation awayFrom) {
        Direction away = rc.getLocation().directionTo(awayFrom).opposite();
        Direction[] dirs = {away, away.rotateLeft(), away.rotateRight(), away.rotateLeft().rotateLeft(), away.rotateRight().rotateRight()};
        return dirs;
    }
    
    public Direction[] getDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = {toDest,
                toDest.rotateLeft(), toDest.rotateRight(),
            toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

        return dirs;
    }

    public Direction getMoveDir(MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public Direction getSpawnDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(rc.senseEnemyHQLocation());
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }
        return null;
    }

    public Direction getBuildDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(rc.senseEnemyHQLocation());
        for (Direction d : dirs) {
            if (rc.canBuild(d, type)) {
                return d;
            }
        }
        return null;
    }

    public RobotInfo[] getAllies() {
        RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
        return allies;
    }

    public RobotInfo[] getEnemiesInAttackingRange(RobotType type) {
        RobotInfo[] enemies = rc.senseNearbyRobots(type.attackRadiusSquared, theirTeam);
        return enemies;
    }

    public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
        if (enemies.length == 0) {
            return;
        }

        double minEnergon = Double.MAX_VALUE;
        MapLocation toAttack = null;
        for (RobotInfo info : enemies) {
            if(info.type == RobotType.TOWER){
                rc.attackLocation(info.location);
                return;
            } else if(info.type == RobotType.HQ){
                rc.attackLocation(info.location);
                return;
            }
            if (info.health < minEnergon) {
                toAttack = info.location;
                minEnergon = info.health;
            }
        }

        rc.attackLocation(toAttack);
    }
    
    public static void transferSupplies(RobotController rc) throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for(RobotInfo ri:nearbyAllies){
            RobotType type = ri.type;
//            if(ri.supplyLevel == 0){
//            	if(type == RobotType.DRONE){
//            		transferAmount = Math.min(3*(rc.getSupplyLevel()-ri.supplyLevel)/4, 2000);
//	            	lowestSupply = ri.supplyLevel;
//	            	rc.transferSupplies((int)transferAmount, ri.location);
//	            	//return;
//            	} else if(type == RobotType.BEAVER) {
//            		transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/4, 500);
//            		rc.transferSupplies((int)transferAmount, ri.location);
//            		//return;
//            	} else if(type == RobotType.MINER) {
//            		transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
//            		rc.transferSupplies((int)transferAmount, ri.location);
//            		//return;
//            	} //else {
////            		continue;
////            	}
//            } else if(ri.supplyLevel<lowestSupply && !(type== RobotType.BARRACKS || type == RobotType.HELIPAD || type == RobotType.MINERFACTORY || type == RobotType.TANKFACTORY || type == RobotType.HQ)){
//                lowestSupply = ri.supplyLevel;
////              if(type == RobotType.DRONE){
////                  transferAmount = 6*(rc.getSupplyLevel()-ri.supplyLevel)/8;
////              } else {
////                  transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/4;
////              }
//                transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/4, 500);
//                suppliesToThisLocation = ri.location;
//            }
            
            if(ri.supplyLevel < 50){
            	if(type == RobotType.DRONE){
            		transferAmount = Math.min(3*(rc.getSupplyLevel()-ri.supplyLevel)/4, 2000);
            		rc.transferSupplies((int)transferAmount, ri.location);
            	} else if( type == RobotType.BEAVER){
            		transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/4, 500);
            		rc.transferSupplies((int)transferAmount, ri.location);
            	} else if( type == RobotType.MINER){
            		transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
            		rc.transferSupplies((int)transferAmount, ri.location);
            	}
            }

        }
//        if(suppliesToThisLocation!=null){
//            rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
//        }
    }
    
    public static void hqTransferSupplies(RobotController rc) throws GameActionException {
    	RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
    	
    	for (RobotInfo ri : nearbyAllies){
    		double transferAmount = 0;
    		RobotType type = ri.type;
    		if(ri.ID== rc.readBroadcast(SUPPLIER_ID_CHAN) || ri.ID == rc.readBroadcast(SUPPLIER_TWO_ID_CHAN)){
    			transferAmount =  Math.min(3*rc.getSupplyLevel()/4, 20000);
    			rc.transferSupplies((int) transferAmount , ri.location);
    		} else if(ri.supplyLevel < 50){
            	if(type == RobotType.DRONE){
            		transferAmount = Math.min(3*(rc.getSupplyLevel()-ri.supplyLevel)/4, 2000);
            		rc.transferSupplies((int)transferAmount, ri.location);
            	} else if( type == RobotType.BEAVER){
            		transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/4, 500);
            		rc.transferSupplies((int)transferAmount, ri.location);
            	} else if( type == RobotType.MINER){
            		transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
            		rc.transferSupplies((int)transferAmount, ri.location);
            	}
            }
    	}
    }
    
    public static void hqTransferAllSuppliesForRestOfGame(RobotController rc) throws GameActionException {
    	RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
    	for(RobotInfo ri : nearbyAllies){
    		double transferAmount = 0;
    		if(Clock.getRoundNum() > 1000){
    			if(ri.type== RobotType.TANK){
    				transferAmount = (2000 - Clock.getRoundNum())*45;
    				if( rc.getSupplyLevel() < transferAmount){
    					transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 2500);
    				}
    				//transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
    				rc.transferSupplies((int)transferAmount, ri.location);
    				
    			} else if(ri.type == RobotType.COMMANDER){
    				transferAmount = (2000 - Clock.getRoundNum())*15;
	    			if(rc.getSupplyLevel() < transferAmount){
	    				transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
	    			}
	    			rc.transferSupplies((int)transferAmount, ri.location);
    			} else if(ri.type == RobotType.LAUNCHER){
    				transferAmount = (2000 - Clock.getRoundNum())*45;
    				if( rc.getSupplyLevel() < transferAmount){
    					transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 2500);
    				}
    				//transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
    				rc.transferSupplies((int)transferAmount, ri.location);
    			}
    		} else {
	    		if(ri.type == RobotType.BEAVER && Clock.getRoundNum() < 1000){
	    			if(ri.supplyLevel < 10){
		    			transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/4, 20);
		    			rc.transferSupplies((int)transferAmount, ri.location);
	    			}
	    		} else if(ri.type == RobotType.TANK){
	    			transferAmount = (2000 - Clock.getRoundNum())*10;
	    			if(rc.getSupplyLevel() < transferAmount){
	    				transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 2500);
	    				
	    			}
	    			System.out.println("transferring " + transferAmount );
	    			rc.transferSupplies((int)transferAmount, ri.location);
	    		} else if(ri.type == RobotType.DRONE){
	    			transferAmount = (2000 - Clock.getRoundNum())*4;
	    			if(rc.getSupplyLevel() < transferAmount){
	    				transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
	    			} 
	    			rc.transferSupplies((int)transferAmount, ri.location);
	    		} else if(ri.type == RobotType.MINER && Clock.getRoundNum() < 1000){
	    			transferAmount = (2000 - Clock.getRoundNum())*4;
	    			if(rc.getSupplyLevel() < transferAmount){
	    				transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
	    			}
	    			rc.transferSupplies((int)transferAmount, ri.location);
	    		} else if(ri.type == RobotType.COMMANDER){
	    			transferAmount = (2000 - Clock.getRoundNum())*15;
	    			if(rc.getSupplyLevel() < transferAmount){
	    				transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
	    			}
	    			rc.transferSupplies((int)transferAmount, ri.location);
	    		} else if(ri.type == RobotType.SOLDIER){
	    			transferAmount = (2000 - Clock.getRoundNum())*5;
	    			if(rc.getSupplyLevel() < transferAmount){
	    				transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 1500);
	    			}
	    			rc.transferSupplies((int)transferAmount, ri.location);
	    		} else if(ri.type == RobotType.LAUNCHER){
	    			transferAmount = (2000 - Clock.getRoundNum())*25;
    				if( rc.getSupplyLevel() < transferAmount){
    					transferAmount = Math.min((rc.getSupplyLevel()-ri.supplyLevel)/2, 2500);
    				}
    				//transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
    				rc.transferSupplies((int)transferAmount, ri.location);
	    		}
    		}
    	}
    }
    
    public static void transferDroneSupplies(RobotController rc) throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for(RobotInfo ri:nearbyAllies){
            if(ri.supplyLevel<lowestSupply){
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
                suppliesToThisLocation = ri.location;
            }
        }
        if(suppliesToThisLocation!=null){
            rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
        }
    }
    
    public static void transferSpecificSupplies(RobotType type, RobotController rc, RobotInfo[] nearbyAllies) throws GameActionException {
        //RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for(RobotInfo ri:nearbyAllies){
        	if(ri.supplyLevel == 0 && ri.type == type){
        		lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
                suppliesToThisLocation = ri.location;
                if(Clock.getBytecodesLeft() > 520){
                	rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
                }
                return;
        	} else if(ri.type == type && ri.supplyLevel<lowestSupply ){
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
                suppliesToThisLocation = ri.location;
//                if(ri.type == type){
//                    transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
//                    suppliesToThisLocation = ri.location;
//                    if(suppliesToThisLocation!=null){
//                        rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
//                    }
//                }
            }
        }
        if(Clock.getBytecodesLeft() > 520){
		    if(suppliesToThisLocation!=null){
		        rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
		    }
        }
    }
    
    public static int numTanksSurrounding(RobotController rc, RobotInfo[] nearbyAllies) {
    	int numTanks = 0;
    	for( RobotInfo ri : nearbyAllies){
    		if(ri.type == RobotType.TANK){
    			numTanks ++;
    		}
    	}
    	return numTanks;
    }

    
    public static boolean isLocationInEnemyTerritory(MapLocation loc) {
        MapLocation[] enemyTowerLocations = rc.senseEnemyTowerLocations();
        MapLocation myLocation = rc.getLocation();
        
        for(MapLocation towerLoc : enemyTowerLocations) {
            if(towerLoc.distanceSquaredTo(myLocation) <= 24) {
                rc.setIndicatorString(1, "true"); 
                return true;
            }
        }
        
        if(rc.senseEnemyHQLocation().distanceSquaredTo(myLocation) <= 24) {
            rc.setIndicatorString(1, "true");
            return true;
        }
        rc.setIndicatorString(1, "false");
        return false;
        //return enemyTerritory.contains(loc);
    }
    
    // Actions for a specific robot
    abstract public void run();


    public void loop() {
        while (true) {
            try {
                run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rc.yield();
        }
    }

}