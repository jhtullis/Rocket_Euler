
package rocket_euler;

import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.util.Arrays;
import java.lang.*;

// Author: Jason Henry Tullis, jhtullis@byu.edu
// This is a program I wrote as a sophomore in high school for an individually
// designed class project that served as the final exam for AP Computer Science
// Principles.

// I share the program now in order to evidence some of my proficiency in Java,
// and my basic abilities at that time with both object-oriented and functional
// programming, and in understanding and implementing numeric algorithms from
// the ground up (in this case, Euler's method).

// Below are the program description and code as written in 2018, with minor
// edits to formatting.
// June 2024

/*
This program uses the Tsiolkovsky rocket equation,
formulas in physics relating to rockets,
and known constants such as the gravitational constant
in order to compute new values.

Much of the information relating to the physics equations and constants
was or can be obtained from wikipedia.org.

Also,
https://www.grc.nasa.gov/www/k-12/airplane/atmosmrm.html
https://scied.ucar.edu/webweather/weather-ingredients/change-atmosphere-altitude
https://en.wikipedia.org/wiki/Barometric_formula

were used to obtain data, but NOT program code, crucial for the
accurate operation of this program.
*/
public class Rocket_Euler {
    
    
	//----------------------------------------Global Variable Section---------//
    
	//-------------------Universals----------------
    
	//Label on JOptionPane windows
	String R0 = "Rocket Modeling Program";
    
    
	//Radius of the Earth
	double Radius = 6.371 * 1000000;
    
	//Initial Mass of the rocket
	double InitialMass;
    
	//Amount of fuel burnt during the manuver
	double FuelBurnt;
    
	//Specific impulse in m/s
	double EjectVel;
    
    
	//Signifies if the program is in the instructional mode, initially false.
	boolean DefMode = false;

	//Signifies if the rocket escapes from the earth's influence.
	boolean Escape = false;
    
	//--------------Earth--------------------
    
	//The height of the rocket above ground in meters
	double Altitude;
    
	//How long the engine was burning fuel, seconds
	double burnLength;
    
	//Mass of rocket at time t in differential equation
	double massT;
    
	//reference area of the rocket in m^2;
	double Ar;
    
	//Coefficient of Drag.
	double CD;
    
	//Step size in seconds for differential equation solver.
	double dt;
    
	//Signifies if the rocket escapes the atmosphere in it's flight, initially false.
	boolean atmosEscape = false;
	//---------------------------------Space----------------------------
    
	//Initial Altitude of the rocket
	double InitialAltitude;
    
	//Initial speed of the rocket.
	double InitialSpeed;
    
	//Final speed of the rocket.
	double FinalSpeed;
    
	//The offset of the heading from the prograde in degrees
	double HeadingOffset;
    
	//------------------------------- Initialization Section------------------//
	public static void main(String[] args)
	{
    	Rocket_Euler In = new Rocket_Euler();
        In.Start();
	}

	private void Start()
	{
    	//Resets global booleans to false in case of restart from later methods
    	DefMode = false;
    	Escape = false;
    	atmosEscape = false;
   	 
    	//To begin the program and user input
    	JOptionPane.showMessageDialog(null, "Welcome to the Rocket Simulator.", R0, JOptionPane.INFORMATION_MESSAGE, null);
    	JOptionPane.showMessageDialog(null, "This program can model your rocket performing a manuver or launching, "
            	+ "\nand report the results of the manuver. \n\n\n This program "
            	+ "also features an instructional mode for beginning users. "
            	+ "Press OK to begin.", R0, JOptionPane.INFORMATION_MESSAGE, null);
   	 
    	String[] SM0 = { "Atmospheric Launch", "Space Manuver" };
   	 
    	String[] Y_N = { "Yes, please", "No, thanks" };
   	 
    	boolean atmos;
    	String S0 = (String) JOptionPane.showInputDialog(null, "Would you like to simulate"
            	+ " performing a atmospheric launch or a space manuver?",
            	R0, JOptionPane.INFORMATION_MESSAGE, null, SM0, SM0[0]);
   	 
    	if (S0.equals(SM0[0]))
    	{
        	atmos = true;
    	}
    	else
    	{
        	atmos = false;
    	}
   	 
    	String SInstruct = (String) JOptionPane.showInputDialog(null, "Would you like to use the instructional mode?"
            	+ " mode? It will define the values you need to input.", R0, JOptionPane.INFORMATION_MESSAGE, null, Y_N, Y_N[1]);
   	 
    	if (SInstruct.equals(Y_N[0]))
    	{
        	DefMode = true;
    	}
   	 
    	if (atmos)
    	{
            	Start1Surface();
    	}
    	else if (!atmos)
    	{
            	Start1Space();
    	}
           	 
	}    
   	 

