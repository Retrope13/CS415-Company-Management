package edu.colostate.cs415.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import edu.colostate.cs415.dto.QualificationDTO;

public class QualificationTest {
	
	private String qualificationDescription = "testDescription";
	private double salary = 42000.0;
	private Qualification q1;
	private Qualification q2;
	private Qualification q3;
	
	@Before
	public void qualificationTestSetup(){
		q1 = new Qualification(qualificationDescription); //tested with null and empty string, threw error as expected
	}

	//Description variable tests
	@Test(expected = NullPointerException.class)
	public void testNullQualification() {
		q1 = new Qualification(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadDescription() {
		q1 = new Qualification("    ");
	}

	@Test
	public void testEquals() {
		q1 = new Qualification(qualificationDescription);
		q2 = new Qualification(qualificationDescription);
		q3 = new Qualification("testDescription fail");
		
		Object o = new Object();
		assertTrue(q1.equals(q2));
		assertFalse(q1.equals(o));
		assertFalse(q1.equals(q3));
	}

	@Test
	public void testNullEquals() {
		assertFalse(q1.equals(null));
	}
	
	@Test
	public void testHashCodeDifferentNames(){
		Qualification q1 = new Qualification(qualificationDescription);
		Qualification q2 = new Qualification("testDescription fail");
		assertNotEquals("Hash is identical for different Qualification descriptions", q1.hashCode(), q2.hashCode());
	}
  
	@Test
	public void testHashCodeSameNames(){
		Qualification q1 = new Qualification(qualificationDescription);
		Qualification q2 = new Qualification(qualificationDescription);
		assertEquals("Hash is not identical for the same Qualification description", q1.hashCode(), q2.hashCode());
	}
	
	@Test
	public void testDescriptionToString(){
		assertEquals("The toString() method doesn't return the description string", qualificationDescription, q1.toString());
		
	}

	@Test
	public void testConvertToDTO(){
		String[] expectedWorkersString = new String[]{"workerName1"};
		Set<Qualification> qualifications = new HashSet<Qualification>();
		Qualification q1 = new Qualification(qualificationDescription);
		qualifications.add(q1);
		
		Worker w1 = new Worker("workerName1", qualifications, 1);
		w1.addQualification(q1);
		
		
		q1.addWorker(w1);
		QualificationDTO q1DTO = q1.toDTO();
		assertEquals("QualificationDTO and Qualification description don't match" , qualificationDescription, q1DTO.getDescription());
		assertArrayEquals("QualificationDTO and Qualification Worker set don't match", expectedWorkersString, q1DTO.getWorkers());
	}
	

  //Worker variable tests
   @Test
    public void testAddGetWorkers(){
		Qualification q1 = new Qualification(qualificationDescription);
		Set<Worker> emptyWorkers = new HashSet<Worker>();
		Set<Worker> nonEmptyWorkers = new HashSet<Worker>();
		Set<Qualification> quals = new HashSet<Qualification>();
		quals.add(q1);
		assertEquals("Get workers did not return an empty set", q1.getWorkers(), emptyWorkers);

		Worker w1 = new Worker("workerName1", quals, salary);
		w1.addQualification(q1);
		Worker w2 = new Worker("workerName2", quals, salary);
		w2.addQualification(q1);
		nonEmptyWorkers.add(w1);
		q1.addWorker(w1);
		assertEquals("Get workers did not return one worker", q1.getWorkers(), nonEmptyWorkers);
		assertEquals(q1.getWorkers().contains(w1), true);

		nonEmptyWorkers.add(w2);
		q1.addWorker(w2);
		assertEquals("Get workers did not return more than one Worker", q1.getWorkers(), nonEmptyWorkers);
		assertEquals(q1.getWorkers().contains(w2), true);
	}

	@Test
	public void testGetWorkersEmptySet() {
		assertTrue("Failed to return an empty set", q1.getWorkers().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullWorker() {
		q1.addWorker(null);
	}

	@Test
	public void testRemoveExistingWorker() {
		q1 = new Qualification(qualificationDescription);
		Set<Qualification> qualifications= new HashSet<Qualification>();
		qualifications.add(q1);
		Worker w1 = new Worker("workerName1", qualifications, salary);
		q1.addWorker(w1);
		q1.removeWorker(w1);
		assertEquals("Worker was not removed from the Workers set", true, q1.getWorkers().isEmpty());
	}

	@Test
	public void testRemoveNonExistingWorker() {
		q1 = new Qualification(qualificationDescription);
		Set<Qualification> qualifications= new HashSet<Qualification>();
		qualifications.add(q1);
		Worker w1 = new Worker("workerName1", qualifications, salary);
		q1.removeWorker(w1);
		assertEquals("Attempted to remove worker that was not added to the set", 0, q1.getWorkers().size());
	}

	@Test(expected = NullPointerException.class)
	public void testRemoveNullWorker() {
		q1.removeWorker(null);
	}
}
