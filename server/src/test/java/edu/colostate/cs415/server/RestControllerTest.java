package edu.colostate.cs415.server;

import org.junit.Test;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.colostate.cs415.db.DBConnector;
import edu.colostate.cs415.dto.AssignmentDTO;
import edu.colostate.cs415.dto.ProjectDTO;
import edu.colostate.cs415.dto.QualificationDTO;
import edu.colostate.cs415.dto.WorkerDTO;
import edu.colostate.cs415.model.Company;
import edu.colostate.cs415.model.Project;
import edu.colostate.cs415.model.ProjectSize;
import edu.colostate.cs415.model.Qualification;
import edu.colostate.cs415.model.Worker;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class RestControllerTest {
    private Gson gson = new Gson();
    private static DBConnector dbConnector = mock(DBConnector.class);
    private static RestController restController = new RestController(7777, dbConnector);
    
    private static Company c1;
    private static Project p1;
    private static Qualification q1;
    private static Worker w1;

    @BeforeClass
    public static void testRestControllerSetup(){
        when(dbConnector.loadCompanyData()).thenAnswer((i) -> c1);
        c1 = new Company("companyName1");
        q1 = c1.createQualification("qualificationDescription1");
        HashSet<Qualification> q1Set = new HashSet<Qualification>();
        q1Set.add(q1);
        p1 = c1.createProject("projectName1", q1Set, ProjectSize.BIG);
        w1 = c1.createWorker("workerName1", q1Set, 1);
    }

    @Test
    public void testGETProjectName() throws IOException{
        restController.start();
        ProjectDTO testProject = gson.fromJson(
                        Request.get("http://localhost:7777/api/projects/projectName1").execute().returnContent().asString(), ProjectDTO.class);
        assertEquals(p1.toDTO(), testProject);
    }

    @Test
    public void testGETProjects() throws IOException{
        Qualification q2 = c1.createQualification("qualificationDescription2");
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q2);
        Project p2 = c1.createProject("projectName2",q2Set, ProjectSize.SMALL);
        
        ProjectDTO[] projectDTOArray = new ProjectDTO[2];
        projectDTOArray[0] = p1.toDTO();
        projectDTOArray[1] = p2.toDTO();
        
        restController.start();
        ProjectDTO[] testProjects = gson.fromJson(
                        Request.get("http://localhost:7777/api/projects").execute().returnContent().asString(), ProjectDTO[].class);    
        assertArrayEquals(projectDTOArray, testProjects);
    }

    @Test
    public void testGETQualifications() throws IOException {
        QualificationDTO[] QualificationDTOArray = new QualificationDTO[2];
        QualificationDTOArray[0] = new Qualification("qualificationDescription2").toDTO();
        QualificationDTOArray[1] = q1.toDTO();
        restController.start();
        QualificationDTO[] testQualifications = gson.fromJson(
                        Request.get("http://localhost:7777/api/qualifications").execute().returnContent().asString(), QualificationDTO[].class);    
        assertArrayEquals( QualificationDTOArray,testQualifications);
    }

    @Test
    public void testGETQualification() throws IOException {
        q1.addWorker(w1);
        restController.start();
        QualificationDTO testQualifications = gson.fromJson(
                        Request.get("http://localhost:7777/api/qualifications/qualificationDescription1").execute().returnContent().asString(), QualificationDTO.class);    
        assertEquals(q1.toDTO(), testQualifications);
    }

    @Test
    public void testPOSTQualification() throws IOException {
        q1.addWorker(w1);

        QualificationDTO q1DTO = q1.toDTO();
        String q1String = gson.toJson(q1DTO);
        restController.start();
        Request post = Request.post("http://localhost:7777/api/qualifications/qualificationDescription1").bodyString(q1String, ContentType.APPLICATION_JSON);
        int responseCode = post.execute().returnResponse().getCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void testPOSTProject() throws IOException{
        Qualification q3 = new Qualification("qualificationDescription3");
        HashSet<Qualification> q3Set = new HashSet<Qualification>();
        q3Set.add(q3);
        Project p3 = new Project("projectName3", q3Set, ProjectSize.SMALL);
        
        restController.start();
        Request post = Request.post("http://localhost:7777/api/projects/projectName3").bodyString(gson.toJson(p3.toDTO()), ContentType.APPLICATION_JSON);
        int responseCode = post.execute().returnResponse().getCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void testGETWorkers() throws IOException{
        Qualification q2 = c1.createQualification("qualificationDescription2");
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q2);
        Worker w2 = c1.createWorker("workerName2", q2Set, 1);
        
        WorkerDTO[] workerDTOArray = new WorkerDTO[2];
        workerDTOArray[1] = w1.toDTO();
        workerDTOArray[0] = w2.toDTO();
        
        restController.start();
        WorkerDTO[] testWorkers = gson.fromJson(
                        Request.get("http://localhost:7777/api/workers").execute().returnContent().asString(), WorkerDTO[].class);
        assertArrayEquals(workerDTOArray, testWorkers);
    }
    
    @Test
    public void testGETWorkerName() throws IOException{
        restController.start();
        WorkerDTO testWorker = gson.fromJson(
                        Request.get("http://localhost:7777/api/workers/workerName1").execute().returnContent().asString(), WorkerDTO.class);
        assertEquals(w1.toDTO(), testWorker);
    }
    
    @Test
    public void testPOSTWorker() throws IOException{
        String[] qArray = new String[1];
        qArray[0] = "qualificationDescription1";
        String[] pArray = new String[1];
        pArray[0] = "projectName1";
        WorkerDTO w2 = new WorkerDTO("workerName2", 1.0, 0, pArray, qArray);
        
        String w2String = gson.toJson(w2);
        restController.start();
        Request post = Request.post("http://localhost:7777/api/workers/workerName2").bodyString(w2String, ContentType.APPLICATION_JSON);
        int responseCode = post.execute().returnResponse().getCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void testPUTStartProject() throws IOException {
        String expected = "OK";
        c1.assign(w1, p1);
        restController.start();
        String result = Request.put("http://localhost:7777/api/start").bodyString(gson.toJson(p1.toDTO()), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        assertEquals(expected, result);
    }

    @Test(expected = IOException.class) 
    public void testPUTStartInvalidProject() throws IOException {
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q1);
        Project p2 = c1.createProject("projectName2", q2Set, ProjectSize.BIG);
        restController.start();
        Request.put("http://localhost:7777/api/start").bodyString(gson.toJson(p2.toDTO()), ContentType.APPLICATION_JSON).execute().returnContent().asString();
    }

    @Test
    public void testPUTFinishProject() throws IOException {
        String expected = "OK";
        restController.start();
        // p1 is already started from preceeding test so finish can be called without initializing values
        String result = Request.put("http://localhost:7777/api/finish").bodyString(gson.toJson(p1.toDTO()), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        assertEquals(expected, result);
    }

    @Test(expected = IOException.class) 
    public void testPUTFinishInvalidProject() throws IOException {
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q1);
        Project p2 = c1.createProject("projectName2", q2Set, ProjectSize.BIG);
        restController.start();
        Request.put("http://localhost:7777/api/finish").bodyString(gson.toJson(p2.toDTO()), ContentType.APPLICATION_JSON).execute().returnContent().asString();
    }

    @Test
    public void testPUTAssign() throws IOException {
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q1);
        Project p2 = c1.createProject("projectName2", q2Set, ProjectSize.BIG);
        Worker w2 = c1.createWorker("workerName2", q2Set, 1);
        
        AssignmentDTO assignmentDTO = new AssignmentDTO("workerName2", "projectName2");
        String expected = "OK";
        restController.start();
        String result = Request.put("http://localhost:7777/api/assign").bodyString(gson.toJson(assignmentDTO), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        assertEquals(expected, result);
    }

    @Test(expected = IOException.class)
    public void testPUTAssignBadWorkerName() throws IOException {
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q1);
        Project p2 = c1.createProject("projectName2", q2Set, ProjectSize.BIG);
        Worker w2 = c1.createWorker("workerName2", q2Set, 1);
        
        AssignmentDTO assignmentDTO = new AssignmentDTO("BADworkerName2", "projectName2");
        restController.start();
        Request.put("http://localhost:7777/api/assign").bodyString(gson.toJson(assignmentDTO), ContentType.APPLICATION_JSON).execute().returnContent().asString();
    }

    @Test(expected = IOException.class)
    public void testPUTAssignBadWorkerAndProjectName() throws IOException {
        AssignmentDTO assignmentDTO = new AssignmentDTO("BADworkerName2", "BADprojectName2");
        restController.start();
        Request.put("http://localhost:7777/api/assign").bodyString(gson.toJson(assignmentDTO), ContentType.APPLICATION_JSON).execute().returnContent().asString();
    }

    @Test
    public void testPUTUnassign() throws IOException {
        HashSet<Qualification> q2Set = new HashSet<Qualification>();
        q2Set.add(q1);
        Project p2 = c1.createProject("projectName2", q2Set, ProjectSize.BIG);
        Worker w2 = c1.createWorker("workerName2", q2Set, 1);
        c1.assign(w2, p2);
        AssignmentDTO assignmentDTO = new AssignmentDTO("workerName2", "projectName2");
        String expected = "OK";
        restController.start();
        String result = Request.put("http://localhost:7777/api/unassign").bodyString(gson.toJson(assignmentDTO), ContentType.APPLICATION_JSON).execute().returnContent().asString();
        assertEquals(expected, result);
    }
}
