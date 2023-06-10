import { useEffect, useRef, useState } from 'react'
import ClickList from '../components/ClickList'
import { createProject, getProjects, getQualifications, startProject, finishProject } from '../services/dataService'
import LocationID from '../utils/location'
import { darkGrayContainerStyle, grayContainerStyle, pageStyle } from '../utils/styles'
import '../style.css'
import Select from 'react-select';

const Project = (project, active) => {
    return (
        <div>
            <div>{project.name}</div>
            {active === true ? ProjectBody(project) : null}
        </div>
    )
}

const ProjectBody = (project) => {
	let isActive = project.status === "ACTIVE";
	let isPlanned = project.status === "PLANNED";
	let isSuspended = project.status === "SUSPENDED";
	if (!project.missingqualifications) {
		project.missingqualifications = [];
	}

	const handleStartButton = async () => {
		if (project.missingqualifications.length === 0) {
			startProject(project.name, project.size, project.status, project.workers, project.qualifications, project.missingqualifications);
			project.status = "ACTIVE";
		} else {
			alert("Cannot start project if there are missing qualifications");
		}
	}

	const handleFinishButton = async () => {
		finishProject(project.name, project.size, project.status, project.workers, project.qualifications, project.missingqualifications);
		project.status = "FINISHED"; 
	} 

    return (
		<div id="parent" class="flex-container">
		    <div id="left" class="flex-child" style={grayContainerStyle}>
			    <div id="workers">
		            Assigned Employees: <ClickList list={project.workers} styles={darkGrayContainerStyle} path="/workers" />
		        </div>
		    </div>
		    <div id="center" class="flex-child" style={grayContainerStyle}>
		        <div id="size">
		        	Project Size: {project.size}
		        </div>
		        <div id="status">
		    		Project Status: {project.status}
		    	</div>
		    </div>
	    	<div id="right" class="flex-child" style={grayContainerStyle}>
	        	<div id="required qualifications" class="satisfied">
		    		Required Qualifications: {project.qualifications}
		        </div>
		        <div id="missing qualifications" class="missing">
		        	Missing Qualifications: {project.missingqualifications}
		        </div>
		    </div>
			<div id="statusButton">
				{isActive ? (<button className='finish' onClick={handleFinishButton}>FINISH</button>)
				: (<></>) }
				{isPlanned || isSuspended ? (<button className='start' onClick={handleStartButton}>START</button>)
				:(<></>)}
			</div>
	    </div>
    )
}

const Projects = () => {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [projectName, setProjectName] = useState(null);
	const [qualifications, setQualifications] = useState([]);
	const [selectedOptions, setSelectedOptions] = useState([]);
	const [selectedSize, setSelectedSize] = useState("");
	const [projects, setProjects] = useState([]);
	const MyError = useRef(null);
	useEffect(() => { getProjects().then(setProjects) }, [])
	useEffect(() => { getQualifications().then(setQualifications) }, [])

	const options = qualifications.map((option) => ({
		value: option, 
		label: option.description
	})); 

	const sizes = ["SMALL", "MEDIUM", "BIG"]
	const sizeOptions = sizes.map((option) => ({
		label: option
	}));

	const handleSubmitClick = async () => {
		if (projectName.trim() === "") {
			MyError.current.textContent = "You must use a readable name";
		} else {
			MyError.current.textContent = "A project with that name has already been created";
			await createProject(projectName, selectedSize.label, selectedOptions.map(e => e.label));
			setIsModalOpen(false);
			setSelectedOptions([]);
			window.location.reload(true);
		}
		
	}

	const handleProjectName = (event) => {
		setProjectName(event.target.value);  
	}

	const active = LocationID('projects', projects, 'name')
    return (
        <div style={pageStyle}>
            <h1>
                This page displays a table containing all the projects.
            </h1>
				<button onClick={() => setIsModalOpen(true)}>Create a new project.</button>
				{isModalOpen && (
					<div className='modal'>
						<div className='modal-content'>
							<p id="errorText" ref={MyError}></p>
							<h2>Create a new Project:</h2>
							<div className='input-container'>
								<label htmlFor='projectName'>Project Name:</label>
								<input id="projectName" type='text' onChange={handleProjectName}/>
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
								<label id='projectSizeLabel' htmlFor='projectSize'>Project Size:</label>
								<Select
									value={selectedSize}
									onChange={setSelectedSize}
									options={sizeOptions}
								/>
							</div>
							<button onClick={handleSubmitClick}>Submit</button>
							<button onClick={() => setIsModalOpen(false)}>Close</button>
						</div>
					</div>
				)}
                <ClickList active={active} list={projects} item={Project} path='/projects' id='name' />
        </div>
    )
}

export default Projects