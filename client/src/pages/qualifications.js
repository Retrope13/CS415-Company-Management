import { useEffect, useState } from 'react'
import ClickList from '../components/ClickList'
import { getQualifications, getWorkers } from '../services/dataService'
import { createQualification } from '../services/dataService'
import { getWorker } from '../services/dataService'
import LocationID from '../utils/location'
import { darkGrayContainerStyle, grayContainerStyle, pageStyle } from '../utils/styles'
import Select from 'react-select';

const Qualification = (qualification, active) => {
    return (
        <div>
            <div>{qualification.description}</div>
            {active === true ? QualificationBody(qualification) : null}
        </div>
    )
}

const QualificationBody = (qualification) => {
    // const [workerName, setWorkerName] = useState('');
    // const [workers, setWorkers] = useState([]);
    // useEffect(() => { getWorkers().then(setWorkers) }, [])


    // const handleWorkerName = (event) => {
	// 	setWorkerName(event.target.value);
	// }

    // const handleChange = (event) => {
    //     console.log(event.target.value);
    // } 
    
    return (
        <div id="parent" class="flex-container">
            <div id="left" class="flex-child" style={grayContainerStyle}>
                Workers: <ClickList list={qualification.workers} styles={darkGrayContainerStyle} path="/workers" />
            </div>
            {/* <div className='input-container'>
                <label htmlFor='workerName'>Worker Name:</label>
                <input id="workerName" type='text' onChange={handleChange}/>
			</div>
            <div id="addWorkerButton">
                {<button className='addWorker'>Add Worker</button>}
            </div> */}
    </div>
    )
}

const Qualifications = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
	const [qualificationName, setQualificationName] = useState(null);
    const [qualifications, setQualifications] = useState([])
    useEffect(() => { getQualifications().then(setQualifications) }, [])
    const active = LocationID('qualifications', qualifications, 'description')

	const handleSubmitClick = async () => {
		createQualification(qualificationName);
		setIsModalOpen(false); 
	}

	const handleQualificationName = (event) => {
		setQualificationName(event.target.value);
	}

    return (
        <div style={pageStyle}>
            <h1>
                This page displays a table containing all the qualifications.
            </h1>
            <button onClick={() => setIsModalOpen(true)}>Create a new qualification.</button>
				{isModalOpen && (
					<div className='modal'>
						<div className='modal-content'>
							<h2>Create a new Qualification:</h2>
							<div className='input-container'>
								<label htmlFor='qualificationName'>Qualification Name:</label>
								<input id="qualificationName" type='text' onChange={handleQualificationName}/>
							</div>
							<button onClick={handleSubmitClick}>Submit</button>
							<button onClick={() => setIsModalOpen(false)}>Close</button>
						</div>
					</div>
				)}
            <ClickList active={active} list={qualifications} item={Qualification} path='/qualifications' id='description' />
        </div>
    )
}

export default Qualifications