# Compose Spreadsheet
Compose Spreadsheet is a small GUI tool for operating with spreadsheets made with Compose for Desktop.

# How to run it ?
To build and run this program, you need to have [gradle](https://gradle.org/install/) installed on your computer alongside 
with the [JDK](https://adoptopenjdk.net) version 8 or higher.
Then, you need to execute the gradle command "./gradlew runDistributable" on the root directory of the project to build 
the project and execute it at the same time.

Alternatively, you can run the gradle command "./gradlew createDistributable". This will build the project and create a
runnable executable file named ComposeSheet.exe in the directory located in _build\compose\binaries\main\app\ComposeSheet\Compose Spreadsheet_

If you use IntelliJ IDEA, those run configurations are available directly on the run configuration dropdown at the top left of the IDEA.

# Functionalities
Compose Spreadsheet UI is separated in two components. Above the input pane allow the user to write a formula for the current selected cell
and the grid below contains all the cell. To select a cell, you just have to click on it.

A cell can contain a formula that can be any correct combination of these:
- A number value (ex: `2` or `2.0`);
- A reference to another cell (ex: `A4` or `b5`);
- An arithmetic expression (ex: `2+7.38^8+(42*7.6-2)`);
- An operation on a range of cells (ex: `A1*2` or `B6*A5`).

Once a cell is unselected, it shows the calculated result. If the result cannot be evaluated, the content of cells shows
an error token.
There is 4 kinds of errors:
- _REC_ &rarr; Recursion error: A cell reference is recursive. Ex: `A1` is written in the cell A1
- _SYNTAX_ &rarr; Syntax error: Invalid character or invalid formula
- _MATH_ &rarr; Math error: Division by 0 or your result is too big to be calculated (overflow)
- _ERROR_ &rarr; Every other error / unknown error

**Additional functionality:**
You can load or save the sheet as json file. To do that, you can go on the file menu or use the shortcuts **CTRL+S** to
export the Spreadsheets and **CTRL+O** to import a json file.
