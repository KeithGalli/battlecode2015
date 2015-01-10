package tank_strategy;

import battlecode.common.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Random;

public class BroadcastSystem {

	public static BaseRobot robot;
	public static RobotController rc;

	public static void init(BaseRobot myRobot) {
		robot = myRobot;
		rc = robot.rc;
	}
}
	