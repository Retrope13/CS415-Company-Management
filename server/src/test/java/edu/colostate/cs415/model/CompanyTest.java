package edu.colostate.cs415.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Collections;

public class CompanyTest {
	
	private Project p1, p2;
	private Qualification q1, q2;
	private Worker w1, w2;
	private Set<Qualification> p1Qualifications, p2Qualifications; //Project qualifications
	private Set<Qualification> w1Qualifications, w2Qualifications; //Worker qualifications
	private Company c1, c2;
	
	@Before
	public void testCompanySetup(){
		p1Qualifications = new HashSet<Qualification>();
		p2Qualifications = new HashSet<Qualification>();
		w1Qualifications = new HashSet<Qualification>();
		w2Qualifications = new HashSet<Qualification>();
		c1 = new Company("companyName1");
		c2 = new Company("companyName2");
		
		q1 = c1.createQualification("testDescription1");
		q2 = c1.createQualification("testDescription2");
		p1Qualifications.add(q1);
		p1Qualifications.add(q2);
		p2Qualifications.add(q1);
		p2Qualifications.add(q2);
		w1Qualifications.add(q1);
		w2Qualifications.add(q2);
		
		p1 = c1.createProject("projectName1", p1Qualifications, ProjectSize.BIG);
		p2 = c1.createProject("projectName2", p2Qualifications, ProjectSize.MEDIUM);
		w1 = c1.createWorker("workerName1", w1Qualifications, 1);
		w2 = c1.createWorker("workerName2", w2Qualifications, 1);

		c1.assign(w1, p1);
		c1.assign(w2, p1);
		c1.assign(w1, p2);
		c1.assign(w2, p2);
	}

	@Test
	public void testConstructor(){
		assertNotNull(c1);	
		Company c2 = null;
		try {
			c2 = new Company(null);
			fail();
		} catch (NullPointerException e) {
			assertNull(c2);
		}
		try {
			c2 = new Company("\n\t");
			fail();
		} catch (IllegalArgumentException e) {
			assertNull(c2);
		}
	}

	@Test
	public void testHashCodeSameNames() {
		c2 = new Company("companyName1");
		assertEquals("Hash is not identical for same Company name", c1.hashCode(),  c2.hashCode());
	}

	@Test
	public void testHashCodeDifferentNames() {
		c2 = new Company("CompanyName");
		assertNotEquals("Hash is identical for different Company name", c1.hashCode(),  c2.hashCode());
	}
	
	@Test
	public void testGetUnassignedWorkers() {
		w2 = new Worker("WorkerName2", w2Qualifications, 1);
		//Worker w3 = new Worker("WorkerName2", qualifications, 1);
		//c1.assign(w1, p1);
		//assertTrue(c1.getUnassignedWorkers().contains(w2));
		//assertFalse(c1.getUnassignedWorkers().contains(w1));
		//assertTrue(c1.getUnassignedWorkers().contains(w2) && c1.getUnassignedWorkers().contains(w3));
		// TODO Can't implement until createWorker, createProject, and assign are done.
	}

	@Test
	public void testCompanyEqualsOverride() {
		c2 = new Company("companyName1");
		Company c3 = new Company("companyName2");
		Qualification q1 = new Qualification("TestQualification");
		assertTrue("Failed to show equality between Companies with the same name", c1.equals(c2));
		assertFalse("Failed to detect different Companies", c1.equals(c3));
		assertFalse("Failed to detect differences in objects", c1.equals(q1));
	}

	@Test
	public void testGetEmployedWorkers() {
		assertEquals("Failed to return an empty set of employees", 2, c1.getEmployedWorkers().size());
		// TODO add test for retruning non-empty employee set once createWorker() is implemented.
	}
	
	@Test
	public void testGetAssigned() {
		Worker w2 = new Worker("WorkerName2", w2Qualifications, 1);
		/*c1.assign(w1, p1);
		assertTrue(c1.getAssignedWorkers().contains(w1));
		assertFalse(c1.getAssignedWorkers().contains(w2));
		assertTrue(c1.getUnassignedWorkers().contains(w2));
		c1.assign(w2, p1);
		assertTrue(c1.getAssignedWorkers().contains(w1) && c1.getAssignedWorkers().contains(w2));
		assertTrue(c1.getUnassignedWorkers().isEmpty());
		c1.unassign(w2, p1);
		assertTrue(c1.getAssignedWorkers().contains(w1));
		assertFalse(c1.getAssignedWorkers().contains(w2));
		assertTrue(c1.getUnassignedWorkers().contains(w2));
		TODO Can't implement until createWorker, createProject, and assign and unassign are done.*/
	}

