package edu.colostate.cs415.model;

import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

import edu.colostate.cs415.dto.ProjectDTO;

public class Project {

	private String name;
	private ProjectSize size;
	private ProjectStatus status;
	private Set<Worker> workers;
	private Set<Qualification> qualifications;

	public Project(String name, Set<Qualification> qualifications, ProjectSize size) {
		if (name == null) {
			throw new NullPointerException("Project name cannot be null");
		}
		else {
			if (name.trim().isEmpty()) {
				throw new IllegalArgumentException("Project name must not be blank or empty");
			}
			else {
				this.name = name;
			}
		}
		if (qualifications == null) {
			throw new NullPointerException("Project qualifications cannot be null");
		}
		else {
			if (qualifications.isEmpty()) {
				throw new IllegalArgumentException("Project must have at least one qualification");
			}
			else {
				this.qualifications = qualifications;
			}
		}
		if (size == null) {
			throw new NullPointerException("Project size cannot be null");
		}
		else {
			this.size = size;
		}
		this.workers = new HashSet<Worker>();
		setStatus(ProjectStatus.PLANNED);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Project)) {
			return false;
		}
		Project p = (Project) other;
		return this.getName() == p.getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return this.name + ":" + this.workers.size() + ":" + this.status;
	}

	public String getName() {
		return name;
	}

	public ProjectSize getSize() {
		return size;
	}

	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("Status cannot be null");
		}
		this.status = status;
	}

	public void addWorker(Worker worker) {
		if (worker == null) {
			throw new NullPointerException("Worker cannot be null");
		}
		workers.add(worker);
	}

	public void removeWorker(Worker worker) {
		if (worker == null) {
			throw new NullPointerException("Worker cannot be null");
		}
		workers.remove(worker);
	}

	public Set<Worker> getWorkers() {
		return workers;
	}

	public void removeAllWorkers() {
		workers.clear();
	}

	public Set<Qualification> getRequiredQualifications() {
		return this.qualifications;
	}

	public void addQualification(Qualification qualification) {
		if (qualification == null) {
			throw new NullPointerException("Qualification cannot be null");
		}
		if (this.status != ProjectStatus.PLANNED && this.status != ProjectStatus.ACTIVE) {
			throw new IllegalArgumentException("Project must be in planning phase to add qualifications");
		}
		qualifications.add(qualification);
	}

	public Set<Qualification> getMissingQualifications() {
		Set<Qualification> quals = this.getRequiredQualifications();
		Set<Qualification> workerQuals = new HashSet<Qualification>();
		Set<Qualification> missingQuals = new HashSet<Qualification>();
		missingQuals.addAll(quals);
		for (Worker worker: this.getWorkers()) {
			for (Qualification qual: worker.getQualifications()) {
				workerQuals.add(qual);
			}
		}
		missingQuals.removeAll(workerQuals);
		return missingQuals;
	}

	public boolean isHelpful(Worker worker) {
		if (worker == null) {
			return false;
		}
		return !Collections.disjoint(worker.getQualifications(), this.getMissingQualifications());
	}

	public ProjectDTO toDTO() {
		Set<Qualification> missingQualifications = getMissingQualifications();
		String[] workerArray = new String[workers.size()];
		String[] qualificationsArray = new String[qualifications.size()];
		String[] missingQualificationsArray = new String[missingQualifications.size()];

		int i = 0;
		for (Worker w : workers) {
			workerArray[i] = w.getName(); 
			i++;
		}

		i = 0;
		for (Qualification q : qualifications) {
			qualificationsArray[i] = q.toString(); 
			i++;
		}

		i = 0;
		for (Qualification m : missingQualifications) {
			missingQualificationsArray[i] = m.toString(); 
			i++;
		}

		return new ProjectDTO(name, size, status, 
			workerArray, 
			qualificationsArray, 
			missingQualificationsArray);
	}
}