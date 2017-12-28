/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.Iterator;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;

import bgu.spl.a2.sim.actions.AddStudent;
import bgu.spl.a2.sim.actions.Unregister;
import bgu.spl.a2.sim.actions.OpenANewCourse;
import bgu.spl.a2.sim.actions.ParticipatingInCourse;
import bgu.spl.a2.sim.actions.RegisterWithPreferences;
import bgu.spl.a2.sim.actions.CloseACourse;
import bgu.spl.a2.sim.actions.CheckAdministrativeObligations;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.sim.actions.AddSpaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {


	public static ActorThreadPool actorThreadPool;
	private static Warehouse WH;
	private static String JsonPath;
	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start(){
		JsonParser PARSER = new JsonParser();
		JsonObject JSON= null;
		try{
			JSON = PARSER.parse(new FileReader(JsonPath)).getAsJsonObject();
		}catch (FileNotFoundException e){}

		int NUMBER_OF_THREADS = JSON.get("threads").getAsInt();									//nThread
		JsonArray COMPUTER = JSON.get("Computers").getAsJsonArray();							//Array with computer
		JsonArray PHASE1 = JSON.get("Phase 1").getAsJsonArray();								//Phase1 Actions
		JsonArray PHASE2 = JSON.get("Phase 2").getAsJsonArray();								//Phase2 Actions
		JsonArray PHASE3 = JSON.get("Phase 3").getAsJsonArray();								//Phase3 Actions
		CountDownLatch PHASE1_CLOCK = new CountDownLatch( PHASE1.size());	//monitor for Phase1 - The program will wait until phase1 is over
		CountDownLatch PHASE2_CLOCK = new CountDownLatch(PHASE2.size());						//monitor for Phase2 - The program will wait until phase2 is over
		CountDownLatch PHASE3_CLOCK = new CountDownLatch(PHASE3.size());						//monitor for Phase3 - The program will wait until phase3 is over
		attachActorThreadPool(new ActorThreadPool(NUMBER_OF_THREADS));												//build ActorThreadPool
		actorThreadPool.start();
		createWareHouse(COMPUTER);

/**						 START OF  PHASE 1 							  						 **/
		createWareHouse(COMPUTER );

		SUBMIT_FUNCTION(PHASE1 ,PHASE1_CLOCK);

/** 					 START THE THREAD POOL												 **/

		try {
			PHASE1_CLOCK.await();
		} catch (InterruptedException e) {}

/**						 START OF  PHASE 2 							  						 **/
		SUBMIT_FUNCTION(PHASE2 ,PHASE2_CLOCK);
		try {
			PHASE2_CLOCK.await();
		} catch (InterruptedException e) {}

/**						 START OF  PHASE 3 							  						 **/
		SUBMIT_FUNCTION(PHASE3 ,PHASE3_CLOCK);

		try {
			PHASE3_CLOCK.await();
		} catch (InterruptedException e) {}

/**						 SHUTDOWN THE OPOOL - END OF MISSION 								 **/
		HashMap<String,PrivateState> END = end();
		try{
			FileOutputStream fout = new FileOutputStream("result.ser");
			try{
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(END);
			}catch(IOException ioProblem){}
		}catch(FileNotFoundException fileNotFound){}
	}


	/**
	 * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	 *
	 * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	 */
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool = myActorThreadPool;
	}

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String,PrivateState> end(){
		try{
			actorThreadPool.shutdown();
		}catch(InterruptedException e){}
		return actorThreadPool.map_of_private_State();
	}

	public static void main(String [] args){
		JsonPath = args[0];
		start();

	}

	// "SUBMIT_FUNCTION" receive "PHASE" which is a JSON-ARRAY type and submit all the extracted commands to the ActorThreadPool
	public static void SUBMIT_FUNCTION(JsonArray PHASE , CountDownLatch C ){
		String ACTION_TO_EXECUTE = "";
		String department ,  course , computer , spaces , studentID ,  grade ;
		String[] Prerequisites ,  grades ,  preferences , students , courses;
		List<String> condition;

		for(JsonElement action :PHASE ){
			ACTION_TO_EXECUTE = action.getAsJsonObject().get("Action").getAsString();

			switch (ACTION_TO_EXECUTE ){

				case "Add Spaces" :
					spaces  = action.getAsJsonObject().get("Number").getAsString();
					course = action.getAsJsonObject().get("Course").getAsString();
					AddSpaces addSpace = new AddSpaces( course ,  spaces);
					addSpace.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(addSpace , course , new CoursePrivateState());
					break;

				case "Open Course" :
					department = action.getAsJsonObject().get("Department").getAsString();
					course = action.getAsJsonObject().get("Course").getAsString();
					Prerequisites = getStringArray( action.getAsJsonObject().get("Prerequisites").getAsJsonArray());
					spaces = action.getAsJsonObject().get("Space").getAsString();
					OpenANewCourse OpenCourse = new OpenANewCourse( department,  course ,  spaces ,  Prerequisites);
					OpenCourse.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(OpenCourse , department , new DepartmentPrivateState());
					break;

				case "Add Student" :
					department = action.getAsJsonObject().get("Department").getAsString();
					studentID = action.getAsJsonObject().get("Student").getAsString();
					AddStudent addStudent = new AddStudent( department,  studentID);
					addStudent.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(addStudent , department , new DepartmentPrivateState());
					break;

				case "Participate In Course" :
					course = action.getAsJsonObject().get("Course").getAsString();
					studentID = action.getAsJsonObject().get("Student").getAsString();
					grade = action.getAsJsonObject().get("Grade").getAsString();
					ParticipatingInCourse participate = new ParticipatingInCourse( course ,  studentID ,  grade);
					participate.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(participate , course , new CoursePrivateState());
					break;

				case "Unregister" :
					course = action.getAsJsonObject().get("Course").getAsString();
					studentID = action.getAsJsonObject().get("Student").getAsString();
					Unregister UnregisterStudent = new Unregister( studentID ,  course);
					UnregisterStudent.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(UnregisterStudent , course , new CoursePrivateState());
					break;


				case "Register With Preferences" :
					studentID = action.getAsJsonObject().get("Student").getAsString();
					preferences = getStringArray( action.getAsJsonObject().get("Preferences").getAsJsonArray());
					grades = getStringArray( action.getAsJsonObject().get("Grade").getAsJsonArray());
					RegisterWithPreferences regWithPre = new RegisterWithPreferences( studentID,  preferences , grades);
					regWithPre.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(regWithPre , studentID , new StudentPrivateState());
					break;

				case "Close Course" :
					department = action.getAsJsonObject().get("Department").getAsString();
					course = action.getAsJsonObject().get("Course").getAsString();
					CloseACourse closeCourse = new CloseACourse( course,  department);
					closeCourse.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(closeCourse , department , new DepartmentPrivateState());
					break;

				case  "Administrative Check" :
					students = getStringArray( action.getAsJsonObject().get("Students").getAsJsonArray());
					condition = getStringLinkedList( action.getAsJsonObject().get("Conditions").getAsJsonArray());
					computer = action.getAsJsonObject().get("Computer").getAsString();
					department = action.getAsJsonObject().get("Department").getAsString();
					CheckAdministrativeObligations AdminCheck = new CheckAdministrativeObligations(WH , students ,computer ,(LinkedList) condition);
					AdminCheck.getResult().subscribe(()-> C.countDown());
					actorThreadPool.submit(AdminCheck , department , new DepartmentPrivateState());
					break;

				default:
					break;
			}
		}
	}

	//"getStringArray" receive JsonArray and return String[] with name/grade/etc.
	public static String[] getStringArray(JsonArray A){
		String[] S = new String[A.size()];
		Iterator<JsonElement> myIter = A.iterator();
		int k = 0;
		while(myIter.hasNext()){
			S[k] = myIter.next().getAsString();
			k++;
		}
		return S;
	}

	public static List<String> getStringLinkedList(JsonArray A){
		List<String> S = new LinkedList<>();
		Iterator<JsonElement> myIter = A.iterator();
		while(myIter.hasNext()){
			S.add(myIter.next().getAsString());
		}
		return S;
	}

	//create WareHouse
	public static void createWareHouse(JsonArray computer){
		String type , SigSuccess , SigFail;
		HashMap<String,SuspendingMutex> Mutex = new HashMap<>();
		SuspendingMutex susMut;
		for(JsonElement comp :computer ){
			type = comp.getAsJsonObject().get("Type").getAsString();
			SigSuccess =  comp.getAsJsonObject().get("Sig Success").getAsString();
			SigFail =  comp.getAsJsonObject().get("Sig Fail").getAsString();
			Computer c = new Computer( type);
			c.setFailSig(Long.parseLong(SigFail));
			c.setSuccessSig(Long.parseLong(SigSuccess));
			susMut = new SuspendingMutex(c);
			Mutex.put(type , susMut);
		}
		WH = new Warehouse(Mutex);
	}
}



