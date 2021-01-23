# FlightGearSimulatorApp

This is a project done in Advanced Software Programming course in java, its purpose is to control the flightGear flight simulator with an external desktop application.

In order to run this project, you will need to:
- install FlightGear
- Add the generic_small.xml file (that will be in the resources folder) to "\FlightGear (your-version-number)\data\Protocol" folder.
- Change the default location Runway to "Honolulu Intl" under the tab location.
- I would also recommend changing the default "time of day" under the environment tab to Noon so you wouldn’t start in the dark.
- Under the settings tab you will need to add the following text in the "Additional Settings": --telnet=socket,in,10,127.0.0.1,5402,tcp --                             generic=socket,out,10,127.0.0.1,5400,tcp,generic_small

-Now press Fly!
-run the SolverServer
-Run FightSimCon
-The Server is running under Port 4343
- the flightGear is running on Port 5402

