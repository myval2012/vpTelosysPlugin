# Telosys Diagram
This representation is based on UML Class Diagram. The following sections describe how each Telosys DSL element is modeled.

### Model and Entity
Telosys model is represented by UML model in the root of Visual Paradigm project. UML model can contain packages 
(for @Package) and classes (represents entities). Each package can contain packages and classes.

### Attribute
Telosys attribute is represented by class attribute. Attribute type has to be one of the neutral types 

![Attribute example](imgs/attributeExample.png)

*Attribute example*

### Link
Links are the most complicated part of Telosys diagram. The aim is to make relations between 
objects clearer and more graphical. For this reason Telosys link is represented with association, 
association class and representative attribute.

The association serves as a base for the relationship. Representative attribute is created by assigning ownership of
association end to class instead of association. Visual paradigm automatically generates representative attribute in
opposing class.

![link, one representative attribute](imgs/linkOneReprAttr.png)

*One representative attribute. OneToMany / ManyToOne*

![link, two representative attributes](imgs/linkTwoReprAttr.png)

*Both representative attributes. ManyToMany*

Multiplicity describes whether link references a single entity or a collection. 
Association class is used to specify join entity (if the association is ManyToMany). 

![One-To-One relationship](imgs/oneToOne.png)

*OneToOne*

There are multiple ways of connecting representative attribute and FK attributes in Telosys.
<ul>
    <li>Inference - There are no controls, but it is possible.</li>
    <li>@LinkByAttr - Stereotype.</li>
    <li>@LinkByFK - Stereotype.</li>
    <li>@LinkByJoinEntity - Automatically generated.</li>
</ul>
Additionally in OneToOne and OneToMany, these FK attributes can be added to association class of relationship. 
Plugin then automatically adds these to on of the entities depending on the direction. 
Direction is used to describe owning and inverse side of the relationship (owning -> inverse).

### Annotation 
Most of the annotations are represented as stereotypes, some also as constraints and other as their UML counterparts.
The list of all annotations and their representations can be found [here](annotations.md).

### Tag
User has to define a stereotype that begins with '#'. Parameter is added as a tagged value to this stereotype 
(value type has to be 'Text').