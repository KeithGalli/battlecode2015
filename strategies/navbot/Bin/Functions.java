package navbot;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class Functions {

    public static BaseRobot robot;
    public static RobotController rc;

    public static void init(BaseRobot myRobot) {
        robot = myRobot;
        rc = robot.rc;
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

    //NOT USED YET
    //TAKES IN AN ARRAY OF MAPLOCATIONS AND OUTPUTS A SINGLE INTEGER
    public static int locsToInt(MapLocation[] pastrs){
         int pastrInt = 0;
         int multiplier = 1;
         for (MapLocation pastr: pastrs){
             pastrInt += locToInt(pastr)*multiplier;
             multiplier*=10000;
         }
         return pastrInt;
     }

     // public static MapLocation[] intToLocs(int num){
     //     MapLocation[] pastrs = new MapLocation[DataCache.numEnemyPastrs];
     //     for (int i=0; i<DataCache.numEnemyPastrs; i++){
     //         pastrs[i] = intToLoc(num%10000);
     //         num/=10000;
     //     }
     //     return pastrs;
     // }
     
     //PURPOSE:
     //Used in NavSystem to find the closest waypoint
     //INPUTS:
     //MapLocation[] manyLocs: array of MapLocations to find the closest one.
     //MapLocation point: the reference point.
     //OUTPUTS:
     //Maplocation: the closest maplocation in manyLocs to point.
        public static MapLocation findClosest(MapLocation[] manyLocs, MapLocation point){
            int closestDist = 10000000;
            int challengerDist = closestDist;
            MapLocation closestLoc = null;
            for(MapLocation m:manyLocs){
                challengerDist = point.distanceSquaredTo(m);
                if(challengerDist<closestDist){
                    closestDist = challengerDist;
                    closestLoc = m;
                }
            }
            return closestLoc;
        }

    //PURPOSE:
    //Used in NavSystem to convert original to internal
    //Used in MapEngine to convert original to internal
    //Used in BroadcastSystem to convert for integer broadcasting
    //INPUT:
    //MapLocation m: the original maplocation as given by the battlecode map, example: (-13140,141593)
    //OUTPUT:
    //MapLocation: converted maplocation to internal coordinates, example: (30, 40)
    public static MapLocation locToInternalLoc(MapLocation m){
        MapLocation temploc = m.add(-DataCache.mapCenter.x, -DataCache.mapCenter.y);
        return temploc.add(MapEngine.internalMapCenter.x, MapEngine.internalMapCenter.y);
    }

    //PURPOSE:
    //Used in NavSystem to convert internal to original
    //Used in MapEngine to convert internal to original
    //Used in BroadcastSystem to convert from integer broadcasting
    //INPUT:
    //MapLocation m: an internal maplocation, example: (30,40)
    //OUTPUT:
    //MapLocation: converted maplocation to original maplocation as given by the battlecode map, example: (-13140,141593)
    public static MapLocation internallocToLoc(MapLocation m){
        MapLocation temploc = m.add(DataCache.mapCenter.x, DataCache.mapCenter.y);
        return temploc.add(-MapEngine.internalMapCenter.x, -MapEngine.internalMapCenter.y);
    }

    //PURPOSE:
    //Used in BroadcastSystem to broadcast maplocations
    //IMPORTANT: MUST BE AN INTERNAL MAPLOCATION, ORIGINAL MAPLOCATION WILL NOT WORK!
    //INPUT:
    //MapLocation m: an internal maplocation, example: (30,40) 
    //OUTPUT:
    //int: integer storing the maplocation information
    public static int locToInt(MapLocation m){
            return (m.x*1000 + m.y);
        }
    public static MapLocation intToLoc(int i){
            return new MapLocation(i/1000,i%1000);
        }

    //PURPOSE:
    //Used in debugging to display an array.
    static void displayArray(int[][] intArray){
        for(int y = 0;y<intArray[0].length;y++){
            String line = "";
            for(int x=0;x<intArray.length;x++){
                //line+=(voidID[x][y]==-1)?"_":".";
                int i = intArray[x][y];
                if((Integer)i==null){//a path
                    line+="X";
                }else{
                    line+=i;
                }
            }
            System.out.println(line);
        }
    }

}
    