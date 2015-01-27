package launchers;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class NavSystem {

    public static RobotController rc;

    public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
    public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
    public static Direction[] diagDirs = {Direction.NORTH_EAST,Direction.NORTH_WEST,Direction.SOUTH_EAST,Direction.SOUTH_WEST};

    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};
    static Direction allDirections[] = Direction.values();


    public static void HQinit(RobotController rcIn) {
        rc = rcIn;
    }

    public static void TOWERinit(RobotController rcIn) {
        rc = rcIn;
    }

    public static void UNITinit(RobotController rcIn) {
        rc = rcIn;
    }

    ///////////////////////////////////////////////////////////////////////////
    //Snail Navigation
    ///////////////////////////////////////////////////////////////////////////
    public static Direction dumbNav(MapLocation loc) throws GameActionException{
        return snailNav(DataCache.currentLoc.directionTo(loc));
    }


    public static Direction snailNav(Direction chosenDirection) throws GameActionException{
        return tryToMove(chosenDirection, true, rc, directionalLooks, allDirections);
    }

    static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();

    static boolean canMove(Direction dir, boolean selfAvoiding,RobotController rc){
        //include both rc.canMove and the snail Trail requirements
        if(selfAvoiding){
            MapLocation resultingLocation = rc.getLocation().add(dir);
            for(int i=0;i<snailTrail.size();i++){
                MapLocation m = snailTrail.get(i);
                if(!m.equals(rc.getLocation())){
                    if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)||resultingLocation.distanceSquaredTo(DataCache.enemyHQ)<=16){
                        //rc.setIndicatorString(2, "adjacentto");
                        return false;
                    }
                }
            }
        }
        //if you get through the loop, then dir is not adjacent to the icky snail trail
        //rc.setIndicatorString(2, "canmove in " + dir);
        return rc.canMove(dir);
    }

    private static Direction tryToMove(Direction chosenDirection,boolean selfAvoiding,RobotController rc, int[] directionalLooks, Direction[] allDirections) throws GameActionException{
        while(snailTrail.size()<2)
            snailTrail.add(new MapLocation(-1,-1));
        if(rc.isCoreReady()){
            snailTrail.remove(0);
            snailTrail.add(rc.getLocation());
            for(int directionalOffset:directionalLooks){
                //rc.setIndicatorString(0, "notmoving");
                //rc.setIndicatorString(2, "notmoving in a direction");
                int forwardInt = chosenDirection.ordinal();
                Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
                if(rc.getType() == RobotType.SOLDIER  ){
                	if( canMove(trialDir,selfAvoiding,rc) && BaseRobot.senseNearbyTowersStat(rc.getLocation().add(trialDir))==0){
                		return trialDir;
                	}
                }
                else if(rc.getType()== RobotType.LAUNCHER){
                	if(canMove(trialDir,selfAvoiding,rc) && BaseRobot.senseNearbyTowersStat(rc.getLocation().add(trialDir))==0 ){
                		return trialDir;
                	}
                }
                else if(canMove(trialDir,selfAvoiding,rc)){
                    //rc.setIndicatorString(0, "moving in" + trialDir);
                    //rc.setIndicatorString(2, String.valueOf(rc.canMove(trialDir)));
                    //
                	return trialDir;
                    //rc.move(trialDir);
                    //snailTrail.remove(0);
                    //snailTrail.add(rc.getLocation());
                    //break;
                }

                //rc.setIndicatorString(2, String.valueOf(rc.canMove(trialDir)));
            }
            //System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    //Simple Navigation
    ///////////////////////////////////////////////////////////////////////////
    public static void simpleNav(Direction chosenDirection) throws GameActionException{
        if(rc.isCoreReady()){
            for(int directionalOffset:directionalLooks){
                int forwardInt = chosenDirection.ordinal();
                Direction trialDir = allDirections[(forwardInt+directionalOffset+8)%8];
                if(rc.canMove(trialDir)){
                    rc.move(trialDir);
                    break;
                }
            }
        }
    }


}
    