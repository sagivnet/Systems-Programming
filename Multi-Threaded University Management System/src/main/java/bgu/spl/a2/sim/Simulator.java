/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.*;
import java.util.HashMap;

import java.util.concurrent.CountDownLatch;


import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.Gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator

{
	//Fields
	private static ActorThreadPool actorThreadPool;
	public static Warehouse warehouse;
	private static UniversityObj university;
	public static CountDownLatch latch;
	
	//Methods
	//------------------------------------------------------------------------------------------------------------------
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
 
	//------------------------------------------------------------------------------------------------------------------
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool)
	{
		actorThreadPool = myActorThreadPool;
	}
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String,PrivateState> end()
	{
		try {
			actorThreadPool.shutdown();
		}
		catch (InterruptedException e){}

		return new HashMap<> (actorThreadPool.getStatesMap());

	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	
	private static Action buildAction (ActionObj actionObj)
	{
		Action returnAction = null;
		
		if (actionObj.Action.equals("Add Student"))
			returnAction = new AddStudentAction (actionObj.Student);
		
		else if (actionObj.Action.equals("Administrative Check"))
			returnAction = new CheckAdministrativeObligationsAction
					(actionObj.Students,actionObj.Computer,actionObj.Conditions,warehouse);
		
		else if (actionObj.Action.equals("Close Course"))
			returnAction = new CloseACourseAction (actionObj.Course);
		
		else if (actionObj.Action.equals("Open Course"))
			returnAction = new OpenCourseAction
					(actionObj.Course, actionObj.Space, actionObj.Prerequisites);
		
		else if (actionObj.Action.equals("Add Spaces"))
			returnAction= new addSpaceToCourseAction
					(actionObj.Number);
		
		else if (actionObj.Action.equals("Participate In Course"))
			returnAction = new ParticipatingInCourseAction (actionObj.Student,actionObj.Grade.get(0));
		
		else if (actionObj.Action.equals("Unregister"))
			returnAction = new UnregisterAction (actionObj.Student);
		
		else if (actionObj.Action.equals("Register With Preferences"))
			returnAction = new PreferencesListAction(actionObj.Preferences,actionObj.Grade);
		
		
		returnAction.getResult ().subscribe (()->
		{
			Simulator.latch.countDown();
		});
		return returnAction;
	}

	private static OurPair whichActor(ActionObj action)
	{
		//	Actions for Department
		
		if (
			action.Action.equals("Close Course") ||
			action.Action.equals("End Registeration") ||
			action.Action.equals("Open Course")	||
			action.Action.equals("Add Student") ||
			action.Action.equals("Administrative Check"))
			
			return new OurPair (action.Department,new DepartmentPrivateState ());
		
		//	Actions for Course
		
		if (action.Action.equals("Add Spaces") ||
			action.Action.equals("Participate In Course") ||
			action.Action.equals("Unregister"))

			return new OurPair (action.Course,new CoursePrivateState ());

		//	Actions for Student

		if (action.Action.equals("Register With Preferences"))

			return new OurPair (action.Student,new StudentPrivateState());

		
		return null;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	public static void main(String [] args)
	{
		try {
			/**Input*/
			FileReader fileReader = new FileReader(args[0]);


			JsonReader jsonReader = new JsonReader(fileReader);
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonVal = jsonParser.parse(jsonReader).getAsJsonObject ();
			
			Gson gson = new Gson();
			university = gson.fromJson (jsonVal,UniversityObj.class);
			
			jsonReader.close ();
			fileReader.close ();
			
		}catch (FileNotFoundException e){System.out.println ("Input file has not found");}
		catch (IOException e){}
		
		
		//Actor Thread Pool creation
		ActorThreadPool pool = new ActorThreadPool (university.threads);
		attachActorThreadPool (pool);
		
		//Warehouse creation
		warehouse = new Warehouse (university.Computers);
		
		start();
		Serializable result = end();
		
		
		/**Output*/
		FileOutputStream FileOut = null;
		ObjectOutputStream objectOut = null;
		try
		{
			FileOut = new FileOutputStream("result.ser");
			objectOut= new ObjectOutputStream(FileOut);
			objectOut.writeObject(result);
		}
		
		catch (FileNotFoundException e) 		{System.out.println ("FileNotFound Exception when trying to write object");}
		
		catch (IOException e) 			{System.out.println ("IO Exception when trying to write object");}
		
		try
		{
			objectOut.close ();
			FileOut.close ();
		}
		catch (IOException e)				{System.out.println ("IO Exception when trying to close streams");}
		

	}
	
	//------------------------------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------------------------------
	public static void start()
	{
		int actionCOUNT=0;
		
		latch = new CountDownLatch(university.Phase1.size());
		//Phase 1 actions' submission
		for (ActionObj actionObj : university.Phase1)
		{
			//Reading data
			OurPair actor = whichActor (actionObj);
			Action action = buildAction (actionObj);

			
			//submit action
			actorThreadPool.submit (action, actor.getName (), actor.getState ());
		}

		
		actorThreadPool.start();
		
		try{ latch.await();}
		catch (InterruptedException e){}
		
		
		latch = new CountDownLatch(university.Phase2.size());

		
		//Phase 2 actions' submission
		for (ActionObj actionObj : university.Phase2)
		{
			//Reading data
			OurPair actor = whichActor (actionObj);
			Action action = buildAction (actionObj);

			
			//submit action
			actorThreadPool.submit (action, actor.getName (), actor.getState ());
		}
		try{ latch.await();}
		catch (InterruptedException e){}
		
		
		
		latch = new CountDownLatch(university.Phase3.size());
		

		
		
		
		//Phase 3 actions' submission
		for (ActionObj actionObj : university.Phase3)
		{
			//Reading data
			OurPair actor = whichActor (actionObj);
			Action action = buildAction (actionObj);


			//submit action
			actorThreadPool.submit (action, actor.getName (), actor.getState ());
		}
		
		try{ latch.await();}
		catch (InterruptedException e){}
		
	}
}
