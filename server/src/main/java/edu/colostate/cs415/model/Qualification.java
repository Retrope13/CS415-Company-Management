package edu.colostate.cs415.model;

import java.util.Set;
import java.util.HashSet;

import edu.colostate.cs415.dto.QualificationDTO;

public class Qualification {

	private String description;
	private Set<Worker> workers;

	public Qualification(String description) {
		
		if (description == null) {
			throw new NullPointerException("description cannot be null");
		} else {
			if (description.trim().isEmpty()) {
				throw new IllegalArgumentException("Description must not be blank or empty");
			}
			else{
				this.description = description;
			}
		}
		this.workers = new HashSet<Worker>();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof Qualification)) {
            return false;
        }
		Qualification q = (Qualification) other;
		return this.toString().equals(q.toString());
	}

	@Override
	public int hashCode() {
		return description.hashCode();
	}

	@Override
	public String toString() {
		return description;
	}

	public Set<Worker> getWorkers() {
		return workers;
	}

	public void addWorker(Worker worker) {
		if (worker == null) {
			throw new IllegalArgumentException("Worker cannot be null");
		}
		workers.add(worker);
	}

	public void removeWorker(Worker worker) {
		if (worker == null) {
			throw new NullPointerException("Worker cannot be null");
		}
		this.workers.remove(worker);
	}

	public QualificationDTO toDTO() {
		String[] workerArray = new String[workers.size()];

		int i = 0;
		for (Worker w : workers) {
			workerArray[i] = w.getName();
			i++;
		}
	
		return new QualificationDTO(description, workerArray);
	}
}
