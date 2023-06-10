package edu.colostate.cs415.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.colostate.cs415.dto.WorkerDTO;

public class WorkerTest {

	private Worker w1;
	private Qualification q1;
	private Set<Qualification> qualifications;
	private Project p1;
	private Set<Project> projects;
	private static final int MAX_WORKLOAD = 12;

	@Before
	public void workerTestSetup(){
		qualifications = new HashSet<Qualification>();
		q1 = new Qualification("testDescription");
		qualifications.add(q1);
		w1 = new Worker("workerName", qualifications, 1);
		w1.addQualification(q1);
		projects = new HashSet<Project>();
		p1 = new Project("projectName", qualifications, ProjectSize.SMALL);
		p1.addQualification(q1);
	}
	
	@Test
	public void testConstructor(){
		assertEquals(w1.getName(), "workerName");
		assertEquals(w1.getQualifications(), qualifications);
		assertEquals(w1.getSalary(), 1, 0);
		assertEquals(0, w1.getProjects().size());
		w1.addProject(p1);
		assertEquals(1, w1.getProjects().size());
	}

	@Test(expected = NullPointerException.class)
	public void testNullValuesConstructor() {
		Worker w1 = new Worker(null, null, 1);
		Worker w2 = new Worker("workerName", null, 1);
		Set<Qualification> qualifications = new HashSet<Qualification>();
		Worker w3 = new Worker(null, qualifications, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testNullQualInCtor() {
		Worker w1 = new Worker("workerName", null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentsConstructor() {
		Set<Qualification> qualifications = new HashSet<Qualification>();
		Worker w1 = new Worker("", qualifications, 1);
		Worker w2 = new Worker("", qualifications, 0);
		Worker w3 = new Worker("", qualifications, -1);
		Worker w4 = new Worker("workerName", qualifications, 0);
		Worker w5 = new Worker("workerName", qualifications, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalSalaryInCtor() {
		Worker w1 = new Worker("workerName", qualifications, 0);
		Worker w2 = new Worker("workerName2", qualifications, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyQualificationsInCtor() {
		Set<Qualification> qualifications2 = new HashSet<Qualification>();
		Worker w2 = new Worker("workerName2", qualifications2, 1);
	}

	@Test
	public void testWorkerEqualsOverride() {
		Worker w2 = new Worker("workerName", qualifications, 1);
		Worker w3 = new Worker("workerName2", qualifications, 1);
		Qualification q1 = new Qualification("TestQualification");
		assertTrue("Failed to show equality between workers", w1.equals(w2));
		assertFalse("Failed to detect different workers", w1.equals(w3));
		assertFalse("Failed to detect differences in objects", w1.equals(q1));
	}
	
	@Test
	public void testWorkerEqualsNull() {
		assertFalse(w1.equals(null));
	}

	@Test
	public void testGetName() {
		Set<Qualification> qualifications = new HashSet<Qualification>();
		qualifications.add(q1);
		Worker w2 = new Worker("workerName", qualifications, 1);
		w2.addQualification(q1);
		assertEquals("Failed to return worker name", "workerName", w2.getName());
	}

	@Test
	public void testHashCodeDifferentNames(){
		Set<Qualification> qualifications = new HashSet<Qualification>();
		qualifications.add(q1);
		Worker w2 = new Worker("workerName2", qualifications, 5000.0);
		w2.addQualification(q1);
		assertNotEquals("Hash is identical for different Worker names", w1.hashCode(), w2.hashCode());
	}

	@Test
	public void testHashCodeSameNames(){
		Set<Qualification> qualifications = new HashSet<Qualification>();
		qualifications.add(q1);
		Worker w2 = new Worker("workerName", qualifications, 5000.0);
		w2.addQualification(q1);
		assertEquals("Hash is not identical for the same Worker name", w1.hashCode(), w2.hashCode());
	}
	
	@Test(expected = NullPointerException.class)
	public void testHashCodeNullName(){
		Set<Qualification> qualifications = new HashSet<Qualification>();
		Worker w2 = new Worker(null, qualifications, 1);
	}

	@Test
	public void testToString() {
		Project p1 = new Project("TestProj", qualifications, ProjectSize.SMALL);
		Qualification q1 = new Qualification("Test");
		p1.addQualification(q1);
		Qualification q2 = new Qualification("Test2");
		qualifications.add(q1);
		qualifications.add(q2);
		Worker w3 = new Worker("workerName", qualifications, 10000.20);
		w3.addQualification(q1);
		w3.addQualification(q2);
		assertEquals("workerName:0:3:10000", w3.toString());
		w3.addProject(p1);
		assertEquals("workerName:1:3:10000", w3.toString());
  }

  @Test
	public void testSetSalary() {
		w1.setSalary(1.0);
		assertEquals(1.0, w1.getSalary(), 0.00);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testIllegalArgumentSetSalary() {
		Worker w2 = new Worker("workerName", qualifications, 1);
		w2.addQualification(q1);
		w2.setSalary(-1);
		w2.setSalary(0);
	}
  
  @Test
  public void testWillOverload() {
	Project p1 = new Project("TestProj1", qualifications, ProjectSize.BIG);
	Project p2 = new Project("TestProj2", qualifications, ProjectSize.BIG);
	Project p3 = new Project("TestProj3", qualifications, ProjectSize.BIG);
	Project p4 = new Project("TestProj4", qualifications, ProjectSize.BIG);
	Project p5 = new Project("TestProj5", qualifications, ProjectSize.SMALL);
	Worker w2 = new Worker("workerName", qualifications, 1);
	w2.addQualification(q1);
	w2.addProject(p1);
	w2.addProject(p2);
	w2.addProject(p3);
	assertEquals(w2.willOverload(p4), false);
	w2.addProject(p4);
	assertEquals(w2.willOverload(p5), true);
  }

  @Test
  public void testWillOverloadSameProject() {
	Project p1 = new Project("TestProj1", qualifications, ProjectSize.BIG);
	Project p2 = new Project("TestProj2", qualifications, ProjectSize.BIG);
	Project p3 = new Project("TestProj3", qualifications, ProjectSize.BIG);
	Project p4 = new Project("TestProj4", qualifications, ProjectSize.BIG);
	Worker w2 = new Worker("workerName", qualifications, 1);
	w2.addQualification(q1);
	w2.addProject(p1);
	w2.addProject(p2);
	w2.addProject(p3);
	w2.addProject(p4);
	w2.addProject(p4); // duplicate addProject call to make sure willOverload catches same project
	assertEquals(w2.willOverload(p4), false);
  }

  @Test
  public void testNullWillOverload() {
	assertFalse("Processed null parameter on willOverload() call", w1.willOverload(null));
  }
  
  @Test
  public void testGetProjects() {   
	Project p1 = new Project("TestProj1", qualifications, ProjectSize.SMALL);
	Project p2 = new Project("TestProj2", qualifications, ProjectSize.SMALL);
	Worker w2 = new Worker("workerName", qualifications, 1);
	w2.addQualification(q1);
	w2.addProject(p1);
	w2.addProject(p2);
	Set<Project> projects= new HashSet<Project>();
	projects.add(p1);
	projects.add(p2);
	assertEquals(projects, w2.getProjects());
  }
  
  @Test
  public void testGetSalary() {
	  Set<Qualification> qualifications = new HashSet<Qualification>(); 
	  qualifications.add(q1); 
	  Worker w2 = new Worker("workerName", qualifications, 42000);
	  w2.addQualification(q1);
	  assertEquals(w2.getSalary(), 42000, 0);
  }
  
  @Test
	public void testIsAvailable() {
		Set<Qualification> projQuals = new HashSet<Qualification>();
		projQuals.add(q1);
		Set<Qualification> workerQuals = new HashSet<Qualification>();
		workerQuals.add(q1);
		Worker w1 = new Worker("workerName", workerQuals, 10000.0);
		Project p1 = new Project("P1", projQuals, ProjectSize.SMALL);
		p1.addQualification(q1);
		w1.addQualification(q1);
		w1.addProject(p1);
		assertTrue(w1.isAvailable());
	}

	@Test
	public void testIsNOTAvailable() {
		Set<Qualification> projQuals = new HashSet<Qualification>();
		projQuals.add(q1);
		Set<Qualification> workerQuals = new HashSet<Qualification>();
		workerQuals.add(q1);
		Worker w1 = new Worker("workerName", workerQuals, 10000.0);
		Project p1 = new Project("P1", projQuals, ProjectSize.SMALL);
		Project p2 = new Project("P2", projQuals, ProjectSize.BIG);
		Project p3 = new Project("P3", projQuals, ProjectSize.BIG);
		Project p4 = new Project("P4", projQuals, ProjectSize.BIG);
		Project p5 = new Project("P5", projQuals, ProjectSize.BIG);
		p1.addQualification(q1);
		w1.addQualification(q1);
		w1.addProject(p1);
		w1.addProject(p2);
		w1.addProject(p3);
		w1.addProject(p4);
		w1.addProject(p5);
		assertFalse(w1.isAvailable());
	}

	@Test
	public void testIsAvailableEdgeCase() {
		Set<Qualification> projQuals = new HashSet<Qualification>();
		projQuals.add(q1);
		Set<Qualification> workerQuals = new HashSet<Qualification>();
		workerQuals.add(q1);
		Worker w1 = new Worker("workerName", workerQuals, 10000.0);
		Project p1 = new Project("P1", projQuals, ProjectSize.BIG);
		Project p2 = new Project("P2", projQuals, ProjectSize.BIG);
		Project p3 = new Project("P3", projQuals, ProjectSize.BIG);
		Project p4 = new Project("P4", projQuals, ProjectSize.BIG);
		p1.addQualification(q1);
		w1.addQualification(q1);
		w1.addProject(p1);
		w1.addProject(p2);
		w1.addProject(p3);
		w1.addProject(p4);
		assertFalse(w1.isAvailable());
	}

	@Test
	public void testAddProject() {
		Project testProject = new Project("P1", qualifications, ProjectSize.SMALL);
		Project testProject2 = new Project("P2", qualifications, ProjectSize.MEDIUM);
		Worker w1 = new Worker("workerName", qualifications, 10000.0);
		w1.addProject(testProject);
		assertEquals("Project was not added to Worker", 1, w1.getProjects().size());
		w1.addProject(testProject);
		assertEquals("Duplicate Project was added to Worker", 1, w1.getProjects().size());
		w1.addProject(testProject2);
		assertEquals("Project was not added to Worker", 2, w1.getProjects().size());
  	}

	@Test(expected = NullPointerException.class)
	public void testNullValueAddProject() {
		w1.addProject(null);
	}

	@Test
	public void testGetWorkload() {
		assertEquals("Failed to return workload on empty project set", 0, w1.getWorkload());
		Project testProject = new Project("P1", qualifications, ProjectSize.SMALL);
		Project testProject2 = new Project("P2", qualifications, ProjectSize.MEDIUM); // at 3
		Project testProject3 = new Project("P3", qualifications, ProjectSize.BIG);  // 6 
		Project testProject4 = new Project("P4", qualifications, ProjectSize.BIG); // 9
		Project testProject5 = new Project("P5", qualifications, ProjectSize.BIG);  // 12
		Project testProject6 = new Project("P6", qualifications, ProjectSize.SMALL);
		
		w1.addProject(testProject);
		assertEquals("Failed to return workload", 1, w1.getWorkload());
		w1.addProject(testProject2);
		w1.addProject(testProject3);
		w1.addProject(testProject4);
		w1.addProject(testProject5);
		assertEquals("Failed to return workload", MAX_WORKLOAD, w1.getWorkload());
		w1.addProject(testProject6);
		assertEquals("Failed to return workload", 13, w1.getWorkload());
	}

	@Test
	public void testFinishedWorkload() {
		p1.setStatus(ProjectStatus.FINISHED);
		w1.addProject(p1);
		assertEquals("Incorrrectly added finished project to workload", 0, w1.getWorkload());
	}

  	@Test
	public void testToDTO(){
		String[] expectedDTOProjects = new String[]{"testProject1", "testProject2"};
		String[] expectedDTOQualifications = new String[]{"testDescription","testDescription2"}; 
		Arrays.sort(expectedDTOQualifications);
		Arrays.sort(expectedDTOProjects);

		Qualification q1 = new Qualification("testDescription");
		Qualification q2 = new Qualification("testDescription2");
		Set<Qualification> testQualficationSet1 = new HashSet<Qualification>();
		Set<Qualification> testQualficationSet2 = new HashSet<Qualification>();
		testQualficationSet1.add(q2);
		testQualficationSet2.add(q2);

		w1.addProject(new Project("testProject1", testQualficationSet1, ProjectSize.BIG));
		w1.addProject(new Project("testProject2", testQualficationSet2, ProjectSize.SMALL));
		w1.addQualification(q1);
		w1.addQualification(q2);

		WorkerDTO w1DTO = w1.toDTO();
		Arrays.sort(w1DTO.getProjects());
		Arrays.sort(w1DTO.getQualifications());

		assertEquals("WorkerDTO and Worker name don't match" , "workerName", w1DTO.getName());
		assertEquals("WorkerDTO and Worker salary don't match", 1, w1DTO.getSalary(), 1e-5);
		assertEquals("WorkerDTO and Worker workload don't match", 4, w1DTO.getWorkload());
		assertArrayEquals("WorkerDTO fails to return correct projects", expectedDTOProjects, w1DTO.getProjects());
		assertArrayEquals("WorkerDTO fails to return correct qualifications", expectedDTOQualifications, w1DTO.getQualifications());
	}

	@Test
	public void testRemoveProject(){ 
		Project p1 = new Project("testProject", qualifications, ProjectSize.BIG);
		p1.addQualification(q1);
		w1.removeProject(p1);
		assertEquals("Removed object form empty set", 0, w1.getProjects().size());
		w1.addProject(p1);
		w1.removeProject(p1);
		assertTrue("Worker didn't remove the project object", w1.getProjects().isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void testRemoveNullProject() {
		w1.removeProject(null);
	}

	@Test
	public void testGetQualifications(){
		Set<Qualification> expectedQualifications = new HashSet<Qualification>();
		Qualification q1 = new Qualification("testDescription");
		expectedQualifications.add(q1);
		assertEquals("Returned qualifiation set doesn't match expected qualifications", expectedQualifications, w1.getQualifications());
		w1.addQualification(q1);
		assertEquals("Failed to return qualifications", 1, w1.getQualifications().size());
	}

	@Test
	public void testAddQualifications() {
		Qualification q2 = new Qualification("testQualification2");
		Worker w1 = new Worker("workerName", qualifications, 1);
		w1.addQualification(q1);
		assertEquals("Failed to add qualification to worker", true, w1.getQualifications().contains(q1));
		w1.addQualification(q1);
		assertEquals("Failed to prevent adding duplicate qualification", 1, w1.getQualifications().size());
		w1.addQualification(q2);
		assertEquals("Failed to add qualification to worker", true, w1.getQualifications().contains(q2));
		assertEquals("Failed to add qualification to worker", 2, w1.getQualifications().size());
	}

	@Test(expected = NullPointerException.class)
	public void testNullValueAddQualification() {
		w1.addQualification(null);
	}
}
