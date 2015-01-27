package launchers;

import battlecode.common.*;

import java.util.*;


public class RobotPlayer {
    static RobotController rc;
    static MapLocation loc;
    static Team myTeam;
    static Team enemyTeam;
    static int myRange;
    static Random rand;
    static MapLocation[] theMap;
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    static int numSuppliers = 0;
    static int numUnknown=0;
    static int numOffmap=0;
    static int numNormal=0;
    static int numVoid=0;
    
    public static void run(RobotController rcIn) {
        rc= rcIn;
        BaseRobot robot = null;
        loc = rc.getLocation();
        rand = new Random(rc.getID());

        //int randseed = rc.getID();
        //Util.randInit(randseed, randseed * Clock.getRoundNum());


        // if (rc.getType() == RobotType.HQ){
        //  System.out.println("I am HQ at string:" + rc.senseHQLocation().toString());
        //  System.out.println("I am HQ at :" + loc);
        //  theMap = MapLocation.getAllMapLocationsWithinRadiusSq(loc, 10000);
        //  for (MapLocation mapLoc:theMap){
        //      if (rc.senseTerrainTile(mapLoc)==TerrainTile.UNKNOWN){
        //          numUnknown++;
        //      }else if (rc.senseTerrainTile(mapLoc)==TerrainTile.OFF_MAP){
        //          numOffmap++;
        //          System.out.println(mapLoc);
        //      }else if (rc.senseTerrainTile(mapLoc)==TerrainTile.NORMAL){
        //          numNormal++;
        //      }
        //      else if (rc.senseTerrainTile(mapLoc)==TerrainTile.VOID){
        //          numVoid++;
        //      }
        //      //System.out.println(mapLoc.toString());
        //  }
        //  System.out.println(numUnknown);
        //  System.out.println(numOffmap);
        //  System.out.println(numNormal);
        //  System.out.println(numVoid);

        // } else if (rc.getType() == RobotType.TOWER){
        //  System.out.println("I am tower at :" + loc);
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
//            	if(rc.readBroadcast(BaseRobot.SUPPLIER_DRONES_PREVIOUS_CHAN) <1){
//            		rc.broadcast(BaseRobot.SUPPLIER_ID_CHAN, rc.getID());
//            		robot = new SUPPLIERRobot(rc);
//            	} //else if(rc.readBroadcast(BaseRobot.SUPPLIER_DRONES_PREVIOUS_CHAN) <2){
////            		rc.broadcast(BaseRobot.SUPPLIER_TWO_ID_CHAN, rc.getID());
////            		robot = new SUPPLIERRobot(rc);
////            	} 
//            	else{
//                    robot = new DRONERobot(rc);
//            	}
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
    static void attackSomething() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
        if (enemies.length > 0) {
            rc.attackLocation(enemies[0].location);
        }
    }
       
    static void tryMove(Direction d) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 5) {
            rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
        }
    }
    
    static void trySpawn(Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8], type)) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
    }
    
    // This method will attempt to build in the given direction (or as close to it as possible)
    static void tryBuild(Direction d, RobotType type) throws GameActionException {
        int offsetIndex = 0;
        int[] offsets = {0,1,-1,2,-2,3,-3,4};
        int dirint = directionToInt(d);
        boolean blocked = false;
        while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
            offsetIndex++;
        }
        if (offsetIndex < 8) {
            rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
        }
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

