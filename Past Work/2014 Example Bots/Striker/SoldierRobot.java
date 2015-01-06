package Striker;

import battlecode.common.RobotController;
import battlecode.common.Team;

public class SoldierRobot extends BaseRobot {
	
	public SoldierJob job = SoldierJob.NEW;
	public SoldierJob nextjob;

	public SoldierRobot(RobotController rc) {
		super(rc);
		
	}

	@Override
	public void run() {
		try {
//          DataCache.updateRoundVariables();	
			switch (job){
			case NEW:
				initializeRobot();
				break;
			
			case PASTR_1:
				findSpot();
				makePasture();
				break;
				
			case WORKER_1:
				patrol();
				break;
				
			case HELPER_1:
				herdCows();
				break;
			}
				
			if (nextjob != null) {
				job = nextjob;
                nextjob = null; // clear the state for the next call of run() to use
            }
		} catch (Exception e) {
			//			                    System.out.println("caught exception before it killed us:");
			//			                    System.out.println(rc.getRobot().getID());
			//			                    e.printStackTrace();
		}
	}
	
	private void patrol() {
		// TODO Auto-generated method stub
		
	}

	private void herdCows() {
		// TODO Auto-generated method stub
		
	}

	private void makePasture() {
		// TODO Auto-generated method stub
		
	}

	private void findSpot() {
		if (DataCache.team.equals(Team.A)){ //this is really bad
		}
		
	}

	public void initializeRobot(){
		int jobList = BroadcastSystem.read(jobChannel);
		int tempjobList = jobList;
		int numjob = 1; //default
		while (tempjobList%10 != 0){
			tempjobList /= 10;
			numjob+=1;
		}
		BroadcastSystem.write(jobChannel, jobList+10^(numjob-1));
		nextjob = SoldierJob.values()[numjob];
	}

}
