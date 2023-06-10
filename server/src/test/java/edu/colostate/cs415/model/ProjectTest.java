package edu.colostate.cs415.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import java.util.Set;
import java.util.HashSet;

import edu.colostate.cs415.dto.ProjectDTO;

public class ProjectTest {

	private Project p1;
	private Qualification q1;
	private Set<Qualification> qualifications;
	
	@Before
	public void testProjectSetup(){
		qualifications = new HashSet<Qualification>();
		q1 = new Qualification("testDescription");
		qualifications.add(q1);
		p1 = new Project("projectName", qualifications, ProjectSize.SMALL);
	}

	@Test
	public void testProjectConstructor() {
		assertEquals("projectName", p1.getName());
		assertEquals(ProjectSize.SMALL, p1.getSize());
		assertEquals(p1.getRequiredQualifications(), qualifications);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullName() {
		Project p1 = new Project(null, qualifications, ProjectSize.MEDIUM);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullQuals() {
		Project p1 = new Project("projectName", null, ProjectSize.MEDIUM);

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyQuals() {
		Project p1 = new Project("projectName", new HashSet<Qualification>(), ProjectSize.MEDIUM);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullSize() {
		Project p1 = new Project("projectName", qualifications, null);
	}

	@Test
	public void testProjectEqualsOverride() {
		Project p1 = new Project("projectName", qualifications, ProjectSize.SMALL);
		Project p2 = new Project("projectName", qualifications, ProjectSize.SMALL);
		Project p3 = new Project("projectName1", qualifications, ProjectSize.SMALL);
		Project p4 = new Project("projectName1", qualifications, ProjectSize.BIG);
		assertEquals("Projects with same name failed to show equality", true, p1.equals(p2));
		assertEquals("Projects with different names showed equality", false, p1.equals(p3));
		assertEquals("Projects with same name failed to show equality", true, p3.equals(p4));
		assertEquals("Object was incorrectly cast to project", p1.equals("projectName"), false);
	}

	@Test
	public void testHashCodeDifferentNames() {
		Project p2 = new Project("projectName1", qualifications, ProjectSize.SMALL);
		assertNotEquals("Hash is identical for different Project names", p1.hashCode(), p2.hashCode());
	}

	@Test
	public void testHashCodeSameNames() {
		Project p2 = new Project("projectName", qualifications, ProjectSize.SMALL);
		assertEquals("Hash is not identical for the same Project name", p1.hashCode(), p2.hashCode());
	}
	
	@Test
	public void testToString() {
		p1 = new Project("projectName", qualifications, ProjectSize.SMALL);
		p1.setStatus(ProjectStatus.PLANNED);
		Worker w1 = new Worker("workerName", qualifications, 1);
		assertEquals(p1.toString(), "projectName:0:PLANNED");
		p1.addWorker(w1);
		assertEquals(p1.toString(), "projectName:1:PLANNED");
		p1.setStatus(ProjectStatus.FINISHED);
		assertEquals(p1.toString(), "projectName:1:FINISHED");
	}
	
	@Test
	public void testGetProjectName() {
		assertEquals("Accessor method for project name returns incorrect name", "projectName", p1.getName());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyName() {
		Project p1 = new Project("", qualifications, ProjectSize.MEDIUM);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWhitespaceName() {
		Project p1 = new Project("   ", qualifications, ProjectSize.MEDIUM);
	}
	
	@Test
	public void testGetSize() {
		assertEquals(p1.getSize(), ProjectSize.SMALL);
	}

	@Test
	public void testGetSetStatus() {
		assertEquals("Upon creation, project should be planned", ProjectStatus.PLANNED, p1.getStatus());
		p1.setStatus(ProjectStatus.ACTIVE);
		assertEquals("Project should be active", ProjectStatus.ACTIVE, p1.getStatus());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullStatus() {
		p1.setStatus(null);
	}

	@Test
	public void testAddWorker() {
		Worker w1 = new Worker("workerName1", qualifications, 1);
		Worker w2 = new Worker("workerName2", qualifications, 1);
		p1.addWorker(w1);
		assertEquals("Worker was not added to project", 1, p1.getWorkers().size());
		p1.addWorker(w2);
		assertEquals("Worker is not the same worker that was added", p1.getWorkers().contains(w1), true);
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddNullWorker() {
		p1.addWorker(null);
	}
	
	@Test
	public void testRemoveWorker() {
		Worker w1 = new Worker("workerName1", qualifications, 1);
		Worker w2 = new Worker("workerName2", qualifications, 1);
		assertEquals(p1.getWorkers().size(), 0);
		p1.addWorker(w1);
		assertEquals(p1.getWorkers().size(), 1);
		p1.removeWorker(w2);
		assertEquals(p1.getWorkers().size(), 1);
		p1.removeWorker(w1);
		assertEquals(p1.getWorkers().size(), 0);
		p1.removeWorker(w1);
		assertEquals(p1.getWorkers().size(), 0);
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveNullWorker() {
		p1.removeWorker(null);
	}
	
	@Test
	public void testGetWorkers() {
		Set<Worker> workers = new HashSet<Worker>();
		Worker w1 = new Worker("w1", qualifications, 1);
		Worker w2 = new Worker("w2", qualifications, 1);
		assertEquals("Set of workers returned doesn't match anticipated set", workers, p1.getWorkers());
		assertTrue("Worker set is not empty upon creation", p1.getWorkers().isEmpty());
		workers.add(w1);
		workers.add(w2);
		p1.addWorker(w1);
		p1.addWorker(w2);
		assertEquals("Set of workers returned doesn't match anticipated set", workers, p1.getWorkers());
	}

	@Test
	public void testRemoveAllWorkers() {
		Worker w1 = new Worker("workerName1", qualifications, 1);
		Worker w2 = new Worker("workerName2", qualifications, 1);
		assertEquals(p1.getWorkers().size(), 0);
		p1.addWorker(w1);
		p1.addWorker(w2);
		assertEquals(p1.getWorkers().size(), 2);
		p1.removeAllWorkers();
		assertEquals(p1.getWorkers().size(), 0);
	}

	@Test
	public void testAddGetRequiredQualifications() {
		p1.addQualification(q1);
		assertTrue(p1.getRequiredQualifications().contains(q1));
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddNullQuals() {
		p1.addQualification(null);
	}
	
	@Test
	public void testIsHelpful() {
		qualifications.add(new Qualification("testDescription1"));
		
		Set<Qualification> qualifications2 = new HashSet<Qualification>();
		qualifications2.add(new Qualification("testDescription2"));
		qualifications2.add(new Qualification("testDescription3"));
		
		Set<Qualification> qualifications3 = new HashSet<Qualification>();
		qualifications3.add(new Qualification("testDescription"));
		
		Worker w1 = new Worker("workerName", qualifications, 1);
		Worker w2 = new Worker("w2", qualifications2, 1);
		Worker w3 = new Worker("w3", qualifications3, 1);

		assertEquals("Worker wasn't identified as helpful when it should be", p1.isHelpful(w1), true);
		assertEquals("Worker was identified as helpful when it shouldn't be", p1.isHelpful(w2), false);
		assertEquals("Worker wasn't identified as helpful when it should be", p1.isHelpful(w3), true);
		p1.addWorker(w1);
		assertFalse(p1.isHelpful(w3));
	}

	@Test
	public void testNullIsHelpful() {
		assertFalse(p1.isHelpful(null));
	}
	
	@Test
	public void testGetMissingQualifications() {
		Qualification q2 = new Qualification("testDescription2");
		qualifications.add(q2);
		
		Set<Qualification> qualifications2 = new HashSet<Qualification>();
		Qualification q4 = new Qualification("testDescription2");
		Qualification q5 = new Qualification("testDescription3");
		qualifications2.add(q1);

		Worker w1 = new Worker("workerName", qualifications2, 1);
		p1.addWorker(w1);

		Set<Qualification> expectedMissingQuals = new HashSet<Qualification>();
		expectedMissingQuals.add(q2);
		assertEquals("Failed to return missing qualification", true, expectedMissingQuals.equals(p1.getMissingQualifications()));
		assertEquals("Error - Original set was modified", 2, qualifications.size());

		Set<Qualification> expectedEmptySet = new HashSet<Qualification>();
		w1.addQualification(q4);
		assertEquals("Failed to return missing qualification", true, expectedEmptySet.equals(p1.getMissingQualifications()));
		w1.addQualification(q5);
		assertEquals("Failed to return missing qualification", true, expectedEmptySet.equals(p1.getMissingQualifications()));
	}

	@Test
	public void testGetMissingQualificationsMultipleWorkers() {
		Set<Qualification> qualifications = new HashSet<Qualification>();
		Set<Qualification> qualifications2 = new HashSet<Qualification>();
		Set<Qualification> qualifications3 = new HashSet<Qualification>();
		
		Qualification q1 = new Qualification("testDescription");
		Qualification q2 = new Qualification("testDescription2");
		Qualification q3 = new Qualification("testDescription3");
		
		qualifications.add(q1);
		qualifications.add(q2);
		qualifications.add(q3);	
		qualifications2.add(q1);
		qualifications3.add(q2);

		Worker w1 = new Worker("workerName1", qualifications2, 1);
		Worker w2 = new Worker("workerName2", qualifications3, 1);
		Project p2 = new Project("projectName", qualifications, ProjectSize.BIG);
		p2.addWorker(w1);
		p2.addWorker(w2);

		Set<Qualification> expectedMissingQuals = new HashSet<Qualification>();
		expectedMissingQuals.add(q3);
		assertEquals("Failed to return missing qualification", true, expectedMissingQuals.equals(p2.getMissingQualifications()));
		Set<Qualification> expectedEmptySet = new HashSet<Qualification>();
		w1.addQualification(q3);
		assertEquals("Failed to return missing qualification", true, expectedEmptySet.equals(p2.getMissingQualifications()));
	}
	
	@Test
	public void testToDTO() {
		String[] expectedDTOWorkers = {"workerName"};
		String[] expectedDTOQualifications = {"projectDescription2", "projectDescription1"};
		String[] expectedDTOMissingQualifications  = {"projectDescription2"};

		Set<Qualification> qualifications1 = new HashSet<Qualification>();
		Set<Qualification> qualifications2 = new HashSet<Qualification>();
		Qualification q1 = new Qualification("projectDescription1");
		Qualification q2 = new Qualification("projectDescription2");
		qualifications1.add(q1);
		qualifications1.add(q2);
		qualifications2.add(q1);

		p1 = new Project("projectName", qualifications1, ProjectSize.BIG);
		Worker w1 = new Worker("workerName", qualifications2, 1);
		p1.addWorker(w1);


		ProjectDTO p1DTO = p1.toDTO();
		assertEquals(p1.getName(), p1DTO.getName());
		assertEquals("Size returned from DTO doesn't match project size", p1.getSize(), p1DTO.getSize());
		assertArrayEquals("Returned incorrect array of Workers", expectedDTOWorkers, p1DTO.getWorkers());
		assertArrayEquals("Returned incorrect array of Qualifiations", expectedDTOQualifications, p1DTO.getQualifications());
		assertArrayEquals("Retuned incorrect array of missing Qualifications", expectedDTOMissingQualifications, p1DTO.getMissingQualifications());
	}
}