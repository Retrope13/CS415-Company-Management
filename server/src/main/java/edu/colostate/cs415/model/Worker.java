package edu.colostate.cs415.model;

import java.util.HashSet;
import java.util.Set;

import edu.colostate.cs415.dto.WorkerDTO;

public class Worker {

	public static final int MAX_WORKLOAD = 12;

	private String name;
	private double salary;
	private Set<Project> projects;
	private Set<Qualification> qualifications;

	public Worker(String name, Set<Qualification> qualifications, double salary) {
		if (name == null) {
			throw new NullPointerException("Name " + name + " cannot be null.");
		}
		else {
			this.name = name;
		}
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name can not be an empty string.");
		}
		else {
			this.name = name;
		}
		if (qualifications == null) {
			throw new NullPointerException("Qualifications set cannot be null.");
		}
		else if (qualifications.size() < 1) {
			throw new IllegalArgumentException("Workers must contain at least one qualification");
		}
		else {
			this.qualifications = qualifications;
		}
		if (salary <= 0) {
			throw new IllegalArgumentException("Salary must be greater than 0.");
		}
		else {
			this.salary = salary;
		}
		this.projects = new HashSet<Project>();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Worker)) {
			return false;
		}
		Worker otherWorker = (Worker) other;
		return this.getName() == otherWorker.getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		String name = this.getName();
		int numProjects = this.getProjects().size();
		int numQualifications = this.getQualifications().size();
		int salary = (int) (Math.floor(this.getSalary()));
		String retString = String.format("%s:%d:%d:%d", name, numProjects, numQualifications, salary);
		return retString;
	}

	public String getName() {
		return name;
	}

	public double getSalary() {
		return this.salary;
	}

	public void setSalary(double salary) {
		if (salary <= 0) {
			throw new IllegalArgumentException("Salary cannot be negative or equal to zero");
		}
		this.salary = salary;
	}

	public Set<Qualification> getQualifications() {
		return qualifications;
	}

	public void addQualification(Qualification qualification) {
		if (qualification == null) {
			throw new NullPointerException("Qualification cannot be null");
		}
		qualifications.add(qualification);
	}

	public Set<Project> getProjects() {
		return this.projects;
	}

	public void addProject(Project project) {
		if (project == null) {
			throw new NullPointerException("Project cannot be null");
		}
		projects.add(project);
	}

	public void removeProject(Project project) {
		if (project == null) {
			throw new NullPointerException("Project cannot be null");
		}
		projects.remove(project);
	}

	public int getWorkload() {
		int workLoad = 0;
		for (Project project : projects) {
			if (project.getStatus() == ProjectStatus.FINISHED) {
				continue;
			}
			workLoad = project.getSize().getValue() + workLoad;
		}
		return workLoad;
	}

	public boolean willOverload(Project project) {
		if (project == null || this.getProjects().contains(project)) {
			return false;
		}
		return (this.getWorkload() + project.getSize().getValue()) > MAX_WORKLOAD;
	}

	public boolean isAvailable() {
		if (this.getWorkload() < MAX_WORKLOAD) {
			return true;
		}
		return false;
	}

	public WorkerDTO toDTO() {
		int workload = getWorkload();
		String[] projectsArray = new String[projects.size()];
		String[] qualificationsArray = new String[qualifications.size()];

		int i = 0;
		for (Project p : projects) {
			projectsArray[i] = p.getName(); 
			i++;
		}

		i = 0;
		for (Qualification q : qualifications) {
			qualificationsArray[i] = q.toString(); 
			i++;
		}

		return new WorkerDTO(name, salary, workload, projectsArray,
				qualificationsArray);
	}
}
