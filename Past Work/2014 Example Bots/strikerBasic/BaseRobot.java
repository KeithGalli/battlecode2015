package strikerBasic;

import battlecode.common.RobotController;

public abstract class BaseRobot {
	
		public static int allChannel = 203;
		public static int assignGroup = 202;
		public static int rallyChannel = 201;
		public static int headChannel = 200;
		public static int jobList = 0;
        public RobotController rc;
        public int id;
        
        public boolean enemyNukeHalfDone = false;
        
        // Default constructor
        public BaseRobot(RobotController myRC) {
                rc = myRC;
                id = rc.getRobot().getID();
                
                DataCache.init(this); // this must come first
                BroadcastSystem.init(this);
//               EncampmentJobSystem.init(this);
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