	//-------------------------------- Earth Launch Section-------------------//
	private void Start1Surface()
	{
    	JOptionPane.showMessageDialog(null, "This program assumes that the "
            	+ "rocket is launched vertically and continues at uniform full thrust vertically for the duration of the burn.");
   	 
    	//The corresponding definition and instructional phrases to each upcoming window for input.
    	String[] DefStrings =
    	{
        	"Specific Impulse is, more or less, the speed of the "
            	+ "exhaust of your rocket in space. It can be interpreted as the rocket's efficiency. \n\n\n",
        	"The amount of fuel used during the rocket's burn.\n\n\n",
        	"Initial Mass is the total mass of your rocket at the beginning of the simulated burn, including fuel. \n\n\n",
        	"Burn Length is the amount of time it takes the rocket to use all the fuel.\n\n\n",
        	"An approximate coefficient representing the effect of drag on an object. A typical model rocket has \n"
        	+ "a coefficient of drag around .75, while you may have one up to 1.2.\n\n\n",
        	"Reference area is the area of the largest cross section of the object "
        	+ "perpendicular to it's movement relative to a fluid, in this case, the atmosphere. \n\n\n",
        	"Step Size is the amount the simulation moves forward. In general, a smaller step size means a more"
        	+ "\n accurate result and requires a larger computing feat. A good step size is around 1 second."
        	+ "\n\n\n ***Remember: If you make the step size too small, "
            	+ "the results may be subject to round - off error.***\n\n\n"
    	};
   	 
    	//Zeros the defmode strings in order to change the results when in defmode or not in defmode.
    	if (!DefMode)
    	{
        	int n;
        	for (n = 0; n < DefStrings.length; n++)
        	{
            	DefStrings[n] = "";
        	}
   	 
    	}
    	else if (DefMode)
    	{
        	JOptionPane.showMessageDialog(null, "You will now need "
                	+ "to input some data related to your rocket. The different terms "
                	+ "will be defined.", R0, JOptionPane.INFORMATION_MESSAGE, null);
    	}
   	 
    	//User inputs several required variables for the rocket, through JOptionPane interface
    	String S1 = (String) JOptionPane.showInputDialog(null, DefStrings[0]
            	+ "Specific Impulse rating (m/s):", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S1);
    	EjectVel = Double.parseDouble(S1);
  	 
    	String S2 = (String) JOptionPane.showInputDialog(null, DefStrings[1]
            	+ "Fuel Burnt in launch (Kg):", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S2);
    	FuelBurnt = Double.parseDouble(S2);
           	 
    	String S3 = (String) JOptionPane.showInputDialog(null, DefStrings[2]
            	+ "Total Initial Mass of the rocket (Kg):", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S3);
    	InitialMass = Double.parseDouble(S3);
           	 
    	String S4 = (String) JOptionPane.showInputDialog(null, DefStrings[3]
            	+ "Burn Length (s) :", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S4);
    	burnLength = Double.parseDouble(S4);
     	 
    	String S5 = (String) JOptionPane.showInputDialog(null, DefStrings[4]
            	+"Coefficient of Drag :", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S5);
    	CD = Double.parseDouble(S5);
   	 
    	String S6 = (String) JOptionPane.showInputDialog(null, DefStrings[5]
            	+ "Reference Area (m^2) :", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S6);
    	Ar = Double.parseDouble(S6);
   	 
    	String S7 = (String) JOptionPane.showInputDialog(null, DefStrings[6]
            	+ "Step Size (s) :", R0, JOptionPane.INFORMATION_MESSAGE);
    	System.out.println(S7);
    	dt = Double.parseDouble(S7);
   	 
    	//Begins the calculation method, carrying on the newly defined global variables.
    	EarthSurface();
	}
    
