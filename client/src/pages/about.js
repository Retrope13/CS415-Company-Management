import { pageStyle } from '../utils/styles'

const About = () => {
    return (
        <div style={pageStyle}>
            <div style={{ 'text-align': 'center' }}>
                <h1>Company Management</h1>
                <br />
                <h3>
                    This is the software engineering project for the Spring 2023 CS415 Software Testing course at Colorado State University.
                    <br />
                    This web application allows to manage employees and projects within a company.
                </h3>
            </div>
        </div>
    )
}

export default About