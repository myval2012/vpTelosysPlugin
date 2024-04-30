# Visual Paradigm Telosys Plugin
Generate Telosys project from a Telosys Diagram. 

## Why?
Telosys is a powerful lightweight code generator. Telosys DSL is however only textual and lacks some of the clarity 
that diagrams provide. This project is trying to solve this issue. Designed Telosys diagram represents all constructs 
that can be made in Telosys DSL. Plugin compiles the Visual Paradigm project with its models, packages and classes into 
a Telosys project.

## Project setup
For other developers who want to compile this repository, here is a quick tutorial:
1. Clone the project repository.
2. In folder `dependency`, there is an archive with `telosys-tools-all-x.x.x.jar`. if you want to use other version. 
Place it here and modify the `build.gradle.kts` build script.
3. Find `openapi.jar` library in your Visual Paradigm distribution (in `./Visual Paradigm xx.x/lib/`) and:
   * Copy it into the `dependency` folder or
   * modify the `build.gradle.kts`
4. Run compilation using `gradle build zip`

Compilation creates a zip archive in `build` that can be installed in Visual Paradigm.

## Installation
Download the latest release version of `telosysPlugin.zip` follow [Visual Paradigm Installing from a zip of plugin](https://www.visual-paradigm.com/support/documents/vpuserguide/124/254/7041_installingpl.html) 
for further instructions. 

After a successful installation it is recommended to create own language and data types 
in Visual Paradigm. Plugin compares string names of data types. All data types must be lowercase just like in Telosys 
DSL. Follow [Data type options](https://www.visual-paradigm.com/support/documents/vpuserguide/2270/2276/59851_datatype.html) 
for instructions on how to create custom language in Visual Paradigm.  

## Usage
### Creating new project
1. Create a new project via project->new
2. Choose the correct `data type set` of the project
![Create new project](/doc/imgs/demoProjectCreateNew.png)
3. Save the project (configuration will not have default values otherwise)
4. Initialize the projects metamodel using `Init Project` action in plugin menu.
![Init project](/doc/imgs/demoProjectInit.png)
If successful a metamodel `Telosys DSL defs` will appear in model hierarchy. Logging messages will be printed after.
![Successful init](/doc/imgs/demoProjectSuccessfulInit.png)
### Demo project
1. In project root create a model (each model represents one Telosys model).
2. In this model create a class diagram (as many as you want).
3. Draw classes, packages.
![Packages And Classes](/doc/imgs/demoProjectPackagesAndClasses.png)
4. Define links using association. Use context actions to make the association definition faster.
![Context Actions](/doc/imgs/demoProjectContextActions.png)
5. You can verify the created models using the `Verify Models`
6. After creating valid model specify where you want the Telosys project to be generated. Configuration 
is located in the metamodel `"Telosys DSL defs"/config`
![Complete demo model](/doc/imgs/demoProjectValidModel.png)
![Config](/doc/imgs/demoProjectConfig.png)
7. Use generate action to generate telosys project

Show how the plugin works (project init, build, setting variables...).
### Graphical Telosys DSL
Basic overview of Telosys diagram components can be accessed [here](doc/overview.md). <br>
Overview of all plugin actions can be found [here](doc/actions.md) <br>
Detailed description of the representation of all annotations can be found [here](doc/annotations.md). <br>

## LICENSE
This project is under [GNU LESSER GENERAL PUBLIC LICENSE Version 3](COPYING.LESSER)

## NOTICE
This project contains [Telosys](https://github.com/telosys-tools-bricks) by [Laurent Guerin](https://github.com/l-gu). <br>
This project uses [Visual Paradigm's](https://www.visual-paradigm.com/) openapi. <br>
Special thanks to [Peter Wong](https://forums.visual-paradigm.com/u/peter.wong/summary) for a [project configuration problem solution](https://forums.visual-paradigm.com/t/how-to-stor-config-info-for-plugin/11772/4?u=ondrej_vrana). <br>

Plugin was written with help of the Visual Paradigm [Plugin development guide](https://www.visual-paradigm.com/support/documents/vpuserguide/124/254/7039_introduction.html),
[sample projects](https://www.visual-paradigm.com/support/documents/pluginsample.jsp), 
and other [official openapi guides](https://knowhow.visual-paradigm.com/openapi/).