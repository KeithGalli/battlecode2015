package team010;

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


		if (rc.getType() == RobotType.HQ){
			System.out.println("I am HQ at string:" + rc.senseHQLocation().toString());
			System.out.println("I am HQ at :" + loc);
			theMap = MapLocation.getAllMapLocationsWithinRadiusSq(loc, 10000);
			for (MapLocation mapLoc:theMap){
				if (rc.senseTerrainTile(mapLoc)==TerrainTile.UNKNOWN){
					numUnknown++;
				}else if (rc.senseTerrainTile(mapLoc)==TerrainTile.OFF_MAP){
					numOffmap++;
					System.out.println(mapLoc);
				}else if (rc.senseTerrainTile(mapLoc)==TerrainTile.NORMAL){
					numNormal++;
				}
				else if (rc.senseTerrainTile(mapLoc)==TerrainTile.VOID){
					numVoid++;
				}
				//System.out.println(mapLoc.toString());
			}
			System.out.println(numUnknown);
			System.out.println(numOffmap);
			System.out.println(numNormal);
			System.out.println(numVoid);

		} else if (rc.getType() == RobotType.TOWER){
			System.out.println("I am tower at :" + loc);
		}

        try {
            switch(rc.getType()) {

            case HQ:
            	robot = new HQRobot(rc);
            	break;
            }

            // case TOWER:
            //         robot = new HQRobot(rc);
            //         break;



            // // Structures //
            // case AEROSPACELAB:
            //         robot = new HQRobot(rc);
            //         break;

            // case BARRACKS :
            //         robot = new HQRobot(rc);
            //         break;        	

            // case HANDWASHSTATION  :
            //         robot = new HQRobot(rc);
            //         break;  

            // case HELIPAD:
            //         robot = new HQRobot(rc);
            //         break;   

            // case MINERFACTORY:
            //         robot = new HQRobot(rc);
            //         break;    

            // case SUPPLYDEPOT:
            //         robot = new HQRobot(rc);
            //         break;             

            // case TANKFACTORY:
            //         robot = new HQRobot(rc);
            //         break;  

            // case TECHNOLOGYINSTITUTE:
            //         robot = new HQRobot(rc);
            //         break;                           
           	
           	// case TRAININGFIELD:
            //         robot = new HQRobot(rc);
            //         break;   
            // ///////////////

            // // Units //
            // case BASHER:
            //         robot = new HQRobot(rc);
            //         break;

            // case BEAVER:
            //         robot = new HQRobot(rc);
            //         break;        	

            // case COMMANDER:
            //         robot = new HQRobot(rc);
            //         break;  

            // case COMPUTER:
            //         robot = new HQRobot(rc);
            //         break;   

            // case DRONE:
            //         robot = new HQRobot(rc);
            //         break;    

            // case LAUNCHER:
            //         robot = new HQRobot(rc);
            //         break;             

            // case MINER:
            //         robot = new HQRobot(rc);
            //         break;     
           	
           	// case MISSILE:
            //         robot = new HQRobot(rc);
            //         break;   
 
           	// case SOLDIER:
            //         robot = new HQRobot(rc);
            //         break;     

           	// case TANK:
            //         robot = new HQRobot(rc);
            //         break;                                           
            // ///////////////
            // }
            robot.loop();
        } catch (Exception e) {
        	//e.printStackTrace();
    	}
    }
}

