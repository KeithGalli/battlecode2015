package drone_missle_strategy;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

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

	public static RobotController rc;
	public static int id;
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;


	// Default constructor
	public BaseRobot(RobotController myRC) {
		rc = myRC;
		id = rc.getID();
		this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();

		DataCache.init(this); //MUST COME FIRST
		BroadcastSystem.init(this);
		MapEngine.init(this);

		// DataCache.init(this); // this must come first
		// BroadcastSystem.init(this);
		// Functions.init(rc);
		
	}
	
	public MapLocation getClosestTower() {
	    MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
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

//	public MapLocation getMostIsolatedTower() {
//	    MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
//	    int xCount = 0;
//	    int yCount = 0;
//	    for (MapLocation tower : enemyTowers) {
//	        xCount += tower.x;
//	        yCount += tower.y;
//	    }
//	}
	
	public boolean withinRange(int unitCount1, int unitCount2, double idealValue, double threshold) {
	    return ((unitCount1*1.0/unitCount2-idealValue)<threshold);
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

    public RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
        return enemies;
    }

    public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
        if (enemies.length == 0) {
            return;
        }

        double minEnergon = Double.MAX_VALUE;
        MapLocation toAttack = null;
        for (RobotInfo info : enemies) {
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
			if(ri.supplyLevel<lowestSupply){
				lowestSupply = ri.supplyLevel;
				if(ri.type == RobotType.DRONE){
					transferAmount = 7*(rc.getSupplyLevel()-ri.supplyLevel)/8;
				} else{
					transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
				}
				suppliesToThisLocation = ri.location;
			}
		}
		if(suppliesToThisLocation!=null){
			rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
		}
	}
    
    public static void transferMinerSupplies(RobotController rc) throws GameActionException {
    	RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation suppliesToThisLocation = null;
		for(RobotInfo ri:nearbyAllies){
			if(ri.supplyLevel<lowestSupply){
				lowestSupply = ri.supplyLevel;
				if(ri.type == RobotType.MINER){
					transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
					suppliesToThisLocation = ri.location;
					if(suppliesToThisLocation!=null){
						rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
					}
				}
			}
		}
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