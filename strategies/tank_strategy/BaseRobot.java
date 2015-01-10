package tank_strategy;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public abstract class BaseRobot {

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