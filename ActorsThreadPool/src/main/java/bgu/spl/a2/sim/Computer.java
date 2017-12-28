package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

	String computerType;
	long failSig;
	long successSig;

	//constructor of Computer
	public Computer(String computerType) {
		this.computerType = computerType;
	}

	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */

	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		for(String course:courses){
			if(coursesGrades.containsKey(course)){
				if(coursesGrades.get(course).intValue() <= 56) {
					return failSig;
				}
			}else return failSig;
		}
		return successSig;
	}

	public void setSuccessSig(long successSig ) {
		this.successSig=successSig;
	}

	public void setFailSig(long failSig ) {
		this.failSig=failSig;
	}

}