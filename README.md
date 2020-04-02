# LogicSim3

LogicSim is a simulation tool for digital circuits.
It has been programmed until 2009 by Andreas Tetzl.
http://www.tetzl.de/java_logic_simulator_de.html

The LogicSim2 project seems to be abandoned. 
In 2020 there was a reprogram by Peter Gabriel (pngabriel@gmail.com)
to enhance the program and get rid of old techniques (e.g. applets).

The code is nearly entirely new - a object hierarchy has been designed to make the program more expandable.

The old program used the serialization technique to persist circuits. This has been replaced by a XML-format to describe circuits and modules.
Unfortunately old files written with LogicSim2 cannot be used with LogicSim3.
