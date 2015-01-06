package Striker;

import battlecode.common.RobotController;

public class BroadcastSystem {
	public static BaseRobot robot;
    public static RobotController rc;
	
	public static void init(BaseRobot myRobot) {
        robot = myRobot;
        rc = robot.rc;
	}
	
	public static int read(int channel) {
        try {
                if (rc != null) {
                        int message = rc.readBroadcast(channel);
                        return message;
                }
                return -9999;
        } catch (Exception e) {
                return -9999;
        }
	}
	
	public static void write(int channel, int message) {
		if (rc != null) {
            try {
                 rc.broadcast(channel, message);
                
            } catch (Exception e) {
            }
		}
	}

}
