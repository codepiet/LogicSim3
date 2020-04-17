# LogicSim3

INTRODUCTION
LogicSim is a simulation tool for digital circuits.
It has been programmed until 2009 by Andreas Tetzl until version 2.4 and this version is hosted on http://www.tetzl.de/java_logic_simulator_de.html.
In 2020 LogicSim has been reprogrammed by Peter Gabriel (pngabriel@gmail.com) to enhance the program and get rid of old techniques (e.g. applets).

The code is nearly entirely new - a object hierarchy has been designed to make the program more expandable.

The old program used the serialization technique to persist circuits. This has been replaced by a XML-format to describe circuits and modules.
Unfortunately old files written with LogicSim2 cannot be used with LogicSim3.

STARTING
Double-click the jar-file for an automated start. On some Windows Systems Java is not configured correctly.
Please use the distributed logicsim.bat for a start. Javaw.exe must be included in your PATH environment variable. Normally this happens during installation of Java.

MANUAL
There is only very sparse documentation about this project.
Please experiment with the functions. There is a manual in the manual-subdirectory of the project which will be revised soon.

ISSUES
For issue tracking, there is a tracking system on github. It would be nice if you report a bug. 
An email to the author is also ok.