package team010;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class DataCache {

	public static BaseRobot robot;
	public static RobotController rc;

	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;
	}
}
	