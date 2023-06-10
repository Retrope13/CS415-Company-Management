# Use Cases

## View Company Qualifications

Summary: A user can view all qualifications within a company

Actor: Company Manager

Precondition: User is on company homepage

Postcondition: Table of qualifications is visible

### Description:
1. Click on the Qualifications tab
2. Client requests all qualifications
3. System returns list of all qualifications
4. System displays list of qualifications

## View Employed Workers

Summary: A user can view all workers employed in the company

Actor: Company Manager

Precondition: User is on company homepage

Postcondition: Table of workers is visible

### Description:
1. Click on the Workers tab
2. Client requests list of all workers
3. System returns a list of all workers
4. System displays list of workers

## View Projects

Summary: A user can view all projects within a company

Actor: Company Manager

Precondition: User is on company homepage

Postcondition: Table of projects is visible

### Description:
1. Click on the Projects tab
2. Client requests a list of all projects
3. System returns list of all projects
2. System displays list of projects

## View Qualification Details

Summary: A user can view the description and set of workers for a selected a qualification

Actor: Company Manager

Precondition: User is on the Qualification page

Postcondition: Drop down menu for a selected qualification is visible

### Description:
1. Click on a qualification from the list
2. System displays details of the single relevant qualification

## View Worker Details

Summary: A user can view the name, salary, workload, assigned projects, and qualifications of a selected worker

Actor: Company Manager

Precondition: User is on Worker page

Postcondition: Drop down menu for a selected worker is visible

### Description:
1. Click on a worker from the list
2. System displays details of the single relevant worker

## View Project Details

Summary: A user can the name, status, size, assigned employees, required qualifications and missing qualifications

Actor: Company Manager

Precondition: User is on Project page

Postcondition: Drop down menu for a selected project is visible

### Description:
1. Click on a project from the list
2. System displays details of the single relevant project

## Create a New Qualification

Summary: A user can press a button and enter qualification information to create a new qualification

Actor: Company Manager

Precondition: User is on the Qualification page

Postcondition: A new user specified qualification is present in the qualification table and on the server

### Description:
1. User clicks the create qualification button
2. System opens modal
3. Client selects input box and enters a description
4. Client clicks the submit button
5. System on Client side sends create qualification API call
6. System returns OK
7. Client Refreshes the page
8. System shows new qualification

## Create a New Worker

Summary: A user can press a button and enter worker information to create a new worker

Actor: Company Manager

Precondition: User is on the Worker page

Postcondition: The new worker is present in the worker table and on the server

### Description:
1. User clicks on the 'Create a new worker' button
2. System displays worker creation modal
3. User selects the Worker Name text input box and enter a string
4. User selects one or more qualifications from the drop down menu
5. User selects the Salary text input box and enters an integer
6. User selects the submit button.
7. System sends API request to create worker on the server
8. The server creates a new worker object
9. The server sends the new list of workers to the client 
10. The new worker is now displayed in the workers tab

## Create a New Project

Summary: A user can press a button and enter project information to create a new project

Actor: Company Manager

Precondition: User is on the Project page

Postcondition: The new project is in the project table and on the server

### Description:
1. User clicks on the ‘Create a new project’ button
2. System displays project creation modal
3. Then they select the Project Name text input box and enter a string
4. User selects the Qualification drop down and one or more options
5. User clicks a size button to choose size of project
6. User submits the request
7. System sends API request to create project on the server
8. The server creates a new project object
9. The server sends the new list of projects to the client
10. New Project is reflected in the project table.


## Start Project

Summary: A user can view all qualifications within a company

Actor: Company Manager

Precondition: ATM is displaying the idle welcome message

Postcondition: The selected project has its status set to active on the server

### Description:
1. User clicks on a planned or suspended project element in the list
2. The System displays the project details and start button for the project
3. The user clicks the start button
4. The start project API request is sent to the server
5. The server changes the project’s status to active
6. Start button disappears and the finish button appears in project details

## Finish Project

Summary: A user can view all qualifications within a company

Actor: Company Manager

Precondition: User is on the Projects page

Postcondition: The selected project has its status set to finished on the server

### Description:
1. User clicks on an active project element in the list
2. The System displays the project details and finish button for the project
3. The user clicks the finish button
4. The finish project API request is sent to the server
5. The server changes the project’s status to finished


## Assign Worker

Summary: a user can assign a worker who is available and qualified to a project

Actor: Company Manger

Precondition: User is on the Workers page

Postcondition: The selected worker has been added to the project

### Description
1. User clicks button titled "Assign Worker to Project"
2. User uses the two lists to select a project and a worker to assign to said project
3. User clicks the confirm button
4. The assign API is sent to the server
5. The server adds the worker to the project

## Unassign Worker
Summary: User can unassign a worker who is currently working on the project

Actor: Company Manager

Precondition: User is on the Workers page, the worker is currently assigned to the project

Postcondition: The worker is no longer assigned to the project

### Description
1. User clicks button titled "Unassign Worker from Project"
2. User uses the two drop downs to select a project and a worker to unassign from that project
3. User clicks the confirm button
4. The unassign API is sent to the server
5. The server removes the worker to the project