	private void EarthSurface()
	{
    	//Time variable in for loop
    	double t = 0;
   	 
    	//Presets Altitude to zero
    	Altitude = 0;
   	 
    	//rCent is distance from the center of the earth in meters, useful in calculating gravitational strength
    	double rCent = Radius + Altitude;
   	 
    	// massT is the mass of the rocket at time t, assuming uniform thrust.
    	massT = ((InitialMass - FuelBurnt)/(burnLength) * t) + InitialMass;
   	 
    	//calculates force of gravity by Newton's law of gravitation
    	double ForceGrav = (massT) * (6.67408)*(10000000)*(1/(rCent * rCent))*1000000*((5.972));
   	 
    	//Defines the thrust of the rocket.
    	double Thrust = EjectVel * (FuelBurnt/burnLength);
   	 
    	//Declares and initializes velocity, for the simulation
    	double v = 0;
   	 
    	//The sum for the Reimann sum, converting velocities to altitudes - just the sums of the velocities.
    	double vSum = 0;
   	 
    	//The maximum altitude of the rocket.
    	double MaxAltitude = 0;
   	 
    	//Declares a variable: A double, really a bunch of bits, representing the force of drag.
    	double ForceDrag;

    	//Determines whether the statement that the engines have stopped firing
    	//has been printed or not. True represents that it has still not been printed yet.
    	boolean printCoast = true;
   	 
    	//Is used later in conjunction with if statements which change to false if the simulation should end,
    	//is the condition to continue on the simulation in the for loop.
    	boolean run = true;
   	 
    	//the array containing values, really just ones and zeros, that represent air densities at given heights,
    	//which are then used in the drag calculations. The first row represents height, the second row, density.
    	//(Found with https://en.wikipedia.org/wiki/Barometric_formula )
    	Double[][] DensityArray =
    	{
        	{0.0, 11000.0, 20000.0, 32000.0, 47000.0, 51000.0, 71000.0, 80000.0},                      	//height(m)
        	{1.2250, 0.36391, 0.08803, 0.01322, 0.00143, 0.00086, 0.000064, 0.0 /*0.0 is approximation.*/} //Density(kg/m^3)
    	};
   	 
    	//Declares a variable double representing the density of the air.
    	double density;

    	//Provides context for the information that follows this
    	//printline that comes from the simulation.
    	System.out.println("Elapsed Time (s), Velocity (m/s), Altitude (m) : ");
   	 
    	//Employs Euler's method for solving differential equations to find an approximation of the velocity function, and
    	//uses a reimann sum to turn the approximate velocities into an approximated position, or altitude, function.
    	//Follows is the Euler's method loop:
   	 
    	for (t = 0; run ; t = t + dt)
    	{
        	//In the case the fuel is out, informs the users and makes the nessecary adjustments to the simulation.
        	if (t > burnLength && printCoast)
        	{
            	Thrust = 0;
            	printCoast = false;
            	System.out.println("\n The engines quit firing, and the rocket begins to coast. \n");
            	System.out.println("Elapsed Time (s), Velocity (m/s), Altitude (m) : ");
        	}
       	 
        	//Prints the current information solved for by Euler's Method
        	System.out.println(t + "\t" + v +"\t" + Altitude);
       	 
        	//Declares density, (valid for only altitude = 0),  which is soon changed
        	density = 1.2250;
        	//For loop finds the applicable density value for a given altitude, using a peicewise
        	//linear approximation structured on the array of densities.
        	//Condition in for loop ensures that the right density is used
        	for (int n = 0; (DensityArray[0][n] <= Altitude) && (n < DensityArray[0].length - 1); n++)
        	{
            	//Constructs the density for altitude and n, using the linear peice described before.
            	density = (Altitude - DensityArray[0][n]) * ((DensityArray[1][n+1]
                    	- DensityArray[1][n])/(DensityArray[0][n+1] - DensityArray[0][n])) + (DensityArray[1][n]);
           	 
            	//Works for Altitude is less than 80000 meters.
        	}    
        	//Sets density as zero if altitude is greater than 71000
            	//meters. It is a good enoough approximation in most cases. It corrects the errors in the previous density
            	//finding method for altitudes greater than the max value of the array.
            	if (Altitude >= 80000)
            	{
                	density = 0;
            	}
       	 
        	//Finds the force of drag, using several variables, some updated, like the density established above.
        	ForceDrag = ((Ar * CD * v * (Math.abs(v)) * density) / 2);      	 
       	 
        	//Finds the vertical acceleration of the rocket as defined by the forces of thrust, gravity, and drag,
        	//as well as the current mass of the rocket.
        	double Acceleration = ((Thrust - ForceGrav - ForceDrag)/massT);
        	//As acceleration is the derivative of position, uses Euler's method to change the
        	v = v + (dt * Acceleration);
       	 
        	//Updates the vSum component of the Reimann sum.
        	vSum = vSum + v;
        	//Updates the Altitude with vSum
        	Altitude = vSum * dt;

        	//resets mass, and stablizes it in the case that all the fuel is burnt.
        	if (t < burnLength)
        	{
        	massT =  InitialMass - (t * (InitialMass - FuelBurnt)/(burnLength));
        	}
        	else
        	{
            	massT = InitialMass - FuelBurnt;
        	}
       	 
        	//Resets the force of Gravity.
        	rCent = Altitude + Radius;
        	ForceGrav = (massT)/rCent * (6.67408)*((1000000)/((rCent)))*10000000*((5.972));

        	//updates maximum altitude
        	if (Altitude > MaxAltitude)
        	{
            	MaxAltitude = Altitude;
        	}
       	 
        	if (Altitude > 80000)
        	{
            	atmosEscape = true;
            	run = false;
        	}
        	if (v < 0)
        	{
            	run = false;
        	}
    	}
   	 
    	//for loop ends

    	//Proceeds to define results and includes reports
    	//for the user based on the results.
   	 
    	//Proceeds for if the ship escaped the atmosphere in it's flight
    	if (atmosEscape)
    	{
        	JOptionPane.showMessageDialog(null, "The Spacecraft leaves the majority of the earth's atmosphere:"
                	+ "\nElapsed Time (s), Velocity (m/s), Altitude (m) : " + t + ", "
                	+ v +", " + Altitude, R0, JOptionPane.INFORMATION_MESSAGE);
       	 
        	//Determines if escapes, and the Maximum altitude if it doesn't.
        	if ((28234331.5 / Math.sqrt(Radius + Altitude)) <= v)
        	{
            	Escape = true;
        	}

        	else
        	{
            	MaxAltitude = (1 / ( (1 / (Altitude + Radius)) - (( v*v / 797177477)/1000000))) - Radius;
            	Escape = false;
        	}
    	}
   	 
    	//If the ship remained within the atmosphere:
    	else
    	{
        	Escape = false;
        	atmosEscape = false;
    	}
   	 
    	//If the rocket did not escape from the planet:
    	if (!Escape)
    	{
       	 
        	if (atmosEscape)
        	{
            	JOptionPane.showMessageDialog(null, "The rocket made it out of "
                    	+ "the atmosphere for a period.", R0, JOptionPane.INFORMATION_MESSAGE);
        	}
        	else if (!atmosEscape)
        	{
            	JOptionPane.showMessageDialog(null, "The rocket remained  within "
                    	+ "the atmosphere of the earth.", R0, JOptionPane.INFORMATION_MESSAGE);
        	}
        	JOptionPane.showMessageDialog(null, "Apex altitude (m): " + MaxAltitude + "\nElapsed Time to Apex(s): "
                	+ (t - dt) + "\nThe craft soon returns to the surface of the earth.", R0, JOptionPane.INFORMATION_MESSAGE);
    	}
   	 
    	//If the rocket did escape:
    	else
    	{
        	if (DefMode)
        	{
        	JOptionPane.showMessageDialog(null, "\n\nYour rocket leaves Earth's influence after reaching escape velocity."
                	+ "\n\n\n(If you ever want to get back, you'd better get going now!:)", R0, JOptionPane.INFORMATION_MESSAGE);
        	}
        	else if (!DefMode)
        	{
        	JOptionPane.showMessageDialog(null, "\n\nYour rocket leaves Earth's "
                	+ "influence after reaching escape velocity.", R0, JOptionPane.INFORMATION_MESSAGE);
        	}
    	}
   	 
    	//User input to continue (restart) or finish (End program).
    	String[] Options = {"Continue", "End Program"};
   	 
    	String S1 = (String) JOptionPane.showInputDialog(null, "Thank you for using Rocket Simulator."
            	+ " Would you like to continue or end the program?", R0,
            	JOptionPane.QUESTION_MESSAGE, null, Options, Options[0]);
   	 
    	if (S1.equals("Continue"))
    	{
        	Start();
    	}
    	else
    	{
        	JOptionPane.showMessageDialog(null, "Ending Program.", R0, JOptionPane.INFORMATION_MESSAGE);
        	//Finishes up the program.
    	}
    	}
	//---------------------------------------------------------------------------------------------
    
