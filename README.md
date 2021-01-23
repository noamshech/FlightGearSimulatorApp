# FlightGearSimulatorApp

This is a project done in Advanced Software Programming in java, its purpose is to control the flightGear flicht simulator with an external desktop apllication.

In order to run this project you will need to:
- install FlightGear
- Add the  generic_small.xml file (that will be in the resorces foder) to "\FlightGear (your-version-number)\data\Protocol" folder.
- Change the defult location Runway to "Honolulu Intl" under the tab location.
- I would also recommand to change the difult "time of day" under the environment tab to Noon so you wouldnt start in the dark.
- U nder the settings tav you will need to add the following text in the "Additional Settings": --telnet=socket,in,10,127.0.0.1,5402,tcp --                             generic=socket,out,10,127.0.0.1,5400,tcp,generic_small

-Now press Fly!
-run the SolverServer
-Run FightSimCon
-The Server is runing under Port 4343
- the flightGear is runining on Port 5402
