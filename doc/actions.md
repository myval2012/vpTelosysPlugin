# Plugin Actions
All actions in plugin are listed below
## Menu Actions
These actions can be found in the `plugin` menu.
#### Init Project
Creates / repairs Telosys metamodel. Project needs to be saved for this to work fully. If the project is not saved 
before using this action the project variables in `"Telosys DSL defs"/config` won't have initial values 
(Values can be added manually). 
#### Verify Models
Checks created models. Doesn't create any files.
#### Generate Models
Compiles all models. Creates / modifies Telosys project in given directory 
(`"Telosys DSL defs"/config/"Telosys project directory"`), given path should be absolute. If project already exists 
and contains models with entities that have the same names as in the compiled Telosys diagrams, then the `.entity` 
models are rewritten.
## Context Actions
#### OneToOne
Association action. Sets multiplicity of both association ends to "0..1".
#### OneToMany
Association action. Sets end of association to either "0..1" or "0..&ast;" depending
on association direction. Owning side -> Inverse side, the owning side has "0..&ast;" multiplicity.
#### ManyToMany
Association action. Sets multiplicity of both association ends to "0..&ast;".
#### Create Links
Association action. Creates both links of the association (if they don't already exist). 
#### Create FK(s)
Association action. if association doesn't have a association class, creates association class attributes for FK 
parts and add @FK stereotype to them. If association class already exists, then this action does nothing. 
#### Create Links and FK(s)
Association action. Chain actions `Create Links` and `Create FK(s)` together. One click instead of two. 
