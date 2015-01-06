package Striker;

import battlecode.common.RobotController;

public abstract class BaseRobot {
	
		public static int jobChannel = 15;
		public static int jobList = 0;
        public RobotController rc;
        public int id;
        
        
        // Default constructor
        public BaseRobot(RobotController myRC) {
                rc = myRC;
                id = rc.getRobot().getID();
                
                DataCache.init(this); // this must come first
                BroadcastSystem.init(this);
        }
        
        // Actions for a specific robot
        abstract public void run();
        
        public void loop() {
                while (true) {
                        try {
                                run();
                        } catch (Exception e) {
                                // Deal with exception
//                                e.printStackTrace();
                        }
                        rc.yield();
                }
        }
}