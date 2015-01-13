package navbot;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class RobotPlayer {
	static RobotController rc;
	static MapLocation loc;
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	static Random rand;
	static MapLocation[] theMap;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};


	static int numUnknown=0;
	static int numOffmap=0;
	static int numNormal=0;
	static int numVoid=0;
	
	public static void run(RobotController rcIn) {
		rc= rcIn;
		BaseRobot robot = null;
		loc = rc.getLocation();

		//int randseed = rc.getID();
        //Util.randInit(randseed, randseed * Clock.getRoundNum());


		// if (rc.getType() == RobotType.HQ){
		// 	System.out.println("I am HQ at string:" + rc.senseHQLocation().toString());
		// 	System.out.println("I am HQ at :" + loc);
		// 	theMap = MapLocation.getAllMapLocationsWithinRadiusSq(loc, 10000);
		// 	for (MapLocation mapLoc:theMap){
		// 		if (rc.senseTerrainTile(mapLoc)==TerrainTile.UNKNOWN){
		// 			numUnknown++;
		// 		}else if (rc.senseTerrainTile(mapLoc)==TerrainTile.OFF_MAP){
		// 			numOffmap++;
		// 			System.out.println(mapLoc);
		// 		}else if (rc.senseTerrainTile(mapLoc)==TerrainTile.NORMAL){
		// 			numNormal++;
		// 		}
		// 		else if (rc.senseTerrainTile(mapLoc)==TerrainTile.VOID){
		// 			numVoid++;
		// 		}
		// 		//System.out.println(mapLoc.toString());
		// 	}
		// 	System.out.println(numUnknown);
		// 	System.out.println(numOffmap);
		// 	System.out.println(numNormal);
		// 	System.out.println(numVoid);

		// } else if (rc.getType() == RobotType.TOWER){
		// 	System.out.println("I am tower at :" + loc);
		// }

        try {
            switch(rc.getType()) {

            case HQ:
            	robot = new HQRobot(rc);
            	break;       

            case TOWER:
                    robot = new TOWERRobot(rc);
                    break;



            // Structures //
            case AEROSPACELAB:
                    robot = new AEROSPACELABRobot(rc);
                    break;

            case BARRACKS :
                    robot = new BARRACKSRobot(rc);
                    break;        	

            case HANDWASHSTATION  :
                    robot = new HANDWASHSTATIONRobot(rc);
                    break;  

            case HELIPAD:
                    robot = new HELIPADRobot(rc);
                    break;   

            case MINERFACTORY:
                    robot = new MINERFACTORYRobot(rc);
                    break;    

            case SUPPLYDEPOT:
                    robot = new SUPPLYDEPOTRobot(rc);
                    break;             

            case TANKFACTORY:
                    robot = new TANKFACTORYRobot(rc);
                    break;  

            case TECHNOLOGYINSTITUTE:
                    robot = new TECHNOLOGYINSTITUTERobot(rc);
                    break;                           
           	
           	case TRAININGFIELD:
                    robot = new TRAININGFIELDRobot(rc);
                    break;   
            ///////////////

            // Units //
            case BASHER:
                    robot = new BASHERRobot(rc);
                    break;

            case BEAVER:
                    robot = new BEAVERRobot(rc);
                    break;        	

            case COMMANDER:
                    robot = new COMMANDERRobot(rc);
                    break;  

            case COMPUTER:
                    robot = new COMPUTERRobot(rc);
                    break;   

            case DRONE:
                    robot = new DRONERobot(rc);
                    break;    

            case LAUNCHER:
                    robot = new LAUNCHERRobot(rc);
                    break;             

            case MINER:
                    robot = new MINERRobot(rc);
                    break;     
           	
           	case MISSILE:
                    robot = new MISSILERobot(rc);
                    break;   
 
           	case SOLDIER:
                    robot = new SOLDIERRobot(rc);
                    break;     

           	case TANK:
                    robot = new TANKRobot(rc);
                    break;                                           
            ///////////////
            }
            robot.loop();
        } catch (Exception e) {
        	//e.printStackTrace();
    	}
    }
    static void trySpawn(Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = Functions.directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }
    
}

