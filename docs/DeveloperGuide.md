---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in the [diagrams](https://github.com/se-edu/addressbook-level3/tree/master/docs/diagrams/) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** has two classes called [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.


**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

How the `Logic` component works:
1. When `Logic` is called upon to execute a command, it uses the `AddressBookParser` class to parse the user command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `AddCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to add a person).
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

The Sequence Diagram below illustrates the interactions within the `Logic` component for the `execute("delete 1")` API call.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in json format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

## Add Feature

### Implementation Details

The implementation of the `add` command involves creating a new `Student` object and storing it in `AddressBook`. <br>

Given below is a class diagram on the `Student` class and the classes related to its attributes: <br>

![student_diagram](images/StudentClassDiagram.png) 

The `Student` object is composed of attributes:

* `Name`: The name of the student.
* `Phone`: The phone number of the student.
* `Email`: The email address of the student.
* `Address`: The address of the student.
* `Education`: The education level of the student.
* `Subject`: The subjects the tutor is teaching the student.
* `Remark`: Remarks/notes the tutor has about the student.

### Proposed Implementation
The `add` command has the following fields:
> NOTE : `[COMPULSORY]` indicates that the field is cannot be omitted when using `add`. 
> Unless stated as`[COMPULSORY]`, the field is optional.
* Prefix `\n` followed by the name of the student `[COMPULSORY]`.
* Prefix `\p` followed by the phone number of the student.
* Prefix `\e` followed by the student's email.
* Prefix `\a` followed by the student's address.
* Prefix `\edu` followed by the student's education level.
* Prefix `\s` followed by the subject name.
* Prefix `\r` followed by the remarks/notes on the student.

Here is a sequence diagram showing the interactions between components when `add n/Alice edu/Primary 6` is run.: <br>

![add_sequence](images/AddSequenceDiagram.png)

### Feature details
1. The app will validate the parameters supplied by the user with pre-determined formats for each attribute.
2. If an input fails the validation check, an error message is provided which details the error and prompts the user for a corrected input.
3. If the input passes the validation check, a new `Student` entry is created and stored in the `AddressBook`.

### General Design Considerations

The implementation of the attributes of a `Student` is very similar to that of a `Person` in the original AB3 codebase. 
Hence, resulting in a similar implementation of the `add` feature. </br>

Some additions made were the `Education`, `Subject` and `Remark` attributes. </br>
1. `Education` is implemented similar to the other attributes like `Address`, but is modified to fit the logic that a student can only have one education level.
2. `Subject` is implemented in a similar way to `Tags` in AB3 but has been modified to accomodate subject names that are more than one word long as in real life.
3. Every attribute except`Name` has been made **OPTIONAL** to accomodate circumstances where some student's details are unknown at the time of entry.
    * We utilised the [java.util.Optional<T>](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html "java.util.Optional<T>") class to encapsulate the optional logic of the attributes.

When adding a student entry, these were the alternatives considered.
* **Alternative 1 (current choice):** Only `Name` has to be specified to create a `Student` entry, making the other attributes `Optional<>`.
    * Pros:
        * Improves user convenience by allowing them to add a `Student` entry even with limited knowledge about their details.
    * Cons:
        * A lot of modification for empty/*null* inputs have to be accounted for when saving the data and testing.
* **Alternative 2:** All parameters have to be filled in
    * Pros:
        * Easier to implement as there is lesser room for errors when dealing with empty/*null* inputs
    * Cons:
        * `add` becomes a lengthy command to execute as unnecessary additional time is needed to enter dummy values to meet the input requirements.
        * Reduces user convenience as "useful" entries that can be made are limited to students whose details are all known.

## Delete feature

### Implementation Details

The `delete` implementation is identical to the implementation in AB3's codebase.

### Proposed Implementation
The proposed `delete` implementation supports deleting multiple `Student` entries at once. For example `delete 1 3 5` will delete the `Student` entries at indexes 1, 3 and 5 in the  `AddressBook` 
> Assuming the indexes are valid.

// TODO sequence diagram

### Design Considerations
// TODO

## Edit Feature

### Implementation Details

The implementation of `edit` involves creating a new `Student` object with updated details to replace the previous `Student` object.
This is done with the help of the `EditPersonDescriptor` class, which helps create the new `Student` object.

With a similar fields to the [Add feature](#add-feature), `edit` has an additional `INDEX` parameter. </br>
> NOTE : `[COMPULSORY]` indicates that the field is cannot be omitted when using `add`.
> Unless stated as`[COMPULSORY]`, the field is optional.
* `INDEX` which represents the index number of the student to be edited in the list.
* Prefix `\n` followed by the name of the student.
* Prefix `\p` followed by the phone number of the student.
* Prefix `\e` followed by the student's email.
* Prefix `\a` followed by the student's address.
* Prefix `\edu` followed by the student's education level.
* Prefix `\s` followed by the subject name.
* \[To be implemented\] Prefix `\r` followed by the remarks/notes on the student.

Here is a sequence diagram showing the interactions between components when `edit 1 n/Bob edu/Primary 5` is run.: <br>

![edit_sequence](images/EditSequenceDiagram.png)

### Feature details
1. Similar to `add`, the app will validate the parameters supplied by the user with pre-determined formats for each attribute.
2. If an input fails the validation check, an error message is provided which details the error and prompts the user for a corrected input.
3. If the input passes the validation check, the corresponding `Student` is replaced by a new edited `Student` object and stored in the `AddressBook`.

### General Design Considerations
When editing a student entry, whether a new `Student` object should be created.
* **Alternative 1 (Current choice):** `edit` will create a new `Student` object with the help of `EditPersonDescriptor`
    * Pros:
        * Meets the expectations of the immutable `Student` class.
    * Cons:
        * Inefficient as an entire `Student` object is created even if only one field is changed. </br>

* **Alternative 2:** `edit` directly sets the updated values in the existing `Student` object directly.
    * Pros:
        * More timely option and space efficient.
    * Cons:
        * In order to execute this, `Student` cannot be immutable, this reduces the defensiveness of the program, making it more susceptible to errors.


## Find feature

### Implementation Details

The proposed `find` feature is implemented using `MultiFieldContainsKeywordsPredicate` and `NameContainsKeywordsPredicate`. <br>

Both of which implement the `Predicate<Person>` interface where the `test` method checks whether the data in the relevant field of a `Student` contains the specified keyword.

Here is a sequence diagram showing the interactions between components when `find Alice` is run.: <br>
// TODO sequence diagram

### Feature details
Our implementation extends from the `find` implementation in AB3 by accommodating `find PARTIAL_KEYWORD` on top of the current `find KEYWORD`.
> An example of `PARTIAL_KEYWORD` is "Ye" while `KEYWORD` would be "Yeoh". <br>

### General Design Considerations
THe implementation of `find` is built on top of the original AB3 codebase's `find` command. <br>

Our implementation has some additions such as:
1. Allowing `PARTIAL_KEYWORD` finds so that we can accommodate for the real-life scenarios where users are not certain of the full `KEYWORD` to input for `find`.
2. `find PREFIX` across the various attributes of a `Student` other than their `Name`

**Aspect: Command format:**
* **Alternative 1 (Current choice):** `find PREFIX KEYWORD/PARTIAL_KEYWORD`
  * Pros:
    * Improves user convenience by giving them flexibility in the completeness of their desired find keyword.
    * Extensible across other attributes.
  * Cons:
    * Adds complexity to the implementation as this implementation introduces a lot of potential errors in parsing the user's input.
    * Might be slightly challenging for new users to enter the `PREFIX`.
* **Alternative 2:** `find KEYWORD/PARTIAL_KEYWORD`
  * Pros:
    * Easier to implement as there is lesser validating done by the app.
    * Provides the user flexibility in searching across all attributes by default.
  * Cons:
    * The filtered list may not be what was desired as short partial keywords like `a` is unlikely to result in a succinct list.
    * Users will not be able to search keywords for a particular attribute.

## List feature

### Implementation Details
The `list` implementation is identical to the implementation in AB3's codebase.
// TODO sequence diagram
### Design Consideration
// TODO
### \[Proposed\] Sort feature

### Proposed Implementation

The proposed `sort` implementation will sort the `UniquePersonList` object, hence it will make use of: <br>
* `sort` in [javafx.collections.FXCollections](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/FXCollections.html) for the main sorting functionality.
  * In order to sort by `Name`, the comparator will be as follows `Comparator<Name>`.
* `comparing` in [java.util.Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html) class to execute `sort` in ascending and descending orders. <br>

An example usage would be `sort ASC` to sort the list in ascending order, and `sort DESC` to sort the list in descending order.
> `ASC` and `DESC` will not be case-sensitive, in other words, `sort ASC` and `sort asc` are both acceptable commands.

#### Exepected execution:
1. Upon entering the command `sort ASC` in the command line of the application, the list of students will be sorted in alphabetically ascending order of their `Name`.
2. Upon entering the command `sort DESC` in the command line of the application , the list of students will be sorted in alphabetically descending order of their `Name`.

### Design Considerations:
**Aspect: Command format:**
* **Alternative 1:** `sort`
  * Pros:
    * Simpler command for users to execute
  * Cons:
      * Less flexible as users cannot decide which attribute to sort by.
      * Reduces extensibility of the feature (eg. sort by subject tag is more complicated if `sort` doesn't accept inputs)
      * Users cannot choose which order to sort in as it will be defaulted to sorting in ascending order.
* **Alternative 2 (Current choice):** `sort ORDER`
  * Pros:
    * Provides extensibility of sort (eg. future implementation of `sort edu ORDER` to sort by education level)
    * Allows users to choose the order they would like to sort the list by
    * Gives flexibility and convenience to users.
  * Cons:
    * Adds complexity to the implementation as more error checking of the inputs is required.
  
_{more aspects to be added}_

## Remark feature

#### Feature Implementation Details
The current implementation provides users with two different methods of entering a remark for a student.
1. `remark INDEX r/REMARK` where `INDEX` is the `Student` entry in the list, and `REMARK` is the remark to be added.
2. Adding the remark through the [add feature](#Add-feature)

#### Proposed Implementation

The proposed remark mechanism will be facilitated by a pop-up text box. This will allows users to format their remarks however they like, 
rather than being restricted to a single line in the command line (current implementation).

### General Design Considerations
In order to make this feature as versatile as possible, the `remark` feature should consider formatted inputs (eg. new lines to separate paragraphs). <br>
Additionally, the command line only provides a restricted view and input option for users, hence it does not support formatted remarks.

**Aspect: Command input format**
* **Alternative 1: (Current implementation)** Adding the `remark` through the command line.
  * Pros:
    * Easier to implement
  * Cons:
    * Restricts users to a single line or continuous paragraph of remark.
    * Limits formatting options for remark.
* **Alternative 2: (Future implementation)** Adding remark through a pop-up text window
  * Pros:
    * Provides users flexibility in the format of their remarks.
    * Remarks are not restricted to a single line or continuous paragraph.
  * Cons:
    * More complicated to implement as the format of the remarks have to be saved and loaded into `AddressBook` without any formatting erros.

**Aspect: Remark display**
* **Alternative 1: (Current choice)** Preview the first line of a student's remarks under all the other attributes
  * Pros:
    * Short remarks are instantly visible to users.
    * Easy to implement.
  * Cons:
    * Remarks are limited to a single line as long as the width of the window.
    * Formatting of remarks are not visible.
* **Alternative 2: (Future implementation)** If a remark is present, simply display an indicator in `PersonCard`
  * Pros:
    * Easy to implement.
    * Viewing the remark in `ResultDisplay` is supported by the [show](#show-feature) command.
    * Supports formatting of `remark` since it is not restricted to the `PersonCard` view.
  * Cons:
    * An extra step for users may be inconvenient
* **Alternative 3:** Show the full remark in `PersonCard` beside all the other attributes
  * Pros:
    * Remark is directly visible from the list.
    * Supports formatting in `remark`.
  * Cons:
    * Remarks are limited to the view of `PersonCard` and size of the window.
    * Remarks that are too long will be cut off and not visible.

### Show feature

### Implementation Details
The implementation of `show` is similar to the `list` command in the AB3 codebase. The `show` feature was implemented to support the `remark` feature. <br>
Remarks longer than the width of `PersonListCard` in `PersonListPanel` 
will not be visible. Hence, `show` allows users to view the full remark in the `ResultDisplay` since scrolling is supported.

### General Design Considerations
**Aspect: Display output**
* **Alternative 1: (Future implementation)** Display the entire `PersonCard` of the student chosen in `PersonListPanel`.
  * Pros:
    * Allows users to view the student details and remarks all at once.
    * Supports the `remark` feature as intended
  * Cons:
    * May reduce user convenience as `show INDEX` will likely always be followed with the `list` command to toggle back to the full list of students.
    * Harder to implement as the size of the `PersonCard` for the `Student` has to be updated everytime `show` is executed.
  
* **Alternative 2: (Current choice)** Display the entire `PersonCard` of the student chosen in the `ResultDisplay`
  * Pros:
    * Supports the `remark` feature as intended since scrolling is possible.
    * Allows users to view the student details and remarks all at once.
  * Cons:
    * Harder to implement
  
### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how the undo operation works:

![UndoSequenceDiagram](images/UndoSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Teaching Assistants (TAs)/tutors who have a class of students to manage and are preferably are proficient typers.

**Value proposition**: 

* TeachMeSenpai acts as an optimised app for tutors to manage their students' data, obtain insights on their students' data.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I want to …​                                                                                                | So that I can…​                                                             |
|----------|---------|-------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `* * *`  | tutor   | open the app                                                                                                | begin using the app                                                         |
| `* * *`  | tutor   | close the app                                                                                               | leave the app                                                               |
| `* * *`  | tutor   | add a student's name                                                                                        | track a student's progress by their name                                    |
| `* * *`  | tutor   | include student's education level when adding the student (eg. P6)                                          | keep track of a student's education level                                   |
| `* * *`  | tutor   | include student's phone number when adding the student (eg. 94206942)                                       | keep track of a student's phone number                                      |
| `* * *`  | tutor   | include student's email when adding the student (eg. iloveanimegirls@gmail.com)                             | keep track of a student's email                                             |
| `* * *`  | tutor   | include student's address when adding the student (eg. Block 69 S642069)                                    | keep track of a student's address and go to the place easily                |
| `* * *`  | tutor   | include the subjects I'm teaching a student to their entry (eg. Mathematics, English)                       | keep track of what subjects I'm teaching the student                        |
| `* * *`  | tutor   | include optional student-specific notes when adding the student (eg. Good in Japanese)                      | store information for a particular student such as notes and remarks        |
| `* * *`  | tutor   | delete a student entry from my list (by index)                                                              | remove all details related to a certain student                             |
| `* * *`  | tutor   | have my changes saved automatically                                                                         | be sure that I won't lose my changes if I crash/close the app               |
| `* * *`  | tutor   | view my list of students                                                                                    | keep track of who I'm currently teaching                                    |
| `* * *`  | tutor   | View the address of a student                                                                               | know where to go if I need to provide tuition at their house                |
| `* * *`  | tutor   | have my data persist between use sessions                                                                   | continue my session where I left off                                        |
| `* * *`  | tutor   | find my students by searching their names                                                                   | quickly view that student's details                                         |
| `* *`    | tutor   | filter my students by education level (eg. all P6 students)                                                 | view my students of the same education level                                |
| `* * *`  | tutor   | edit a student's name                                                                                       | correct a student's name                                                    |
| `* * *`  | tutor   | edit the subjects I'm teaching a particular student                                                         | update or correct a student's records                                       |
| `* * *`  | tutor   | edit a student's education level                                                                            | update or correct a student's records                                       |
| `* *`    | tutor   | filter my students by subjects                                                                              | view all the student's I'm teaching a particular subject to                 |
| `* *`    | tutor   | filter my students by address (eg. Ang Mo Kio)                                                              | view all the students who live in a particular area                         |
| `* *`    | tutor   | filter my students by email (eg. @gmail)                                                                    | view all the students with similar emails                                   |
| `* *`    | tutor   | sort my students by their names                                                                             | view my students in a systematic manner                                     |
| `* *`    | tutor   | sort my students by their education level                                                                   | view my students according to their education level                         | 
| `* * *`  | new user | receieve an appropriate and user-friendly error message when I enter the wrong inputs/parameters for a command | find out the correct input/parameter format and use the feature as intended |
| `* * *`  | new user | be able to ask for help                                                                                     | learn how to use the app                                                    |



### Use cases

(For all use cases below, the **System** is the `TeachMeSenpai` app and the **Actor** is the `user`, unless specified otherwise)

#### Use case: Delete a student

**MSS**

1.  User requests to list students

2.  System shows a list of students

3.  User requests to delete a specific student in the list by their index from the list

4.  System deletes the student

    Use case ends

**Extensions**

* 2a. The list is empty

  Use case ends

* 3a. The given index is invalid

    * 3a1. System shows an error message

      Use case resumes at step 2

#### Use case: UC02 - Update remarks

**MSS**
1. User requests to list students
2. System shows a list of students
3. User requests to edit a student's remarks of a specific student in the list by their index from the list
4. Program allows multi-line input of remarks
5. User enters remarks
6. User can exit writing the remarks at any time
7. System saves the remarks

Use case ends

**Extensions**

* 2a. The list is empty

Use case ends

* 3a. The given index is invalid
    * 3a1. System shows an error message

  Use case resumes at step 2

*{More to be added}*

### Non-Functional Requirements

1. A user that is completely new to the application should be able to be familiar with the functionalities within 1 hour.
2. System should respond within 0.1 second of the user providing an input.
3. All systems must be able to access the _save file_ ie. Save file should be independent of the OS.
4. Any information displayed should be concise and structured in a logical manner such that it is easily understandable.
5. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
6. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
7. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.


*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Save FIle**: The file containing all the data (ie. Entries of student information) inputted by the user saved locally on the user's own computer.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
