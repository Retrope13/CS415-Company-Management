package edu.colostate.cs415.model;

import java.util.Set;
import java.util.HashSet;

public class Company {

	private String name;
	private Set<Worker> employees;
	private Set<Worker> available;
	private Set<Worker> assigned;
	private Set<Project> projects;
	private Set<Qualification> qualifications;

	public Company(String name) {
		if (name == null) {
			throw new NullPointerException("Name must not be null");
		}
		if (name.trim().isEmpty()) {
			throw new IllegalArgumentException("Name must not be empty or only consisting of whitespace");
		}
		this.name = name;
		this.employees = new HashSet<Worker>();
		this.available = new HashSet<Worker>();
		this.assigned = new HashSet<Worker>();
		this.projects = new HashSet<Project>();
		this.qualifications = new HashSet<Qualification>();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Company)) {
			return false;
		}
		Company c = (Company) other;
		return this.getName() == c.getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return this.name + ":" + this.available.size() + ":" + this.projects.size();
	}

	public String getName() {
		return name;
	}

	public Set<Worker> getEmployedWorkers() {
		return employees;
	}

	public Set<Worker> getAvailableWorkers() {
		return available;
	}

	public Set<Worker> getUnavailableWorkers() {
		Set<Worker> unavailable = new HashSet<Worker>();
		unavailable.addAll(employees);
		unavailable.removeAll(available);
		return unavailable;
	}

	public Set<Worker> getAssignedWorkers() {
		return this.assigned;
	}

	public Set<Worker> getUnassignedWorkers() {
		Set<Worker> unassigned = new HashSet<Worker>();
		unassigned.addAll(this.employees);
		unassigned.removeAll(this.assigned);
		return unassigned;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public Set<Qualification> getQualifications() {
		return this.qualifications;
	}

	public Worker createWorker(String name, Set<Qualification> qualifications, double salary) {
		try {
			boolean qualificationsAreEmpty = qualifications.isEmpty();
			boolean nameIsEmpty = name.trim() == "" ? true : false;
			boolean salaryIsInvalid = Double.compare(salary, 0.0) <= 0 ? true : false;
			boolean qualSubset = this.qualifications.containsAll(qualifications);
			if(nameIsEmpty ||  qualificationsAreEmpty || !qualSubset || salaryIsInvalid){
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}

		Worker newEmployee = new Worker(name, qualifications, salary);
		employees.add(newEmployee);
		available.add(newEmployee);

		for(Qualification q: qualifications){
			q.addWorker(newEmployee);
		}

		return newEmployee;
	}

	public Qualification createQualification(String description) {
		Qualification q1 = new Qualification(description);
		this.qualifications.add(q1);
		return q1;
	}

	public Project createProject(String name, Set<Qualification> qualifications, ProjectSize size) {
		if(name == null || size == null || qualifications == null || name.trim().isEmpty() || qualifications.isEmpty()){
			return null;
		}
		for (Qualification qual: qualifications) {
			if (!this.qualifications.contains(qual)) {
				return null;
			}
		}
		Project newProject = new Project(name, qualifications, size);
		projects.add(newProject);
		return newProject;
	}

	public void start(Project project) {
		if (project == null) {
			throw new IllegalArgumentException("Project cannot be null when calling start");
		}
		if (project.getMissingQualifications().isEmpty() && project.getStatus() != ProjectStatus.FINISHED && project.getStatus() != ProjectStatus.ACTIVE) {
			ProjectStatus newStatus = ProjectStatus.ACTIVE;
			project.setStatus(newStatus);
		}
		else {
			throw new IllegalArgumentException("Cannot call start on an active or finished project");
		}
	}

	public void finish(Project project) {
		if (project == null) {
			throw new IllegalArgumentException("Project cannot be null when calling finish");
		}
		if (project.getStatus() == ProjectStatus.ACTIVE) {
			project.setStatus(ProjectStatus.FINISHED);
			for (Worker w : project.getWorkers()) {
				w.removeProject(project);
				if (w.getProjects().isEmpty()) {
					assigned.remove(w);
				}
				if (!available.contains(w)) {
					available.add(w);
				}
			}
			project.removeAllWorkers();
		}
	}

	public void assign(Worker worker, Project project) {
		if (project == null || worker == null) {
            throw new IllegalArgumentException("Project nor Worker can be null when assigning");
        }
		if (!this.projects.contains(project)) {
			throw new IllegalArgumentException("Project must belong to company to assign workers to it");
		}
		if (employees.contains(worker)) {
			if (available.contains(worker) && !worker.getProjects().contains(project) && !project.getWorkers().contains(worker)) { //I'm checking the worker set of projects and the project set of workers if something slips by
				if (project.isHelpful(worker) && !worker.willOverload(project) && project.getStatus() != ProjectStatus.ACTIVE && project.getStatus() != ProjectStatus.FINISHED) {
					project.addWorker(worker);
					worker.addProject(project);
					if (!assigned.contains(worker)) {
						assigned.add(worker);
					}
					if (worker.getWorkload() == 12) {
						available.remove(worker);
					}
				}
			}
			else {
				throw new IllegalArgumentException("Illegal assign call");
			}
		}
		else {
			throw new IllegalArgumentException("Attempted to assign Worker not employed by the Company");
		}
	}

	public void unassign(Worker worker, Project project) {
		if (project == null || worker == null) {
            throw new NullPointerException("Project nor Worker can be null when unassigning");
        }
        if (project.getWorkers().contains(worker) && worker.getProjects().contains(project)) {
            project.removeWorker(worker);
            worker.removeProject(project);
            if (worker.getProjects().isEmpty()) {
                assigned.remove(worker);
			}
            if (!available.contains(worker)) {
                available.add(worker);
            }
            if(!project.getMissingQualifications().isEmpty() && project.getStatus() == ProjectStatus.ACTIVE) {
                project.setStatus(ProjectStatus.SUSPENDED);
            }
        }
	}

	public void unassignAll(Worker worker) {
		if (worker == null) {
			throw new NullPointerException("Worker object cannot be null when unassigning");
		}
	        for (Project project : worker.getProjects()) {
	            if (project.getWorkers().contains(worker)) {
	                project.removeWorker(worker);
	                worker.removeProject(project);
	            }
	            if (!project.getMissingQualifications().isEmpty() && project.getStatus() == ProjectStatus.ACTIVE) {
	                project.setStatus(ProjectStatus.SUSPENDED);
	            }
	        }
	        if (assigned.contains(worker)) {
	            assigned.remove(worker);
			}
	        if(!available.contains(worker)) {
	            available.add(worker);
	        }
	}
}