	//-------------------------------- Space Burn Section --------------------//                                                             	 
	private void Start1Space()
	{
   	 
    	//The corresponding definition and instructional phrases to each upcoming window for input.
    	String[] DefStrings =
    	{
        	"Specific Impulse is, more or less, the speed of the exhaust of your"
            	+ " rocket in space. It can be interpreted as the rocket's efficiency. \n\n\n",
        	"The amount of fuel used during the rocket's burn.\n\n\n",
        	"Initial Mass is the total mass of your rocket at the beginning \nof the simulated burn, including the mass of the fuel. \n\n\n",
        	"Initial Space Altitude represents the vertical distance in meters from the earth's surface to your rocket. \n\n\n",
        	"The speed of your rocket.\n\n\n",
        	"Prograde is the direction that the rocket is moving. The Heading is the direction "
            	+ "that the rocket is pointing. \n\n\n",
    	};
   	 
    	//Zeros the defmode strings, in order to change the results when in defmode or not in defmode.
  	 
    	if (!DefMode)
    	{
        	int n;
        	for (n = 0; n < DefStrings.length; n++)
        	{
            	DefStrings[n] = "";
        	}
   	 
    	}
   	 
    	else if (DefMode)
    	{
        	JOptionPane.showMessageDialog(null, "You will now need to input some data related to your rocket. The different terms will be defined.", R0, JOptionPane.INFORMATION_MESSAGE, null);
    	}
   	 
   	 
    	//User inputs several required variables for the rocket, through JOptionPane interface
   	 
    	String S1 = (String) JOptionPane.showInputDialog(null, DefStrings[0] + "Specific Impulse rating (m/s):", R0, JOptionPane.INFORMATION_MESSAGE);
    	EjectVel = Double.parseDouble(S1);
  	 
    	String S2 = (String) JOptionPane.showInputDialog(null, DefStrings[1] + "Fuel Burnt in manuver (Kg):", R0, JOptionPane.INFORMATION_MESSAGE);
    	FuelBurnt = Double.parseDouble(S2);
           	 
    	String S3 = (String) JOptionPane.showInputDialog(null, DefStrings[2] + "Total mass of the rocket before the manuver (Kg):", R0, JOptionPane.INFORMATION_MESSAGE);
    	InitialMass = Double.parseDouble(S3);
           	 
    	String S4 = (String) JOptionPane.showInputDialog(null, DefStrings[3] + "Initial Space Altitude (m):", R0, JOptionPane.INFORMATION_MESSAGE);
    	InitialAltitude = Double.parseDouble(S4);
           	 
    	String S5 = (String) JOptionPane.showInputDialog(null, DefStrings[4] +"What is the current \nspeed of the rocket relative \nto the earth"
            	+ "(m/s) ?", R0, JOptionPane.INFORMATION_MESSAGE);
    	InitialSpeed = Double.parseDouble(S5);
           	 
    	String S6 = (String) JOptionPane.showInputDialog(null, DefStrings[5] +"What is the angle between the prograde direction and the "
            	+ "\nburn heading (degrees) relative to the rocket?", R0, JOptionPane.INFORMATION_MESSAGE);
    	HeadingOffset = Math.toRadians(Double.parseDouble(S6));
   	 
    	Space();
	}
    
