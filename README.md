# Debugger for Event-Driven Systems
This project contains an Eclipse project to debug event-based systems. Event-based systems are systems that adhere to the Event-Driven Architecture (EDA).

## Project
To install the debugger, you need to have a **Debugger Back-End** that transforms the domain events into debuggable events for the **front-end**. This repository contains

* *The Eclipse front-end debugger for JDT based projects (Java Projects)*. This contains GUI and such for the debugger.
* *Core*. This contains the core system module that interacts between back- and front-end.

## Installation

### Repository into Workspace
If you wish to contribute, clone the repository into a workspace. Because of the architecture, the back-end must be *in a dedicated workspace*, not in the same! That is, when you test the system, then

- Have this repository in one workspace
- Run the project as a Eclipse application
- In the sub-instance, have a project "**B**" that contains an appropriate back-end module.
- Then, run **B** in a dedicated instance.

This means that it results in 2 Instances of Eclipse and a debuggee instance, making it 3 instances in total.

### Install from Software Site
TODO

### Install from Zip