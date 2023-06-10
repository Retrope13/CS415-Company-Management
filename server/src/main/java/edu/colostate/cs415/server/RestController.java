package edu.colostate.cs415.server;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.redirect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.logging.Logger;

import com.google.gson.Gson;

import edu.colostate.cs415.db.DBConnector;
import edu.colostate.cs415.dto.AssignmentDTO;
import edu.colostate.cs415.dto.ProjectDTO;
import edu.colostate.cs415.dto.QualificationDTO;
import edu.colostate.cs415.dto.WorkerDTO;
import edu.colostate.cs415.model.Company;
import edu.colostate.cs415.model.Project;
import edu.colostate.cs415.model.Qualification;
import edu.colostate.cs415.model.Worker;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class RestController {

	private static Logger log = Logger.getLogger(RestController.class.getName());
	private static String OK = "OK";
	private static String KO = "KO";

	private DBConnector dbConnector;
	private Company company;
	private Gson gson;

	public RestController(int port, DBConnector dbConnector) {
		port(port);
		this.dbConnector = dbConnector;
		gson = new Gson();
	}

	public void start() {
		// Load data from DB
		company = dbConnector.loadCompanyData();

		// Redirect
		redirect.get("/", "/helloworld");

		// Logging
		after("/*", (req, res) -> logRequest(req, res));
		exception(Exception.class, (exc, req, res) -> handleException(exc, res));

		// Hello World
		get("/helloworld", (req, res) -> helloWorld());

		// API
		path("/api", () -> {
			// Enable CORS
			options("/*", (req, res) -> optionsCORS(req, res));
			after("/*", (req, res) -> enableCORS(res));

			// Qualifications
			path("/qualifications", () -> {
				get("", (req, res) -> getQualifications(), gson::toJson);
				get("/:description", (req, res) -> getQualification(req.params("description")),
						gson::toJson);
				post("/:description", (req, res) -> createQualification(req));
			});

			// Projects
			path("/projects", () -> {
				get("", (req, res) -> getProjects(), gson::toJson);
				get("/:name", (req, res) -> getProject(req.params("name")), gson::toJson);
				post("/:name", (req, res) -> createProject(req));
			});
			// Workers
			path("/workers", () -> {
				get("", (req, res) -> getWorkers(), gson::toJson);
				get("/:name", (req, res) -> getWorker(req.params("name")), gson::toJson);
				post("/:name", (req, res) -> createWorker(req));
			});

			// Company
			put("/start", (req, res) -> startProject(req));
			put("/finish", (req, res) -> finishProject(req));
			put("/assign", (req, res) -> assign(req));
			put("/unassign", (req, res) -> unassign(req));
			
		});
	}

	public void stop() {
		Spark.stop();
	}

	private String helloWorld() {
		return "Hello World!";
	}

	private QualificationDTO[] getQualifications() {
		Set<QualificationDTO> qualDTOSet = new HashSet<QualificationDTO>();
		for (Qualification qual : company.getQualifications()) {
			qualDTOSet.add(qual.toDTO());
		}
		return qualDTOSet.toArray(new QualificationDTO[qualDTOSet.size()]);
	}

	private QualificationDTO getQualification(String description) {
		if (description == null || description.trim() == "") {
			throw new IllegalArgumentException("Description cannot be empty or null");
		}
		QualificationDTO qualDTO = null;
		for (Qualification qual : company.getQualifications()) {
			if (Objects.equals(qual.toDTO().getDescription(), description)) {
				qualDTO = qual.toDTO();
			}
		}
		return qualDTO;
	}

	private String createQualification(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null");
		}
		QualificationDTO assignmentDTO = gson.fromJson(request.body(), QualificationDTO.class);
		if (request.params("description").equals(assignmentDTO.getDescription())) {
			company.createQualification(assignmentDTO.getDescription());
		} else
			throw new RuntimeException("Qualification descriptions do not match.");
		return OK;
	}

	private ProjectDTO getProject(String name){
		if (name == null || name.trim() == "") {
			throw new IllegalArgumentException("Name cannot be empty or null");
		}
		Project reqProject = null;
		for(Project p: company.getProjects()){
			if(p.getName().equals(name)){reqProject = p;}
		}
		if(reqProject != null){
			return reqProject.toDTO();
		}else{
			throw new RuntimeException("Provided name doesn't match any Project in Company database.");
		}
	}

	private String createProject(Request request){
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be empty or null");
		}
		ProjectDTO projectDTO = gson.fromJson(request.body(), ProjectDTO.class);
		boolean matchName = request.params("name").equals(projectDTO.getName());

		if(matchName) {
			HashSet<Qualification> qualificationSet = new HashSet<Qualification>();

			for(String s: projectDTO.getQualifications()){
				for(Qualification cq: company.getQualifications()){
					if(cq.toString().equals(s)){
						qualificationSet.add(cq);
					}
				}
			}
			company.createProject(projectDTO.getName(), qualificationSet, projectDTO.getSize());
		}else{
			throw new RuntimeException("One or more request parameters do not match data transfer object.");
		}
		return OK;
	}

	private ProjectDTO[] getProjects(){
		ProjectDTO[] retArray = new ProjectDTO[company.getProjects().size()];
		int i = 0;
		
		for(Project p: company.getProjects()){
			retArray[i] = p.toDTO();
			i++;
		}
		return retArray;
	}
	
	private WorkerDTO[] getWorkers() {
		WorkerDTO[] retArray = new WorkerDTO[company.getEmployedWorkers().size()];
		int i = 0;
		
		for(Worker w: company.getEmployedWorkers()) {
			retArray[i] = w.toDTO();
			i++;
		}
		return retArray;
	}
	
	private WorkerDTO getWorker(String name){
		if (name == null || name.trim() == "") {
			throw new IllegalArgumentException("Name cannot be empty or null");
		}
		Worker reqWorker = null;
		for(Worker w: company.getEmployedWorkers()){
			if(w.getName().equals(name)){reqWorker = w;}
		}
		if(reqWorker != null){
			return reqWorker.toDTO();
		}else{
			throw new RuntimeException("Provided name doesn't match any Worker in Company database.");
		}
	}
	

	private String createWorker(Request request){
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be empty or null");
		}
		WorkerDTO workerDTO = gson.fromJson(request.body(), WorkerDTO.class);
		boolean matchName = request.params("name").equals(workerDTO.getName());
		
		if(matchName) {
			HashSet<Qualification> qualificationSet = new HashSet<Qualification>();
			for(String s: workerDTO.getQualifications()){
				for(Qualification cq: company.getQualifications()){
					if(cq.toString().equals(s)){
						qualificationSet.add(cq);
					}
				}
			}
			company.createWorker(workerDTO.getName(), qualificationSet, workerDTO.getSalary());
		}else{
			throw new RuntimeException("One or more request parameters do not match data transfer object.");
		}
		return OK;
	}

	private String startProject(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be empty or null");
		}
		Project reqProject = null;
		ProjectDTO assignmentDTO = gson.fromJson(request.body(), ProjectDTO.class);
		for (Project p: company.getProjects()) {
			if (p.getName().equals(assignmentDTO.getName())) {
				reqProject = p;
				company.start(reqProject);
				break;
			}else
			throw new RuntimeException("Unable to start the project.");
		}
		return OK;
	}

	private String finishProject(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be empty or null");
		}
		Project reqProject = null;
		ProjectDTO assignmentDTO = gson.fromJson(request.body(), ProjectDTO.class);
		for (Project p: company.getProjects()) {
			if (p.getName().equals(assignmentDTO.getName())) {
				reqProject = p;
				company.finish(reqProject);
				break;
			}else
			throw new RuntimeException("Unable to finish the project.");
		}
		return OK;
	}

	private String assign(Request request) {
		AssignmentDTO assignmentDTO = gson.fromJson(request.body(), AssignmentDTO.class);
		Project reqProject = null;
		Worker reqWorker = null;
		WorkerDTO workerDTO = getWorker(assignmentDTO.getWorker());
		ProjectDTO projectDTO = getProject(assignmentDTO.getProject());

		for (Project p: company.getProjects()) {
			if (p.getName().equals(projectDTO.getName())) {
				reqProject = p;
				break; 
			}
		}
		
		for(Worker w: company.getAvailableWorkers()){
			if(w.getName().equals(workerDTO.getName())){
				reqWorker = w;
				break;
			}
		}
		
		if (reqProject.getName().equals(projectDTO.getName()) && reqWorker.getName().equals(workerDTO.getName())) {
			company.assign(reqWorker, reqProject);
		} else {
			throw new RuntimeException("Failed to assign worker to project.");
		}
		return OK;
	}

	private String unassign(Request request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be empty or null");
		}
		AssignmentDTO assignmentDTO = gson.fromJson(request.body(), AssignmentDTO.class);
		Project reqProject = null;
		Worker reqWorker = null;
		WorkerDTO workerDTO = getWorker(assignmentDTO.getWorker());
		ProjectDTO projectDTO = getProject(assignmentDTO.getProject());

		for (Project p: company.getProjects()) {
			if (p.getName().equals(projectDTO.getName())) {
				reqProject = p;
				break; 
			}
		}
		
		for(Worker w: company.getAvailableWorkers()){
			if(w.getName().equals(workerDTO.getName())){
				reqWorker = w;
				break;
			}
		}
		
		if (reqProject.getName().equals(projectDTO.getName()) && reqWorker.getName().equals(workerDTO.getName())) {
			company.unassign(reqWorker, reqProject);
		} else {
			throw new RuntimeException("Failed to assign worker to project.");
		}
		return OK;
	}

	// Logs every request received
	private void logRequest(Request request, Response response) {
		log.info(request.requestMethod() + " " + request.pathInfo() + "\nREQUEST:\n" + request.body() + "\nRESPONSE:\n"
				+ response.body());
	}

	// Exception handling
	private void handleException(Exception exception, Response response) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace();
		exception.printStackTrace(pw);
		log.severe(sw.toString());
		response.body(KO);
		response.status(500);
	}

	// Enable CORS
	private void enableCORS(Response response) {
		response.header("Access-Control-Allow-Origin", "*");
	}

	// Enable CORS
	private String optionsCORS(Request request, Response response) {
		String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
		if (accessControlRequestHeaders != null)
			response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);

		String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
		if (accessControlRequestMethod != null)
			response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
		return OK;
	}
}