	private void Space()
	{
    	//Abstraction - uses Math.log and the Tsiolkovsky rocket equation to calculate a value representing the change in speed
    	//of the rocket, and then uses trigenometry and math functions like Math.sin and Math.pow
    	//to find the values that represent the final speed of the rocket relative to earth,
    	double ChangeSpeedBurn = EjectVel * Math.log(InitialMass / (InitialMass - FuelBurnt));
   	 
    	FinalSpeed = Math.sqrt(Math.pow((ChangeSpeedBurn * Math.sin(HeadingOffset)), 2) +
            	Math.pow((ChangeSpeedBurn * Math.cos(HeadingOffset) + InitialSpeed), 2));
   	 
    	double SpeedMagnitudeChangeTotal = FinalSpeed - InitialSpeed;
    	double EscapeSpeed = Math.sqrt( 797177480000000.0 / (InitialAltitude + Radius));
    	if (FinalSpeed >= EscapeSpeed)
    	{
        	Escape = true;
    	}
   	 
    	JOptionPane.showMessageDialog(null, "The final speed relative to earth was " + FinalSpeed + " m/s, \n"
            	+ "with a velocity change of " + SpeedMagnitudeChangeTotal + " m/s contributed by the burn.",
            	R0, JOptionPane.INFORMATION_MESSAGE);
    	String DstringEscape = "";
   	 
    	if (DefMode)
    	{
        	DstringEscape = "\n\n\nEscape Velocity is the minimum speed that, under \n"
                	+ "ideal circumstances, and if it's path is unobstructed, \n"
                	+ "the rocket will not fall back to the Earth.";
    	}
   	 
    	if (Escape)
    	{
    	JOptionPane.showMessageDialog(null, "The rocket acheives it's"
            	+ " escape velocity of " + EscapeSpeed + " m/s. " + DstringEscape, R0, JOptionPane.INFORMATION_MESSAGE);
    	}
    	else
    	{
    	JOptionPane.showMessageDialog(null, "The rocket does not"
            	+ " acheive it's escape velocity of " + EscapeSpeed + " m/s. " + DstringEscape, R0, JOptionPane.INFORMATION_MESSAGE);    
    	}
    	String[] Options = {"Continue", "End Program"};
   	 
    	String S1 = (String) JOptionPane.showInputDialog(null, "Thank you for using Rocket Simulator."
            	+ " Would you like to continue or end the program?", R0,
            	JOptionPane.QUESTION_MESSAGE, null, Options, Options[0]);
   	 
    	if (S1.equals("Continue"))
    	{
        	//Restarts the program
        	Start();
    	}
    	else
    	{
        	JOptionPane.showMessageDialog(null, "Ending Program.", R0, JOptionPane.INFORMATION_MESSAGE);
        	//Ends the program
    	}
	}
}

