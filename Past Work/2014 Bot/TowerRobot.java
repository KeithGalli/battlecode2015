package team115;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class TowerRobot extends BaseRobot {
	
	MapLocation[] attacklocations = new MapLocation[132];
	int attackcount = 0;
	int attackrange;
	
	boolean ortho = true;
	boolean first;
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static Direction[] diagDirs = {Direction.NORTH_EAST,Direction.SOUTH_EAST,Direction.SOUTH_WEST,Direction.NORTH_WEST};

	public TowerRobot(RobotController myRC) {
		super(myRC);
		Functions.init(rc);
		DataCache.currentLocation = rc.getLocation();
		attackrange = rc.getType().attackRadiusMaxSquared;
		initializeAttackLocations();
	}

	@Override
	public void run() {
		try{
			DataCache.updateRoundVariables();
			if (attackcount == 132){
				attackcount = 0;
			}
			MapLocation loc = null;
			while (loc == null){
				loc = attacklocations[attackcount];
				attackcount++;
			}
			
			
					if (DataCache.currentLocation.distanceSquaredTo(loc)>8){
						rc.attackSquare(loc);
					}
					else{
						rc.attackSquareLight(loc);
					}
		
		}catch (Exception e){
			
		}

	}
	
	private void initializeAttackLocations(){
		MapLocation currentLoc;
		int dircount = 0;
		for (Direction dir: orthoDirs){	
			first = false;
			int reversecount = 18;
			currentLoc = DataCache.currentLocation;
			while (reversecount>-1){
				MapLocation trialLoc = currentLoc.add(dir);
				TerrainTile tile = rc.senseTerrainTile(trialLoc);
				boolean valid = tile!=TerrainTile.OFF_MAP && tile!=TerrainTile.VOID;
				if (valid||!first){
				attacklocations[dircount + reversecount] = trialLoc;
					if(!valid){
						first = true;
					}
				}
				reversecount--;
				currentLoc = trialLoc;
			}
			dircount+=19;
		}
		for (Direction dir: diagDirs){
			first = false;
			int reversecount = 13;
			currentLoc = DataCache.currentLocation;
			while (reversecount>-1){
				MapLocation trialLoc = currentLoc.add(dir);
				TerrainTile tile = rc.senseTerrainTile(trialLoc);
				boolean valid = tile!=TerrainTile.OFF_MAP && tile!=TerrainTile.VOID;
				if (valid||!first){
				attacklocations[dircount + reversecount] = trialLoc;
					if(!valid){
						first = true;
					}
				}
				reversecount--;
				currentLoc = trialLoc;
			}
			dircount+=14;
		}
	}

}
