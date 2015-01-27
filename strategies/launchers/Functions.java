package launchers;

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

    public static int locToInt(MapLocation m){
            return (m.x*100 + m.y);
        }
    public static MapLocation intToLoc(int i){
            return new MapLocation(i/100,i%100);
        }

    static void displayArray(int[][] intArray){
        for(int x = 0;x<intArray.length;x++){
            String line = "";
            for(int y=0;y<intArray[0].length;y++){
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