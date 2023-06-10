import { useEffect, useState } from 'react'
import { pageStyle } from '../utils/styles'
import { getProjects, getWorkers, unassign, assign } from '../services/dataService'
import { createWorker, getQualifications } from '../services/dataService'
import ClickList from '../components/ClickList'
import LocationID from '../utils/location'
import { grayContainerStyle, darkGrayContainerStyle} from '../utils/styles'
import Select from 'react-select';

const Worker = (worker, active) => {
    return (
        <div>
            <div>{worker.name}</div>
            {active === true ? WorkerBody(worker) : null}
        </div>
    )
}

const WorkerBody = (worker) => {
    return (
        <div id="parent" class="flex-container" style={grayContainerStyle}>
		    <div id="left" class="flex-child" style={grayContainerStyle}>
                <div id="salary">
		            Worker Information
		        </div>
			    <div id="salary" style={darkGrayContainerStyle}>
		            Salary: {worker.salary}
		        </div>
			    <div id="workload" style={darkGrayContainerStyle}>
		            Current Workload: {worker.workload}
		        </div>
		    </div>
		    <div id="center" class="flex-child" style={grayContainerStyle}>
		        <div id="qualifications">
		        	Worker Qualifications:
                    <ClickList list={worker.qualifications} path='/qualifications' id='description' styles={darkGrayContainerStyle}/>
		        </div>
		    </div>
	    	<div id="right" class="flex-child" style={grayContainerStyle}>
	        	<div id="projects">
		    		Assigned Projects:
                    <ClickList list={worker.projects} path='/projects' id='name' styles={darkGrayContainerStyle}/>
		        </div>
		    </div>
        </div>
    )
}

const Workers = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
	const [workerName, setWorkerName] = useState(null);
	const [salary, setSalary] = useState(null);
	const [qualifications, setQualifications] = useState([]);
	const [selectedOptions, setSelectedOptions] = useState([]);
    const [workers, setWorkers] = useState([])
	const [selectedWorker, setSelectedWorker] = useState([]);
	const [selectedProject, setSelectedProject] = useState([]);
	const [projects, setProjects] = useState([]);
	const [showUnassignModal, setShowUnassign] = useState(false);
	const [showAssignModal, setShowAssign] = useState(false);
	useEffect(() => { getWorkers().then(setWorkers) }, [])
	useEffect(() => { getQualifications().then(setQualifications) }, [])
	useEffect(() => { getProjects().then(setProjects) }, [])

	const options = qualifications.map((option) => ({
		value: option,
		label: option.description,
	}));

	const projectOptions = projects.map((option) => ({
		value: option,
		label: option.name
	}));

	const workerOptions = workers.map((option) => ({
		value: option,
		label: option.name
	}));

	const handleSubmitClick = async () => {
        createWorker(workerName, selectedOptions.map(e => e.label), salary);
		setIsModalOpen(false);
		setSelectedOptions([]);
	}

	const handleWorkerName = (event) => {
		setWorkerName(event.target.value);
	}

	const handleSalary = (event) => {
		setSalary(event.target.value);
	}

	const handleUnassign = async () => {
		await unassign(selectedWorker.label, selectedProject.label);
		setShowUnassign(false);
		setSelectedProject();
		setSelectedWorker();
		window.location.reload(true);
	}

	function handleUnassCancel() {
		setShowUnassign(false);
		setSelectedWorker([]);
		setSelectedProject([]);
	}

	const handleAssign = async () => {
		await assign(selectedWorker.label, selectedProject.label);
		setShowAssign(false);
		setSelectedProject();
		setSelectedWorker();
		window.location.reload(true);
	}

	function handleAssCancel() {
		setShowAssign(false);
		setSelectedWorker([]);
		setSelectedProject([]);
	}

    const active = LocationID('workers', workers, 'name')
    return (
        <div style={pageStyle}>
            <h1>
                This page displays a table containing all the workers.
            </h1>
			<button onClick={() => setShowAssign(true)}>Assign Worker to Project</button>
			<button className = "finish" onClick={() => setShowUnassign(true)}>Unassign Worker from Project</button>
            <button onClick={() => setIsModalOpen(true)}>Create a new Worker.</button>
			{showUnassignModal && (
				<div className='modal'>
					<div className="modal-content">
						<p>unassign</p>
						<Select
									value={selectedProject}
									onChange={setSelectedProject}
									options={projectOptions}
									isSearchable
								/>
							<Select
									value={selectedWorker}
									onChange={setSelectedWorker}
									options={workerOptions}
									isSearchable
								/>
						<button onClick={handleUnassign}>Confirm</button>
						<button onClick={handleUnassCancel}>Cancel</button>
						</div>
					</div>
			)}
			{showAssignModal && (
				<div className='modal'>
					<div className="modal-content">
						<p>assign</p>
						<Select
									value={selectedProject}
									onChange={setSelectedProject}
									options={projectOptions}
									isSearchable
						/>
						<Select
								value={selectedWorker}
								onChange={setSelectedWorker}
								options={workerOptions}
								isSearchable
						/>
						<button onClick={handleAssign}>Confirm</button>
						<button onClick={handleAssCancel}>Cancel</button>
						</div>
					</div>
			)}
				{isModalOpen && (
					<div className='modal'>
						<div className='modal-content'>
							<h2>Create a new Worker:</h2>
							<div className='input-container'>
								<label htmlFor='workerName'>Worker Name:</label>
								<input id="workerName" type='text' onChange={handleWorkerName}/>
							</div>
							<div className='input-container'>
								<label htmlFor='qualifications'>Qualifications:</label>
								<Select
									value={selectedOptions}
									onChange={setSelectedOptions}
									options={options}
									isMulti
									isSearchable
								/>
							</div>
							<div className='input-container'>
								<label id='salaryLabel' htmlFor='salary'>Salary:</label>
								<input id="salary" type="number" min={1} onChange={handleSalary}/>
							</div>
							<button onClick={handleSubmitClick}>Submit</button>
							<button onClick={() => setIsModalOpen(false)}>Close</button>
						</div>
					</div>
				)}
                <ClickList active={active} list={workers} item={Worker} path='/workers' id='name' />
        </div>
    )
}

export default Workers