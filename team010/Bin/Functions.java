package team010;

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
}
    