	@Test
	public void testGetProjects() {
		assertEquals("Failed to return empty Projects set", 2, c1.getProjects().size());
		// TODO add test for non-empty project set after createProjects() is implemented.
	}
		
	@Test
	public void testCreateGetQualifications() {
		c1.createQualification("testDescription");
		assertTrue(c1.getQualifications().contains(q1));
	}

	@Test
	public void testToString() {
		assertEquals("Failed to accuratley display the Company", "companyName1:2:2", c1.toString());
	}

	@Test
	public void testStatusFinish() {
		p2 = null;
		try {
			c1.finish(p2);
		} catch(IllegalArgumentException e) {
			assertNull(p2);
		}
		p2 = c1.createProject("projectName2", p2Qualifications, ProjectSize.SMALL);
		w1.removeProject(p2);
		p1.setStatus(ProjectStatus.ACTIVE);
		c1.finish(p1); // Since there is only one project now we are also covering remove from assigned
		assertEquals("A project that was finished does not have a finished status", p1.getStatus(), ProjectStatus.FINISHED);
		assertFalse("The project was finished, but a worker without projects wasn't removed from assigned", c1.getAssignedWorkers().contains(w1));
	}

	@Test
	public void testFinishAvailableAdd() {
		w1.removeProject(p2); // remove p2 so we can add it back with a larger size
		p2 = c1.createProject("projectName2", p2Qualifications, ProjectSize.BIG);
		Project p3 = c1.createProject("projectName3", p2Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p2Qualifications, ProjectSize.BIG);

		c1.assign(w1, p2);
		c1.assign(w1, p3);
		c1.assign(w1, p4);

		p4.setStatus(ProjectStatus.ACTIVE);
		c1.finish(p4);
		assertTrue("A project was finished and an unavailable worker was not added back to available", c1.getAvailableWorkers().contains(w1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullStart() {
		c1.start(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStartFinished() {
		c1.start(p1);
		p1.setStatus(ProjectStatus.FINISHED);
		assertTrue(p1.getMissingQualifications().isEmpty() && p1.getStatus() == ProjectStatus.FINISHED);
		c1.start(p1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStartFinishedandMissingQuals() {
		c1.start(p1);
		c1.unassign(w2, p1);
		p1.setStatus(ProjectStatus.FINISHED);
		assertTrue(!p1.getMissingQualifications().isEmpty() && p1.getStatus() == ProjectStatus.FINISHED);
		c1.start(p1);
	}
	
	@Test
	public void testStartMissingQuals() {
		c1.unassign(w2, p1);
		assertFalse(p1.getMissingQualifications().isEmpty());
		try{
			c1.start(p1);
		}catch(IllegalArgumentException e){
			assertNotNull(e);
		}
		assertFalse("Project with missing qualifications should not be set to active.", p1.getStatus() == ProjectStatus.ACTIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignWorkerNotEmployeed() {
		Worker w3 = new Worker("workerName3", p1Qualifications, 1);
		c1.assign(w3, p1);
	}
	
	@Test
	public void testCreateProjectNull() {
		Set<Qualification> p7Qualifications = new HashSet<Qualification>();
		Set<Qualification> p8Qualifications = new HashSet<Qualification>();
		Qualification q8 = new Qualification("absentDescription");
		p8Qualifications.add(q8);
		Project p3 = c1.createProject(null, p1Qualifications, ProjectSize.BIG);
		assertNull(p3);
		Project p4 = c1.createProject("projectName4", null, ProjectSize.BIG);
		assertNull(p4);
		Project p5 = c1.createProject("projectName5", p1Qualifications, null);
		assertNull(p5);
		Project p6 = c1.createProject("", p1Qualifications, ProjectSize.BIG);
		assertNull(p6);
		Project p7 = c1.createProject("projectName7", p7Qualifications, ProjectSize.BIG);
		assertNull(p7);
		Project p8 = c1.createProject("projectName8", p8Qualifications, ProjectSize.BIG);
		assertNull(p8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignNullWorker() {
		c1.assign(null, p1);
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void testAssignNullProject() {
		c1.assign(w1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignNullWorkerAndProject() {
		c1.assign(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignProjectAndWorkerSetsMismatched() {
		Worker w3 = c1.createWorker("workerName3", p1Qualifications, 1);
		Project p1 = c1.createProject("projectName1", p1Qualifications, ProjectSize.BIG);
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.BIG);
		c1.assign(w3, p1);
		c1.assign(w3, p3);
		c1.assign(w3, p4);
		w3.removeProject(p1);
		c1.assign(w3, p1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignWorkerIsUnavailable() {
		Worker w3 = c1.createWorker("workerName3", p1Qualifications, 1);
		assertTrue(c1.getAvailableWorkers().contains(w3));
		w3.addProject(p1);
		assertTrue(w3.getProjects().contains(p1));
		c1.assign(w3, p1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignProjectAlreadyHasWorker() {
		Worker w3 = c1.createWorker("workerName3", p1Qualifications, 1);
		Project p1 = c1.createProject("projectName1", p1Qualifications, ProjectSize.BIG);
		Project p2 = c1.createProject("projectName2", p1Qualifications, ProjectSize.BIG);
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.BIG);
		Project p5 = c1.createProject("projectName5", p1Qualifications, ProjectSize.BIG);
		
		assertTrue(c1.getAvailableWorkers().contains(w3));
		c1.assign(w3, p1);
		c1.assign(w3, p2);
		c1.assign(w3, p3);
		c1.assign(w3, p4);
		assertFalse(c1.getAvailableWorkers().contains(w3));
		c1.assign(w3, p5);
	}

	@Test
	public void testAssignToFinishedProject() {
		p1.setStatus(ProjectStatus.FINISHED);
		c1.unassign(w1, p1);
		c1.assign(w1, p1);
	}

	@Test
	public void testAssignProjectWillOverload() {
		Qualification q3 = c1.createQualification("testDescription3");
		Set<Qualification> p3Qualifications = new HashSet<Qualification>();
		p3Qualifications.add(q3);
		Worker w3 = c1.createWorker("workerName3", p3Qualifications, 1);
		Project p2 = c1.createProject("projectName2", p3Qualifications, ProjectSize.BIG);
		Project p3 = c1.createProject("projectName3", p3Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p3Qualifications, ProjectSize.BIG);
		Project p5 = c1.createProject("projectName5", p3Qualifications, ProjectSize.MEDIUM);
		Project p6 = c1.createProject("projectName6", p3Qualifications, ProjectSize.BIG);
		c1.assign(w3, p2);
		c1.assign(w3, p3);
		c1.assign(w3, p4);
		c1.assign(w3, p5);
		assertTrue(w3.willOverload(p6));
		c1.assign(w3, p6);
	}

	@Test
	public void testAssignToActiveProject() {
		Qualification q3 = c1.createQualification("testDescription3");
		Set<Qualification> p3Qualifications = new HashSet<Qualification>();
		p3Qualifications.add(q3);
		Worker w3 = c1.createWorker("workerName3", p3Qualifications, 1);
		Project p5 = c1.createProject("projectName5", p3Qualifications, ProjectSize.SMALL);
		p5.setStatus(ProjectStatus.ACTIVE);
		c1.assign(w3, p5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAssignWithForeignProject() {
		Project p3 = new Project("project3", p1Qualifications, ProjectSize.SMALL);
		c1.assign(w1, p3);
	}

	@Test
	public void baseChoiceCoverageT1() {
		assertFalse(c1.equals(c2));
		assertEquals(c1.toString(), "companyName1:2:2");
		assertEquals(c1.getName(), "companyName1");
		assertTrue("Created workers are not present in employed set", c1.getEmployedWorkers().contains(w1) && c1.getEmployedWorkers().contains(w2));
		assertTrue("Newly created workers are not in available set", c1.getAvailableWorkers().contains(w1) && c1.getAvailableWorkers().contains(w2));
		assertTrue("Workers assigned to a project were not found in assigned set", c1.getAssignedWorkers().contains(w1) && c1.getAssignedWorkers().contains(w2));
		assertTrue("When all workers have been assigned there remains a worker unassigned", c1.getUnassignedWorkers().isEmpty());

		assertEquals(p1.getStatus(), ProjectStatus.PLANNED);
		c1.start(p1);
		c1.start(p2);
		assertEquals(p1.getStatus(), ProjectStatus.ACTIVE);
		c1.unassign(w1, p1);
		assertEquals(p1.getStatus(), ProjectStatus.SUSPENDED);
		c1.finish(p1);
		assertEquals(p1.getStatus(), ProjectStatus.SUSPENDED);
		c1.assign(w1, p1);
		assertEquals(p1.getStatus(), ProjectStatus.SUSPENDED);
		c1.start(p1);
		assertEquals(p1.getStatus(), ProjectStatus.ACTIVE);
		c1.finish(p1);
		assertEquals(p1.getStatus(), ProjectStatus.FINISHED);
		
		assertTrue(c1.getAssignedWorkers().contains(w1) && c1.getAssignedWorkers().contains(w2));
		c1.unassignAll(w1);
		assertEquals(p2.getStatus(), ProjectStatus.SUSPENDED);
		assertTrue(c1.getAssignedWorkers().contains(w2));
		assertFalse(c1.getAssignedWorkers().contains(w1));
	}	

	@Test
	public void baseChoiceCoverageT2() {
		assertEquals(c1.createWorker("", w1Qualifications, 1), null);
	}

	@Test
	public void baseChoiceCoverageT3() {
		assertEquals(c1.createWorker(null, w1Qualifications, 1), null);
	}

	@Test
	public void baseChoiceCoverageT4() {
		q1 = c2.createQualification("testDescription1");
		q2 = c2.createQualification("testDescription2");
		p1 = c2.createProject("projectName1", p1Qualifications, ProjectSize.BIG);
		p2 = c2.createProject("projectName2", p2Qualifications, ProjectSize.MEDIUM);
		
		assertTrue(c2.getEmployedWorkers().isEmpty());
		assertTrue(c2.getUnavailableWorkers().isEmpty());
		assertTrue(c2.getAvailableWorkers().isEmpty());
		assertTrue(c2.getAssignedWorkers().isEmpty());
		assertTrue(c2.getUnassignedWorkers().isEmpty());
	}

	@Test
	public void baseChoiceCoverageT5() {
		q1 = c2.createQualification("testDescription1");
		q2 = c2.createQualification("testDescription2");
		p2Qualifications = new HashSet<Qualification>();
		p2Qualifications.add(q1);
		p1 = c2.createProject("projectName1", p1Qualifications, ProjectSize.BIG);
		p2 = c2.createProject("projectName2", p2Qualifications, ProjectSize.MEDIUM);
		w1 = c2.createWorker("workerName1", w1Qualifications, 1);
		
		assertTrue(c2.getEmployedWorkers().size() == 1);
		assertTrue(c2.getUnavailableWorkers().isEmpty());
		assertTrue(c2.getAvailableWorkers().size() == 1);
		assertTrue(c2.getAssignedWorkers().isEmpty());
		assertTrue(c2.getUnassignedWorkers().size() == 1);
		
		c2.assign(w1, p1);
		c2.assign(w1, p2);
		c2.start(p2);
		assertTrue(p1.getStatus() == ProjectStatus.PLANNED);
		assertTrue(p2.getStatus() == ProjectStatus.ACTIVE);
	}

	@Test
	public void baseChoiceCoverageT6() {
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p2Qualifications, ProjectSize.MEDIUM);
		Project p5 = c1.createProject("projectName5", p2Qualifications, ProjectSize.MEDIUM);
		Project p6 = c1.createProject("projectName6", p2Qualifications, ProjectSize.SMALL);
		c1.assign(w1, p3);
		c1.assign(w1, p4);
		c1.assign(w1, p5);
		c1.assign(w2, p3);
		c1.assign(w2, p4);
		c1.assign(w2, p5);
		assertTrue(c1.getAvailableWorkers().isEmpty());
		assertTrue(c1.getUnavailableWorkers().contains(w1) && c1.getUnavailableWorkers().contains(w2));
		assertFalse(p6.getWorkers().contains(w1));
		assertFalse(p6.getWorkers().contains(w2));
	}

	@Test
	public void baseChoiceCoverageT7() {
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p2Qualifications, ProjectSize.MEDIUM);
		Project p5 = c1.createProject("projectName5", p2Qualifications, ProjectSize.MEDIUM);
		c1.assign(w1, p3);
		c1.assign(w1, p4);
		c1.assign(w1, p5);
		assertTrue(c1.getAvailableWorkers().size() == 1 && c1.getAvailableWorkers().contains(w2) && !c1.getAvailableWorkers().contains(w1));
		assertTrue(c1.getUnavailableWorkers().size() == 1 && c1.getUnavailableWorkers().contains(w1) && !c1.getUnavailableWorkers().contains(w2));
	}
	
	@Test
	public void baseChoiceCoverageT8(){ //available set is null
		try {
			Field companyAvailableSet = c1.getClass().getDeclaredField("available");
			companyAvailableSet.setAccessible(true);
			companyAvailableSet.set(c1, null);
			assertNull(c1.getAvailableWorkers()); //getAvailableWorkers should return null if set is null
		} catch (NoSuchFieldException e) {
			fail();
		} catch (SecurityException e) {
			fail();
		} catch (IllegalAccessException e){
			fail();
		}
	}

	@Test
	public void baseChoiceCoverageT9(){
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.SMALL);
		c1.assign(w1, p3);
		c1.assign(w1, p4);
		assertEquals("w1 doesn't have a workload of 9", 9, w1.getWorkload());
		assertTrue("w1 is not available", w1.isAvailable());
		assertTrue("w1 is not found in available worker set", c1.getAvailableWorkers().contains(w1));
	}

	@Test
	public void baseChoiceCoverageT10(){
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.MEDIUM);
		c1.assign(w1, p3);
		c1.assign(w1, p4);
		assertEquals("w1 doesn't have a workload of 10", 10, w1.getWorkload());
		assertTrue("w1 is not available", w1.isAvailable());
		assertTrue("w1 is not found in available worker set", c1.getAvailableWorkers().contains(w1));
	}

	@Test
	public void baseChoiceCoverageT11(){
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.BIG);
		c1.assign(w1, p3);
		c1.assign(w1, p4);
		assertEquals("w1 doesn't have a workload of 11", 11, w1.getWorkload());
		assertTrue("w1 is not available", w1.isAvailable());
		assertTrue("w1 is not found in available worker set", c1.getAvailableWorkers().contains(w1));
	}

	@Test
	public void baseChoiceCoverageT12(){
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.BIG);
		Project p5 = c1.createProject("projectName5", p1Qualifications, ProjectSize.SMALL);
		c1.assign(w1, p3);
		c1.assign(w1, p4);
		c1.assign(w1, p5);
		assertEquals("w1 doesn't have a workload of 12", 12, w1.getWorkload());
		assertFalse("w1 is available", w1.isAvailable());
		assertFalse("w1 is found in available worker set", c1.getAvailableWorkers().contains(w1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void baseChoiceCoverageT13(){ //avilable worker is not found in employees set
		try {
			Field companyAvailableSet = c1.getClass().getDeclaredField("available");
			companyAvailableSet.setAccessible(true);

			Worker w3 = new Worker("workerName3", p1Qualifications, 1);
			Set<Worker> testAvailable = new HashSet<Worker>();
			testAvailable.add(w1);
			testAvailable.add(w2);
			testAvailable.add(w3);
			
			companyAvailableSet.set(c1, testAvailable);

			c1.assign(w3, p1);
			fail(); //When trying to assign a worker, the worker should appear in employee set.
		} catch (NoSuchFieldException e) {
			fail();
		} catch (SecurityException e) {
			fail();
		} catch (IllegalAccessException e){
			fail();
		}
	}

	/*@Test //TODO: Review this partition.
	public void baseChoiceCoverageT14(){ //When assigned, worker doesn't appear in project's worker set. Illegal state, should cause failures.
		assertTrue("Project p1 doesn't contain w1 after being assigned.", c1.getAssignedWorkers().contains(w1));
		p1.getWorkers().remove(w1);
		try {
			c1.unassign(w1, p1);
			fail();
		} catch (NullPointerException e) {
			// If an assigned worker is not found in the corresponding project worker set, using the unassign methods should throw exceptions.
		}
	}*/

	@Test(expected = IllegalArgumentException.class)
	public void baseChoiceCoverageT15() {
		Worker w3 = new Worker("workerName3", p1Qualifications, 1);;
		c1.assign(w3, p1);
	}

	@Test
	public void baseChoiceCoverageT16() {
		// unassign all workers from projects
		c1.unassign(w1, p1);
		c1.unassign(w2, p1);
		c1.unassign(w1, p2);
		c1.unassign(w2, p2);
		assertTrue(c1.getAssignedWorkers().isEmpty());

		// check that workers can be assigned to projects when assigned set is empty
		c1.assign(w1, p1);
		assertTrue(c1.getAssignedWorkers().contains(w1));
	}

	@Test
	public void baseChoiceCoverageT17() {
		// unassign all but one worker from projects so assigned set contains 1 worker
		c1.unassign(w2, p1);
		c1.unassign(w1, p2);
		c1.unassign(w2, p2);
		assertTrue(c1.getAssignedWorkers().size() == 1);

		// check that workers can be assigned to projects when assigned set has one element
		c1.assign(w2, p1);
		assertTrue(c1.getAssignedWorkers().contains(w2));
	}

	@Test
	public void baseChoiceCoverageT18() {
		Company c3 = new Company("companyName3");
		Qualification q3 = c3.createQualification("testDescription1");
		Qualification q4 = c3.createQualification("testDescription2");
		assertTrue(c3.getProjects().isEmpty());
		Project p3 = c3.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		
		// check that projects can be added to the Company's project set if the project set is empty
		assertTrue(c3.getProjects().contains(p3));
	}
	@Test
	public void baseChoiceCoverageT19() {
		Company c3 = new Company("companyName3");
		Qualification q3 = c3.createQualification("testDescription1");
		Qualification q4 = c3.createQualification("testDescription2");
		Project p3 = c3.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c3.createProject("projectName4", p2Qualifications, ProjectSize.BIG);
		assertTrue(c3.getProjects().contains(p3) && c3.getProjects().contains(p4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void baseChoiceCoverageT20() {
		p1.setStatus(ProjectStatus.ACTIVE);
		assertTrue(p1.getStatus() == ProjectStatus.ACTIVE);
		c1.start(p1);
	}

	@Test
	public void baseChoiceCoverageT21() {
		p1.setStatus(ProjectStatus.SUSPENDED);
		assertTrue(p1.getStatus() == ProjectStatus.SUSPENDED);
		c1.start(p1);
		assertTrue(p1.getStatus() == ProjectStatus.ACTIVE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void baseChoiceCoverageT22() {
		c1.start(p1);
		assertTrue(p1.getStatus() == ProjectStatus.ACTIVE);
		c1.finish(p1);
		assertTrue(p1.getStatus() == ProjectStatus.FINISHED);
		c1.start(p1);
	}

	@Test
	public void baseChoiceCoverageT23() {
		c1 = new Company("companyName1");
		assertTrue(c1.getQualifications().isEmpty());
	}

	@Test
	public void baseChoiceCoverageT24() {
		c1 = new Company("companyName1");
		c1.createQualification("testDescription1");
		assertEquals("Company with one created qualification does not have qualification set of size 1", c1.getQualifications().size(), 1);
	}

	@Test
	public void baseChoiceCoverageT25() {
		try {
			Field companyQualificationsSet = c1.getClass().getDeclaredField("qualifications");
			companyQualificationsSet.setAccessible(true);
			companyQualificationsSet.set(c1, null);
			assertNull(c1.getQualifications()); //getQualifications should return null if set is null
		} catch (NoSuchFieldException e) {
			fail();
		} catch (SecurityException e) {
			fail();
		} catch (IllegalAccessException e){
			fail();
		}
	}

	@Test
	public void baseChoiceCoverageT26() {
		c1 = new Company("companyName1");
		Set<Qualification> unqualifiedSet = new HashSet<Qualification>();
		Qualification diffQ1 = new Qualification("diffTestDiscription1");
		unqualifiedSet.add(diffQ1);
		Worker w1 = new Worker("workerName1", unqualifiedSet, 1);
		assertTrue("Expected unqualified worker was qualified", Collections.disjoint(c1.getQualifications(), w1.getQualifications()));//makes sure the worker is unqualified
		assertNull("Worker is unqualified but still employed", c1.createWorker("workerName1", unqualifiedSet, 1));//checks if the unqualified worker was employed
	}

	@Test
	public void baseChoiceCoverageT27() {
		Set<Qualification> validQualSet = new HashSet<>();
		Qualification validQualification = new Qualification("testDescription1");
		validQualSet.add(validQualification);

		Project p1 = new Project("testDescription1", validQualSet, ProjectSize.SMALL);
		assertFalse("New project object made directly but shows in projects set", c1.getProjects().contains(p1));
	}

	@Test
	public void baseChoiceCoverageT28() {
		try {
			assertNull(c1.createQualification(null));
			assertNull(c1.createQualification(""));
		} catch(NullPointerException e) {
			
		} catch(IllegalArgumentException e) {

		}
	}

	@Test
	public void testUnassignNull() {
		p1 = null;
		w1 = null;
		try {
			c1.unassign(w1, p1);
		} catch (NullPointerException e) {

		}
		p1 = p2;
		try {
			c1.unassign(w1, p1);
		} catch(NullPointerException e) {

		}
	}

	@Test
	public void testUnassignProjectNotinWorker() {
		Worker w3 = new Worker("workerName3", p1Qualifications, 1);
		p1.addWorker(w3);
		assertFalse("Worker not given a project has project in set", w3.getProjects().contains(p1));
		c1.unassign(w3, p1);
	}

	@Test
	public void testUnassignAddToAvailable() {
		Worker w3 = c1.createWorker("workerName3", p1Qualifications, 1);
		Project p1 = c1.createProject("projectName1", p1Qualifications, ProjectSize.BIG);
		Project p2 = c1.createProject("projectName2", p1Qualifications, ProjectSize.BIG);
		Project p3 = c1.createProject("projectName3", p1Qualifications, ProjectSize.BIG);
		Project p4 = c1.createProject("projectName4", p1Qualifications, ProjectSize.BIG);

		c1.assign(w3, p1);
		c1.assign(w3, p2);
		c1.assign(w3, p3);
		c1.assign(w3, p4);

		assertFalse(c1.getAvailableWorkers().contains(w3));
		c1.unassign(w3, p4);
		assertTrue(c1.getAvailableWorkers().contains(w3));

	}

	@Test
	public void testUnassignSuspend() {
		Set<Qualification> emptyP1Quals = new HashSet<Qualification>();
		try {
			Field p1QualificationSet = p1.getClass().getDeclaredField("qualifications");
			p1QualificationSet.setAccessible(true);
			p1QualificationSet.set(p1, emptyP1Quals);
			c1.unassign(w1, p1);
			assertTrue(p1.getMissingQualifications().isEmpty());
		} catch (NoSuchFieldException e) {
			fail();
		} catch (SecurityException e) {
			fail();
		} catch (IllegalAccessException e){
			fail();
		}
	}
  
  @Test
	public void createWorkerInvalidSalary() {
		Worker w3 = c1.createWorker("workerName3", p1Qualifications, 0);
		assertNull(w3);
	}

	@Test
	public void createWorkerEmptyQualifications() {
		Set<Qualification> emptyQualifications = new HashSet<Qualification>();
		Worker w3 = c1.createWorker("workerName3", emptyQualifications, 1);
		assertNull(w3);
	}

	@Test(expected = Exception.class)
	public void testUnassignAllNullWorker() {
		w1 = null;
		c1.unassignAll(w1);
	}

	@Test
	public void testUnassignAllNoWorker() {
		Worker w3 = c1.createWorker("workerName3", w2Qualifications, 1);
		w3.addProject(p1);
		c1.unassignAll(w3);
		assertFalse(p1.getWorkers().contains(w3));
	}

	@Test
	public void testUnassignAllAddtoAvailable() {
		Worker w3 = c1.createWorker("fds", w1Qualifications, 1);
		Project p1 = c1.createProject("fdsa", w1Qualifications, ProjectSize.BIG);
		Set<Worker> emptyAvailable = new HashSet<Worker>();
		c1.assign(w3, p1);
		try {
			Field c1AvailableSet = c1.getClass().getDeclaredField("available");
			c1AvailableSet.setAccessible(true);
			c1AvailableSet.set(c1, emptyAvailable);
			c1.unassignAll(w3);
		} catch (NoSuchFieldException e) {
			fail();
		} catch (SecurityException e) {
			fail();
		} catch (IllegalAccessException e){
			fail();
		}

	}


}
