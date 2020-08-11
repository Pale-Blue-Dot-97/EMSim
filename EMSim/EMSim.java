import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.lang.Math;

/**
 * This is a program to simulate a very slow moving bunch of protons moving in a
 * weak, uniform magnetic field or under the influence of a point charge.
 *
 * There are five modes:
 * (1) A preset mode to set the spread of the bunch in each direction to 0.1m
 *     in turn and the others to 0, with preset time-step, and alogrithm choice
 *
 * (2) This mode allows the user to define the time-step of the simulation,
 *     algorithm choice, the spread of the bunch in x,y,z and the number of
 *     particles in the bunch
 *
 * (3) Another preset mode to simulate a 10% reduction in the magnetic field
 *     for x less than 0. Simulation runs with a preset time-step, algorithm
 *     choice and a spread in x of 0.1m. It runs till the spread in y is within
 *     10% of the spread in x
 *
 * (4) Preset mode to test the difference between the Euler and Euler-Cromer
 *     algorithms by running the simulation with 1 proton for 100 turns using
 *     each algorithm and then comparing results
 *
 * (5) Preset mode to test the difference between the Euler and Euler-Cromer
 *     algorithms but with a point charge at the centre of the cyclotron which
 *     produces a electric field that pulls the proton round in the same orbit
 *     as (4) instead of a uniform magnetic field.
 *
 * @author Harry Baker
 * @version 5.1
 * Date: 03.04.2019
 *
 * VERSION UPDATES
 *
 */
public class EMSim{

  static EMField magField = new EMField(); // EM field of the simulation

  // All the particles in the simulation will be protons
  static final ChargedParticle proton = new ChargedParticle(1.60217662e-19, 1.6726219e-27);

  static final double c = 299792458; // speed of light (source: IOP)

  // Scanner to read user inputs from the command line
  private static Scanner scanner = new Scanner(System.in);
  private static DataOutput output = new DataOutput();

  // Default frequency of prints to screen to improve performance
  private static int print_freq = 1000000;
  // Default frequency of writes to file to improve performance
  private static int write_freq = 1000000;

  private static int M = 0; // Holds algorithm choice
  private static int Mode = 0; // Holds simulation mode choice

  // Booleans to determine the set-up of the simulation
  private static boolean magneticField = false;
  private static boolean failingField = false;
  private static boolean electricPointCharge = false;
  private static boolean cyclotron = false;

  private static double t = 1.0e-6; // Time-step of simulation
  private static double tol = 0.01; // Tolerence used in RKF45 adaptive time-step
  private static double t_min = 1.0e-7; // Range of adaptive time-step to prevent
  private static double t_max = 0.01; // instability of RKF45
  private static double turnNum = 10.0; // Number of turns to simulate
  private static double spread_x = 0.0; // Default x-spread
  private static double spread_y = 0.0; // Default y-spread
  private static double spread_z = 0.0; // Default z-spread
  private static int particle_num = 100; // Default number of particles
  private static double l; // Width of accelerating field
  private static double phi = Math.PI/2.0; // Phase of accelerating field
  private static double E_strength = 1.0e-7; // Amplitude of accelerating field
  private static double B = 1.0e-7; // Strength of uniform magnetic field

  private static int runNum = 0; // Number of times simulation is run in a session

  private static int maxTurns = 100000;

  // Intial average velocity of the bunch
  private static double v = 0.1;
  // Maximun deviation from intial velocity of bunch for particles to be created
  // with, in fractions of v
  private static double v_sigma = 0.0;

  // String to hold data on multiple runs of the simulation with varying parameters
  // used in some of the test modes, which are then sent to file
  private static String sessionData = "";

  // Booleans to set what data is written to string then to file
  private static boolean writePosData = true;
  private static boolean writeSpreadData = true;
  private static boolean writeConsvData = true;

  // Sets whether to print the status of the sim to screen
  // frequency dependent on print_freq
  private static boolean printStatus = true;
  private static boolean printTurn = true;

  public static String algorithmName(int M)
    {
      String[] algorithmNames = new String[6];
      algorithmNames[0] = "Euler";
      algorithmNames[1] = "Euler-Cromer";
      algorithmNames[2] = "Heun's";
      algorithmNames[3] = "Velocity Verlet";
      algorithmNames[4] = "4th-order Runge-Kutta";
      algorithmNames[5] = "Runge-Kutta-Fehlbergy";

      return algorithmNames[M-1];
    }

  /**
   * Prompts user to choose which mode of the program they wish to use
   */
  public static void chooseMode()
    {
      int no_modes = 12;
      do{

        try{
            // Prompts the user to choose a mode
            System.out.println("\n" + "Choose simulation mode:");
            System.out.println("Full user defined simulation                                     (1)");
            System.out.println("Set spread in x,y,z in turn to 0.1m           -for Exercise 2.2- (2)");
            System.out.println("90% magnetic field strength for x<0           -for Exercise 2.3- (3)");
            System.out.println("Euler and Euler-Cromer Comparison (Magnetic)  -for Exercise 3.2- (4)");
            System.out.println("Euler and Euler-Cromer Comparison (Electric)  -for Exercise 3.2- (5)");
            System.out.println("Cyclotron simulation                                             (6)");
            System.out.println("Phase Testing                                                    (7)");
            System.out.println("Position Spread Testing                                          (8)");
            System.out.println("Energy Spread Testing                                            (9)");
            System.out.println("Accelerate bunch to 0.1c                                         (10)");
            System.out.println("Algorithm Testing                                                (11)");
            System.out.println("Adaptive time-step testing                                       (12)");
            Mode = scanner.nextInt(); // Scans user choice
        }catch(InputMismatchException e)
          {
            // If user input was not a int, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting default: User input mode");
            Mode = 1;
            // Mode choice is set to 1 (user input mode) by default
          }

          if((Mode>no_modes)||(Mode<=0))
            {
              // If user enters an int other than a menu option, they are
              // informed they will have to re-enter their choice
              System.out.println("\n" + "Input does not match menu options");
              System.out.println("Please re-enter" + "\n");
            }
        }while((Mode>no_modes)||(Mode<=0));
        // Runs as long as the user does enter a valid menu option
    }

  /**
   * If one of the preset modes was chosen by the user, this method sets up the
   * parameters of the simulation run(s)
   */
  public static void modeConfig()
    {
      // Varies the spread in each direction in turn to 0.1m with other two set to 0
      if(Mode==2)
        {
          M = 2;
          magneticField = true;
          // Runs simulation 3 times, one for every direction of the spread to
          // alter to 0.1m
          for(int i=0;i<3;i++)
            {
              if(i==0)
                {
                  spread_x = 0.1;
                  spread_y = 0.0;
                  spread_z = 0.0;
                }
              if(i==1)
                {
                  spread_x = 0.0;
                  spread_y = 0.1;
                  spread_z = 0.0;
                }
              if(i==2)
                {
                  spread_x = 0.0;
                  spread_y = 0.0;
                  spread_z = 0.1;
                }
              runSim();
            }
        }

      //
      if(Mode==3)
        {
          writePosData = false;
          writeConsvData = false;
          failingField = true;
          M = 2;
          print_freq = 20000;
          t = 1.0e-6;
          spread_x = 0.1;
          spread_y = 0.0;
          spread_z = 0.0;
          runSim();
        }

      // Sets up parameters that Modes 4 and 5 share
      if((Mode==4)||(Mode==5))
        {
          if(Mode==4)
            {
              magneticField = true;
            }
          if(Mode==5)
            {
              electricPointCharge = true;
            }

          particle_num = 1;
          spread_x = 0.0;
          spread_y = 0.0;
          spread_z = 0.0;
          t = 1.0e-5;
          turnNum = 100.0;
          write_freq = 10000;
          print_freq = 1000000;

          M = 1;
          runSim();

          M = 2;
          runSim();
        }

      if((Mode==6)||(Mode==7)||(Mode==8)||(Mode==9)||(Mode==10)||(Mode==11)||(Mode==12))
        {
          cyclotron = true;
        }
      if((Mode==7)||(Mode==8)||(Mode==9)||(Mode==10)||(Mode==11)||(Mode==12))
        {
          writePosData = false;
          writeConsvData = false;
          writeSpreadData = false;
          printStatus = false;
        }
      // Mode to demostrate the cyclotron mode. Used in development of the code
      // required for cyclotron functionality
      if(Mode==6)
        {
          particle_num = 1;
          spread_x = 0.0;
          spread_y = 0.0;
          spread_z = 0.0;
          t = 1.0e-7;
          turnNum = 100.0;
          write_freq = 1000000;
          print_freq = 1000000;
          M = 3;

          runSim();
        }

      // Phase Testing
      if(Mode==7)
        {
          printTurn = false;
          particle_num = 1;
          spread_x = 0.0;
          spread_y = 0.0;
          spread_z = 0.0;
          t = 1.0e-5;
          turnNum = 100.0;
          M = 3;

          for(int i=0;phi<=2*Math.PI;i++)
            {
              phi = i*(1.0/50.0)*Math.PI;
              runSim();
            }
        }

      // Sets up parameters for Mode 8
      if(Mode==8)
        {
          t = 1.0e-5;
          turnNum = 10.0;
          particle_num = 50;
          M = 3;
          v = 0.1;
          v_sigma = 0.1*v;

          for(int i=0;spread_x<=0.1;i++)
            {
              spread_x = Math.pow(10,0.1*i)*1.0e-8;
              spread_y = 0.0;
              spread_z = 0.0;
              runSim();
            }
          for(int i=1;spread_y<=0.1;i++)
            {
              spread_x = 0.0;
              spread_y = Math.pow(10,0.1*i)*1.0e-8;
              spread_z = 0.0;
              runSim();
            }
          for(int i=1;spread_z<=0.1;i++)
            {
              spread_x = 0.0;
              spread_y = 0.0;
              spread_z = Math.pow(10,0.1*i)*1.0e-8;
              runSim();
            }
        }

      // Mode to test effect of varying maximun spread in initial velocity
      if(Mode==9)
        {
          particle_num = 50;
          turnNum = 50.0;
          M = 3;
          v = 1.0e3;

          for(int i=0;(v_sigma/v)<=0.5;i++)
            {
              failingField = false;
              spread_x = 0.01;
              spread_y = 0.01;
              spread_z = 0.01;
              t = 1.0e-4;
              v_sigma = Math.pow(10,0.1*i)*1.0e-8*v;
              runSim();
            }
          v_sigma = 1.0e-8*v;
          for(int i=0;(v_sigma/v)<=0.5;i++)
            {
              failingField = true;
              spread_x = 0.01;
              spread_y = 0.01;
              spread_z = 0.01;
              t = 1.0e-4;
              v_sigma = Math.pow(10,0.1*i)*1.0e-8*v;
              runSim();
            }
        }

      //Accelerate proton to 0.1c
      if(Mode==10)
        {
          printTurn = false;
          B = 1.0e-7;
          E_strength = 1.0e-6;
          particle_num = 1;
          spread_x = 0.0;
          spread_y = 0.0;
          spread_z = 0.0;
          t = 1.0e-4;
          M = 3;
          v = 1.0e3;
          //v_sigma = 1.0e-5*v;
          v_sigma = 0.0;

          runSim();
        }

      // Testing algorithms with different time-steps
      if(Mode==11)
        {
          printTurn = false;
          particle_num = 1;
          turnNum = 50.0;
          write_freq = 1000000000;
          print_freq = 1000000000;
          v = 0.1;
          v_sigma = 0.0;
          spread_x = 0.0;
          spread_y = 0.0;
          spread_z = 0.0;
          t_min = 1.0e-6;

          for(int i=1;i<=5;i++)
            {
              M = i;
              t = 1.0e-2;
              cyclotron = true;
              for(int j=0;t>=t_min;j++)
                {
                  t = Math.pow(10,-0.05*j)*1.0e-2;
                  runSim();
                }
            }
          for(int i=1;i<=5;i++)
            {
              M = i;
              t = 1.0e-2;
              cyclotron = false;
              magneticField = true;
              for(int j=0;t>=t_min;j++)
                {
                  t = Math.pow(10,-0.05*j)*1.0e-2;
                  runSim();
                }
            }
        }

      if(Mode==12)
        {
          M = 6;
          particle_num = 1;
          v_sigma = 0.0;
          spread_x = 0.0;
          spread_y = 0.0;
          spread_z = 0.0;
          t_max = 5.0e-3;
          t_min = 1.0e-6;
          turnNum = 50.0;

          for(int i=0;tol>=1.0e-3;i++)
            {
              t = 1.0e-4;
              cyclotron = true;
              tol = Math.pow(10,-0.1*i)*0.5;
              runSim();
              System.out.println("Final time-step: " + t + "s");
              System.out.println("\n**************************************\n");
            }
          tol = 0.5;
          for(int i=0;tol>=1.0e-3;i++)
            {
              t = 1.0e-4;
              cyclotron = false;
              magneticField = true;
              tol = Math.pow(10,-0.1*i)*0.5;
              runSim();
              System.out.println("Final time-step: " + t + "s");
              System.out.println("\n**************************************\n");
            }
        }
    }

  /**
   * Prompts user to choose which field set up they wish to use to run the simulation
   */
  public static void chooseFields()
    {
      int choice = 0; // int to hold user menu choice
      int no_setups = 5; // number of choices
      do{
        try{
            // Prompts the user to choose a field set-up
            System.out.println("\n" + "Choose which combination of EM fields to simulate:");
            System.out.println("Uniform magnetic field                  (1)");
            System.out.println("Failing uniform magnetic field          (2)");
            System.out.println("Electric point charge                   (3)");
            System.out.println("Cyclotron                               (4)");
            System.out.println("Cyclotron with failing B field          (5)");
            choice = scanner.nextInt(); // Scans user choice
        }catch(InputMismatchException e)
          {
            // If user input was not a int, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting default: Using uniform magnetic field");
            choice = 1;
            // Field set-up is set to 1 (uniform magnetic field) by default
          }
        // Sets up the required booleans to run the simulation with the user
        // defined field set-up
        if(choice==1)
          {
            magneticField = true;
            System.out.println("Uniform magnetic field -SET-");
          }
        if(choice==2)
          {
            failingField = true;
            System.out.println("Failing magnetic field -SET-");
          }
        if(choice==3)
          {
            electricPointCharge = true;
            System.out.println("Point charge -SET-");
          }
        if(choice==4)
          {
            cyclotron = true;
            System.out.println("Cyclotron Simulation -SET-");
            enterPhase();
          }
        if(choice==5)
          {
            cyclotron = true;
            failingField = true;
            System.out.println("Defective Cyclotron Simulation -SET-");
            enterPhase();
          }

        if((choice>no_setups)||(choice<=0))
          {
            // If user enters an int other than a menu option, they are
            // informed they will have to re-enter their choice
            System.out.println("\n" + "Input does not match menu options");
            System.out.println("Please re-enter" + "\n");
          }
        }while((choice>no_setups)||(choice<=0));
        // Runs as long as the user does enter a valid menu option
    }

  /**
   * Prompts the user to enter the phase of the accelerating electric field
   */
  public static void enterPhase()
    {
      // Prompts user to enter phase
      System.out.println("\n" + "Enter phase (in units of pi): ");
      try{
        System.out.print("phi: "); phi = scanner.nextDouble()*Math.PI;
        // Scans in user entered phase
      }catch(InputMismatchException e)
        {
          // If user input was not a double, throws and catches the
          // InputMismatchException which is printed
          System.out.println("\n" + "Input mismatch exception: " + e);
          System.out.println("Setting to default phi = pi/2");
          phi = Math.PI/2.0;
          // Then the user is told that the phase has been set to default
          // of pi/2. Program can now continue
        }
    }

  /**
   * Prompts user to choose which algorithm they wish to use to run the simulation
   */
  public static void chooseAlgorithm()
    {
      int no_algorithms = 6;
      do{
        try{
            // Prompts the user to choose a algorithm
            System.out.println("\n" + "Choose algorithm:");
            System.out.println("Euler             (1)");
            System.out.println("Euler-Cromer      (2)");
            System.out.println("Heun's            (3)");
            System.out.println("Verlet            (4)");
            System.out.println("RK4               (5)");
            System.out.println("RKF45             (6)");
            M = scanner.nextInt(); // Scans user choice
        }catch(InputMismatchException e)
          {
            // If user input was not a int, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting default: Using Euler");
            M = 1;
            // Algorithm choice is set to 1 (Euler) by default
          }
          // If user picked RKF45, the tolerence then needs to be defined
          if(M==6)
            {
              enterTolerence();
            }

          if((M>no_algorithms)||(M<=0))
            {
              // If user enters an int other than a menu option, they are
              // informed they will have to re-enter their choice
              System.out.println("\n" + "Input does not match menu options");
              System.out.println("Please re-enter" + "\n");
            }
        }while((M>no_algorithms)||(M<=0));
        // Runs as long as the user does enter a valid menu option
    }

  /**
   * Prompts the user to enter the time-step of the simulation
   */
  public static void enterTimeStep()
    {
      do{
        // Prompts user to enter time-step
        System.out.println("\n" + "Enter time interval (in s): ");
        try{
          System.out.print("t: "); t = scanner.nextDouble();
          // Scans in user entered time-step
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default t = 1.0e-6s");
            t = 1.0e-6;
            // Then the user is told that the time-step has been set to default
            // of 1.0e-6s. Program can now continue
          }

        if(t<=0)
          {
            // If the user enters a negative or 0 time-step (which is not
            // compatible with this simulation), informs the user that they will
            // have to re-enter
            System.out.println("\n" + "Time-step cannot be less than or equal to 0");
            System.out.println("Please re-enter");
          }
        }while(t<=0);
        // Loop runs until user enters a valid time-step
    }

  /**
   * Prompts the user to enter the tolerence of the adaptive time-step calculations
   * used in the RKF45 method. Only called if user selected RKF45
   */
  public static void enterTolerence()
    {
      do{
        // Prompts user to enter tolerence
        System.out.println("\n" + "Enter tolerence of simulation (in fraction difference between solutions of RK4 and RK5): ");
        try{
          System.out.print("tol: "); t = scanner.nextDouble();
          // Scans in user entered tolerence
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default tol = 1.0e-3");
            tol = 1.0e-3;
            // Then the user is told that the tolerence has been set to default
            // of 1.0e-3. Program can now continue
          }

        if(tol<=0)
          {
            // If the user enters a negative or 0 tolerence, informs the user
            // that they will have to re-enter
            System.out.println("\n" + "Tolerence cannot be less than or equal to 0");
            System.out.println("Please re-enter");
          }
        }while(tol<=0);
        // Loop runs until user enters a valid time-step
    }

  /**
   * Prompts user to define how many turns they wish to simulate
   */
  public static void enterTurnNum()
    {
      do{
        // Prompts user to enter number of turns to complete
        System.out.println("\n" + "Enter the number of turns to be completed: ");
        try{
          System.out.print("Number of turns: "); turnNum = scanner.nextDouble();
          // Scans in user entered number of turns wanted
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default of 10 turns");
            turnNum = 10.0;
            // Then the user is told that the number of turns has been set
            // to default of 10. Program can now continue
          }

        if((turnNum<=0)||((turnNum % 1) != 0))
          {
            // If the user enters a negative, 0 or non-integer number of turns
            // (which is not obviously possible), informs the user that they
            // will have to re-enter
            System.out.println("\n" + "Turn number cannot be less than or equal to 0 or non-integer");
            System.out.println("Please re-enter");
          }
        }while(turnNum<=0);
        // Loop runs until user enters a valid turn number
    }

  /**
   * Prompts user to define how many protons they wish to simulate in the bunch
   */
  public static void enterNumParticle()
    {
      do{
        // Prompts user to enter number of protons
        System.out.println("\n" + "Enter the number of protons to be simulated: ");
        try{
          System.out.print("Number of particles: "); particle_num = scanner.nextInt();
          // Scans in user entered number of particles wanted
        }catch(InputMismatchException e)
          {
            // If user input was not an integer, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default of 100 protons");
            particle_num = 100;
            // Then the user is told that the number of particles has been set
            // to default of 100. Program can now continue
          }
        if(particle_num!=1)
          {
            enterSpread();
            enterVSigma();
          }
        if(particle_num<=0)
          {
            // If the user enters a negative or 0 number of particles (which is not
            // obviously possible), informs the user that they will have to re-enter
            System.out.println("\n" + "Particle number cannot be less than or equal to 0");
            System.out.println("Please re-enter");
          }
        }while(particle_num<=0);
        // Loop runs until user enters a valid number of particles
    }

  /**
   * Prompts the user to enter the velocity of the simulation
   */
  public static void enterVelocity()
    {
      do{
        // Prompts user to enter the intial velocity of the particles
        System.out.println("\n" + "Enter intial velocity (in ms^-1): ");
        try{
          System.out.print("v: "); v = scanner.nextDouble();
          // Scans in user entered velocity
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default v = 0.1ms^1");
            v = 0.1;
            // Then the user is told that the intial velocity has been set to default
            // of 0.1ms^-1. Program can now continue
          }

        if(v<=0)
          {
            // If the user enters a negative or 0 velocity (which is not
            // compatible with this simulation), informs the user that they will
            // have to re-enter
            System.out.println("\n" + "Intial velocity cannot be less than or equal to 0");
            System.out.println("Please re-enter");
          }
        }while(v<=0);
        // Loop runs until user enters a valid velocity
    }

  /**
   * Allows user to set the maximun spread of the velocities of the particles
   * in the bunch, as a fraction of the average intial velocity v.
   */
  public static void enterVSigma()
    {
      do{
        // Prompts user to enter the intial spread of velocities of the particles
        System.out.println("\n" + "Enter intial spread of velocities (as a fraction of v): ");
        try{
          System.out.print("v_sigma: "); v_sigma = (scanner.nextDouble())*v;
          // Scans in user entered velocity spread
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default v_sigma = 1.0e-3*v");
            v_sigma = 1.0e-3*v;
            // Then the user is told that the velocity sigma has been set to default
            // of 1.0e-3*v. Program can now continue
          }

        if((v_sigma<0)||((v_sigma)>=v))
          {
            // If the user enters a negative velocity sigma (or one that results in one,
            // which is not compatible with this simulation), informs the user
            // that they will have to re-enter
            System.out.println("\n" + "Intial velocity spread cannot be less than 0");
            System.out.println("Please re-enter");
          }
        }while((v_sigma<0)||((v_sigma)>=v));
        // Loop runs until user enters a valid velocity sigma
    }

  /**
   * Allows user to set the maximum spread of the particles randomised intial
   * positions in the bunch, in x,y,z directions
   */
  public static void enterSpread()
    {
      System.out.println("\n" + "Enter the maximum spread of the protons of the bunch in fractions of cyclotron radii.");
      System.out.println("The protons will be randomly created within these limits to form the bunch.");
      do{
        // Prompts user to enter spread in x of protons
        try{
          System.out.print("X: "); spread_x = scanner.nextDouble();
          // Scans in user entered spread
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default spread = 1.0e-4*R");
            spread_x = 1.0e-4;
            // Then the user is told that the spread in x has been set to default
            // of 1.0e-4*R. Program can now continue
          }

        if(spread_x<0)
          {
            // If the user enters a negative spread (which is not
            // compatible with this simulation), informs the user that they will
            // have to re-enter
            System.out.println("\n" + "Spread cannot be less than 0");
            System.out.println("Please re-enter");
          }
        }while(spread_x<0);
        // Loop runs until user enters a valid time-step

      do{
        // Prompts user to enter spread in x of protons
        try{
          System.out.print("y: "); spread_y = scanner.nextDouble();
          // Scans in user entered spread
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default spread = 1.0e-4*R");
            spread_y = 1.0e-4;
            // Then the user is told that the spread in y has been set to default
            // of 1.0e-4*R. Program can now continue
          }

        if(spread_y<0)
          {
            // If the user enters a negative or 0 spread (which is not
            // compatible with this simulation), informs the user that they will
            // have to re-enter
            System.out.println("\n" + "Spread cannot be less than 0");
            System.out.println("Please re-enter");
          }
        }while(spread_y<0);
        // Loop runs until user enters a valid spread

      do{
        // Prompts user to enter spread in x of protons
        try{
          System.out.print("z: "); spread_z = scanner.nextDouble();
          // Scans in user entered spread
        }catch(InputMismatchException e)
          {
            // If user input was not a double, throws and catches the
            // InputMismatchException which is printed
            System.out.println("\n" + "Input mismatch exception: " + e);
            System.out.println("Setting to default spread = 1.0e-4*R");
            spread_z = 1.0e-4;
            // Then the user is told that the spread in z has been set to default
            // of 1.0e-4*R. Program can now continue
          }

        if(spread_z<0)
          {
            // If the user enters a negative spread (which is not
            // compatible with this simulation), informs the user that they will
            // have to re-enter
            System.out.println("\n" + "Spread cannot be less than 0");
            System.out.println("Please re-enter");
          }
        }while(spread_z<0);
        // Loop runs until user enters a valid spread
    }

  /**
   * Writes the spread in x,y,z to string
   *
   * @param bunch Bunch being simulated to have spread of particles writen to string
   *
   * @return String with spread in x,y,z of bunch particles added to it
   *
   */
  public static String writeSpread(Bunch bunch)
    {
      String data = "";
      // Writes the spread in x,y,z to string
      data += bunch.calcSpreadx() + "\t"
            + bunch.calcSpready() + "\t"
            + bunch.calcSpreadz() + "\r\n";

      return data;
    }

  /**
   * Writes the position of bunch in x,y,z to string
   *
   * @param bunch Bunch being simulated to have position writen to string
   *
   * @return String with position in x,y,z of bunch added to it
   *
   */
  public static String writePosition(Bunch bunch)
    {
      String data = "";
      // Writes the spread in x,y,z to string
      data += bunch.getAvgPos().getX() + "\t"
            + bunch.getAvgPos().getY() + "\t"
            + bunch.getAvgPos().getZ() + "\r\n";

      return data;
    }

  /**
   * Writes the total kinnetic and potential energy of the bunch, and the total
   * energy and total angular momentum of the bunch to a string with each entry
   * time stamped
   *
   * @param t Current time of the simulation
   * @param bunch Bunch being simulated to have conserved quantities writen to string
   *
   * @return String with current energies and angular momentum with current time
   *
   */
  public static String writeConsv(double t, Bunch bunch)
    {
      String data = "";
      // Writes the spread in x,y,z to string
      data += t + "\t"
            + bunch.getTotKE() + "\t"
            + bunch.getTotPE() + "\t"
            + bunch.getTotE() + "\t"
            + bunch.getTotL() + "\r\n";

      return data;
    }

  /**
   * Calculates the acceleration of the whole bunch at the time of calling
   *
   * @param bunch
   * @param magField
   * @param pointCharge
   * @param eField
   * @param l
   * @param tott
   *
   */
  public static void accelerateBunch(Bunch bunch, EMField magField,
  ChargedParticle pointCharge, EMOscField eField, double l, double tott)
    {
      if(cyclotron==true)
        {
          eField.updateEM(tott);
        }

      bunch.setLorentzForce(magField,pointCharge,eField,l,
      magneticField,failingField,electricPointCharge,cyclotron);
    }

  /**
   * Method used to determine if a turn has been completed
   *
   * If the average velocity of the bunch in the x-direction at the end
   * of the iteration is positive AND the average velocity of the bunch
   * in the x-direction at the start of the iteration was negative,
   * by simple harmonic motion, the bunch must have passed through the
   * origin and thus a loop is complete.
   *
   * @param bunch State of bunch at end of iteration
   * @param oldbunch State of bunch at start of iteration
   *
   * @return True if turn complete, false if not
   */
  public static boolean determineTurnEnd(Bunch bunch, Bunch oldbunch)
    {
      boolean turnDone = false;

      if(((bunch.getAvgVel().getX())>=0.0)&&((oldbunch.getAvgVel().getX())<=0.0))
        {
          turnDone = true;
        }
      else
        {
          turnDone = false;
        }

      return turnDone;
    }

  /**
   * Method to determine if spread_x = spread_y
   *
   * @param bunch Bunch to have spreads in x,y compared
   *
   * @return True if simulation complete, false if not
   */
  public static boolean determineSpreadxSpreadyEqual(Bunch bunch)
    {
      boolean simEnd = false;
      double tol_spread = 0.1;

      if((bunch.calcSpready()>=((1.0-tol_spread)*bunch.calcSpreadx()))&&(bunch.calcSpready()<=((1.0+tol_spread)*bunch.calcSpreadx())))
        {
          simEnd = true;
        }
      else
        {
          simEnd = false;
        }

      return simEnd;
    }

  /**
   * Returns a formatted time-stamp at time of calling for use to name output
   * files.
   * Formatted such that day_month_year--hour-minutes
   *
   * @return String of formatted current date and time
   */
  public static String timeStamp()
    {
      SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy--'at'--HH-mm");
      Date date = new Date();
      String timeStamp = format.format(date);
      return timeStamp;
    }

  /**
   * Runs the simulation with the parameters defined, in the mode requested
   */
  public static void runSim()
    {
      System.out.println("\n*****************************************");
      runNum ++; // Logs the number of times simulation has been run this session

      // Magnetic field is set to B in z-direction
      magField.setMagnetic(new PhysicsVector(0.0,0.0,B));

      //Calculates the expected period using the cyclotron frequency equation
      // T = (2*pi*m)/(Q*B)
      double expT = (2*Math.PI*proton.getMass())/(proton.getCharge()*magField.getMagnetic().magnitude());

      // Point charge for use in Mode 5
      ChargedParticle pointCharge = new ChargedParticle();

      // Calculates the position of the point charge such that it lies at
      // 1 cyclotron radii from the magnetic field modes
      // R = (v*T)/(2*pi)
      double radius = (v*expT)/(2*Math.PI);
      PhysicsVector centre = new PhysicsVector(radius,0.0,0.0);
      pointCharge.setPosition(centre);

      PhysicsVector phi_E = new PhysicsVector(0.0,phi,0.0);
      PhysicsVector phi_B = new PhysicsVector(0.0,0.0,0.0);
      double omega = (2*Math.PI)/expT;
      PhysicsVector w_E = new PhysicsVector(0.0,omega,0.0);
      PhysicsVector w_B = new PhysicsVector(0.0,0.0,0.0);
      PhysicsVector E_o = new PhysicsVector(0.0,E_strength,0.0);
      PhysicsVector B_o = new PhysicsVector(0,0,0);
      EMOscField eField = new EMOscField(E_o,B_o,phi_E,phi_B,w_E,w_B);

      l = 0.05*radius;
      double V = 2.0*E_strength*l;

      if((Mode!=3)&&(Mode!=2))
        {
          spread_x = spread_x*radius;
          spread_y = spread_y*radius;
          spread_z = spread_z*radius;
        }

      double Q = 0.0;
      // Sets the point charge's charge if user selected mode 5
      if(electricPointCharge==true)
        {
          // Calculates the charge required by a point charge to simulate the
          // same orbit with an electric field as produced by the magnetic
          // field in other modes
          // Q = (m^2*v^3)/(k*q^2*B)
          Q = -(Math.pow(proton.getMass(),2)*Math.pow(v,3))
          /(pointCharge.getCoulombConst()*Math.pow(proton.getCharge(),2)*magField.getMagnetic().magnitude());

          pointCharge.setCharge(Q);
        }

      // Bunch is created using the parameters defined
      Bunch bunch = new Bunch(proton,particle_num,v,v_sigma,spread_x,spread_y,spread_z);

      // Bunch is re-aligned to origin to account for the random intial placement
      // of the protons in the bunch
      bunch.reAlign();

      // Prints bunch position after re-alignment. Should be close to zero
      //System.out.println("Bunch at origin?: " + bunch.getAvgPos().magnitude());

      // Calculates and stores the intial energy of the bunch for later use
      bunch.calcEnergy(pointCharge.getCharge(),centre,magField.getMagnetic());
      double inKE = bunch.getTotKE();
      double inPE = bunch.getTotPE();
      double inTotE = bunch.getTotE();

      // Calculates and stores the total angular momentum of the bunch for later use
      bunch.calcAngularMomentum(centre);
      double inTotL = bunch.getTotL();

      bunch.CalcAvgVel();
      double v_in = bunch.getAvgVel().magnitude();

      double totexpdE = 0;

      //Now the variables to track the simulation are set up
      double tott = 0.0; //Holds the total time of the simulation
      int iNum = 0; //Holds the iteration number of the simulation
      double turnsDone = 0.0; // Holds number of turns completed thus far
      double rwt_in = System.currentTimeMillis(); //Holds the time simulation run commenced

      // Is false till turnsDone==turnNum and thus keeps simulation running
      // till this is true
      boolean simComplete = false;

      String parameterData = "# EMSIM OUTPUT FILE \r\n"
                           + "# PARAMETERS OF SIMULATION RUN \r\n"
                           + "# ---------------------------- \r\n"
                           + "# Time               : " + timeStamp() + "\r\n"
                           + "# ---------------------------- \r\n"
                           + "# Mode               : " + Mode + " \r\n"
                           + "# Magnetic field?    : " + magneticField + "\r\n"
                           + "# Failing field?     : " + failingField + "\r\n"
                           + "# Point charge?      : " + electricPointCharge + "\r\n"
                           + "# Cyclotron Set-up?  : " + cyclotron + "\r\n"
                           + "# Magnetic strength  : " + B + "T \r\n"
                           + "# Electric strength  : " + E_strength + "NC^-1 \r\n"
                           + "# Phase              : " + phi + "\r\n"
                           + "# Algorithm          : " + algorithmName(M) + "\r\n"
                           + "# Time-step          : " + t + "s \r\n";
         if(M==6)
          {
            parameterData += "# Tolerence          : " + tol + "% \r\n";
          }

            parameterData += "# Number of Turns    : " + turnNum + "\r\n"
                           + "# Number of particles: " + particle_num + "\r\n"
                           + "# Spread in x        : " + spread_x + "m \r\n"
                           + "# Spread in y        : " + spread_y + "m \r\n"
                           + "# Spread in z        : " + spread_z + "m \r\n"
                           + "# Intial average v   : " + v + "ms^-1 \r\n"
                           + "# Intial v_sigma     : " + v_sigma + "ms^-1 \r\n"
                           + "# ----------------------------- \r\n";

      // String to hold the spread of the bunch in each direction data
      // Intially formatted with column headings
      String spreadData = parameterData + "Spread in x \t Spread in y \t Spread in z \r\n";

      // String to hold the position of the bunch
      // Intially formatted with column headings
      String positionData = parameterData + "x \t y \t z \r\n";

      // String to hold the boost to velocity per turn of the bunch
      // Intially formatted with column headings
      String boostData = parameterData + "Turn Num \t deltaV \r\n";

      if((runNum==1)&&(Mode==7))
        {
          sessionData += parameterData + "phi \t deltaKE \t expected-dKE \r\n";
        }

      if(((runNum==1)||(spread_y == Math.pow(10,0.1)*1.0e-8*radius)||(spread_z == Math.pow(10,0.1)*1.0e-8*radius))&&(Mode==8))
        {
          sessionData = parameterData;
          if(runNum==1)
            {
              sessionData += "x_spread \t delta_KE \r\n";
            }
          if(spread_y == Math.pow(10,0.1)*1.0e-8*radius)
            {
              sessionData += "y_spread \t delta_KE \r\n";
            }
          if(spread_z == Math.pow(10,0.1)*1.0e-8*radius)
            {
              sessionData += "z_spread \t delta_KE \r\n";
            }
        }

      if((Mode==9)&&((runNum==1)||((runNum!=1)&&(v_sigma==1.0e-8*v))))
        {
          sessionData = parameterData + "v_sigma \t delta_KE \r\n";
        }

      if((Mode==11)&&(t==1.0e-2))
        {
          sessionData = parameterData
                      + "dt \t dE_err \t computational-time \r\n";
        }
      if((Mode==12)&&(runNum==1)||((runNum!=1)&&(tol==0.5)))
        {
          sessionData = parameterData
                      + "tol \t dt \t dE_err \t computational-time \r\n";
        }

      // String to hold the energies and angular momentum of the bunch
      // Intially formatted with column headings
      String consvData = parameterData + "Time \t KE \t PE \t E \t L \r\n";
      consvData += tott + "\t" + inKE + "\t" + inPE + "\t" + inTotE + "\t" + inTotL +"\r\n";

      String runConfigPrint =
                        "PARAMETERS OF SIMULATION RUN \r\n"
                      + "---------------------------- \r\n"
                      + "Mode               : " + Mode + " \r\n"
                      + "Magnetic field?    : " + magneticField + "\r\n"
                      + "Failing field?     : " + failingField + "\r\n"
                      + "Point charge?      : " + electricPointCharge + "\r\n"
                      + "Cyclotron Set-up?  : " + cyclotron + "\r\n"
                      + "Magnetic strength  : " + B + "T \r\n"
                      + "Electric strength  : " + E_strength + "NC^-1 \r\n"
                      + "Phase              : " + phi + "\r\n"
                      + "Algorithm          : " + algorithmName(M) + "\r\n"
                      + "Time-step          : " + t + "s \r\n";
                      if(M==6){
      runConfigPrint += "Tolerence          : " + tol + "% \r\n";
                      }
      runConfigPrint += "Number of Turns    : " + turnNum + "\r\n"
                      + "Number of particles: " + particle_num + "\r\n"
                      + "Spread in x        : " + spread_x + "m \r\n"
                      + "Spread in y        : " + spread_y + "m \r\n"
                      + "Spread in z        : " + spread_z + "m \r\n"
                      + "Intial average v   : " + v + "ms^-1 \r\n"
                      + "Intial v_sigma     : " + v_sigma + "ms^-1 \r\n"
                      + "----------------------------- \r\n";

      System.out.print(runConfigPrint);

      double stan_t = t;
      double var_t = 0.01*t;
      boolean var_t_on_off = false;

      do{
          iNum ++; // Counts up number of iterations

          // First calculates the average position and velocity of the bunch
          // at the start of every iteration
          bunch.CalcAvgPos();
          bunch.CalcAvgVel();

          // Copies the bunch so it's state at start of iteration can be stored
          Bunch oldbunch = new Bunch(bunch);
          // Calculates average position and velocity of copied bunch
          // should be unchanged
          oldbunch.CalcAvgVel();
          oldbunch.CalcAvgPos();

          accelerateBunch(bunch,magField,pointCharge,eField,l,tott);

          // Runs the Euler algorithm on each particle in the bunch
          if(M==1)
            {
              bunch.euler(t);
            }

          // Runs the Euler-Cromer algorithm on each particle in the bunch
          if(M==2)
            {
              bunch.eulerCromer(t);
            }

          // Runs Heun's algorithm on each particle in the bunch
          if(M==3)
            {
              // Creates a copy of the intial bunch
              Bunch bunchEnd = new Bunch(bunch);

              // Moves the copy forward by a time-step to provide an intial
              // estimate for the iteration
              bunchEnd.eulerCromer(t);

              // Recalulates the acceleration at this end-point of the iteration
              accelerateBunch(bunchEnd,magField,pointCharge,eField,l,tott);

              // Uses this end-point state of the iteration to produce a better
              // estimate for the iteration
              bunch.heun(t,bunchEnd);
            }

          // Runs the Velocity Verlet algorithm on each particle in the bunch
          if(M==4)
            {
              // Creates an array of PhysicsVectors to store the intial accelerations
              PhysicsVector[] a_in = new PhysicsVector[bunch.getNumParticle()];

              // Sets the entries of a_in to have the total intial accelerations for
              // each particle in the bunch
              for(int i=0; i<bunch.getNumParticle(); i++)
                {
                  a_in[i] = new PhysicsVector(bunch.getParticle(i).getAcceleration());
                }

              bunch.verletPtOne(t);

              accelerateBunch(bunch,magField,pointCharge,eField,l,tott);

              bunch.verletPtTwo(a_in,t);
            }

          // Runs the 4th-order Runge-Kutta algorithm on each particle in the bunch
          if(M==5)
            {
              Bunch k1 = new Bunch(bunch);
              k1.eulerCromer(t);

              Bunch k1_2 = new Bunch(bunch);
              k1_2.findMidPoint(bunch,k1);

              accelerateBunch(k1_2,magField,pointCharge,eField,l,tott);

              Bunch k2 = new Bunch(bunch);
              k2.eulerAhead(t,k1_2);

              Bunch k2_2 = new Bunch(bunch);
              k2_2.findMidPoint(bunch,k2);

              accelerateBunch(k2_2,magField,pointCharge,eField,l,tott);

              Bunch k3 = new Bunch(bunch);
              k3.eulerAhead(t,k2_2);

              Bunch bunchEnd = new Bunch(k3);
              accelerateBunch(bunchEnd,magField,pointCharge,eField,l,tott);

              Bunch k4 = new Bunch(bunch);
              k4.eulerAhead(t,bunchEnd);

              bunch.rK4(k1,k2,k3,k4);
            }

          // RKF45
          double opt_dt = t;
          if(M==6)
            {
              // k1_n = y_n + k1 = y_n + f(t_n,y_n)dt
              Bunch k1_n = new Bunch(bunch);
              k1_n.eulerCromer(t);

              // k1 = f(t_n,y_n)dt
              Bunch k1 = new Bunch(bunch);
              k1.findkn(bunch,k1_n);

              //---------------------------------------------------------
              Bunch k12 = new Bunch(bunch);
              double[] s12 = new double[6];
              s12[0] = 1.0;
              s12[1] = 0.25;
              s12[2] = 0;
              s12[3] = 0;
              s12[4] = 0;
              s12[5] = 0;
              Bunch[] bunches_12 = new Bunch[6];
              bunches_12[0] = new Bunch(bunch);
              bunches_12[1] = new Bunch(k1);
              bunches_12[2] = new Bunch(bunch);
              bunches_12[3] = new Bunch(bunch);
              bunches_12[4] = new Bunch(bunch);
              bunches_12[5] = new Bunch(bunch);
              k12.findVertex(s12,bunches_12);
              accelerateBunch(k12,magField,pointCharge,eField,l,tott);
              Bunch k2_n = new Bunch(bunch);
              k2_n.eulerAhead(t,k12);

              // k2 = f(t_n,y_n)dt
              Bunch k2 = new Bunch(bunch);
              k2.findkn(bunch,k2_n);

              //---------------------------------------------------------
              Bunch k23 = new Bunch(bunch);
              double[] s23 = new double[6];
              s23[0] = 1.0;
              s23[1] = 3.0/32.0;
              s23[2] = 9.0/32.0;
              s23[3] = 0;
              s23[4] = 0;
              s23[5] = 0;
              Bunch[] bunches_23 = new Bunch[6];
              bunches_23[0] = new Bunch(bunch);
              bunches_23[1] = new Bunch(k1);
              bunches_23[2] = new Bunch(k2);
              bunches_23[3] = new Bunch(bunch);
              bunches_23[4] = new Bunch(bunch);
              bunches_23[5] = new Bunch(bunch);
              k23.findVertex(s23,bunches_23);
              accelerateBunch(k23,magField,pointCharge,eField,l,tott);
              Bunch k3_n = new Bunch(bunch);
              k3_n.eulerAhead(t,k23);

              Bunch k3 = new Bunch(bunch);
              k3.findkn(bunch,k3_n);

              //---------------------------------------------------------
              Bunch k34 = new Bunch(bunch);
              double[] s34 = new double[6];
              s34[0] = 1.0;
              s34[1] = 1932.0/2197.0;
              s34[2] = -1*(7200.0/2197.0);
              s34[3] = 7296.0/2197.0;
              s34[4] = 0;
              s34[5] = 0;
              Bunch[] bunches_34 = new Bunch[6];
              bunches_34[0] = new Bunch(bunch);
              bunches_34[1] = new Bunch(k1);
              bunches_34[2] = new Bunch(k2);
              bunches_34[3] = new Bunch(k3);
              bunches_34[4] = new Bunch(bunch);
              bunches_34[5] = new Bunch(bunch);
              k34.findVertex(s34,bunches_34);
              accelerateBunch(k34,magField,pointCharge,eField,l,tott);
              Bunch k4_n = new Bunch(bunch);
              k4_n.eulerAhead(t,k34);

              Bunch k4 = new Bunch(bunch);
              k4.findkn(bunch,k4_n);

              //---------------------------------------------------------
              Bunch k45 = new Bunch(bunch);
              double[] s45 = new double[6];
              s45[0] = 1.0;
              s45[1] = 439.0/216.0;
              s45[2] = -8.0;
              s45[3] = 3680.0/513.0;
              s45[4] = -1*(845.0/4104.0);
              s45[5] = 0;
              Bunch[] bunches_45 = new Bunch[6];
              bunches_45[0] = new Bunch(bunch);
              bunches_45[1] = new Bunch(k1);
              bunches_45[2] = new Bunch(k2);
              bunches_45[3] = new Bunch(k3);
              bunches_45[4] = new Bunch(k4);
              bunches_45[5] = new Bunch(bunch);
              k45.findVertex(s45,bunches_45);
              accelerateBunch(k45,magField,pointCharge,eField,l,tott);
              Bunch k5_n = new Bunch(bunch);
              k5_n.eulerAhead(t,k45);

              Bunch k5 = new Bunch(bunch);
              k5.findkn(bunch,k5_n);

              //---------------------------------------------------------
              Bunch k56 = new Bunch(bunch);
              double[] s56 = new double[6];
              s56[0] = 1.0;
              s56[1] = -1*(8.0/27.0);
              s56[2] = 2.0;
              s56[3] = -1*(3544.0/2565.0);
              s56[4] = 1859.0/4104.0;
              s56[5] = -1*(11.0/40.0);
              Bunch[] bunches_56 = new Bunch[6];
              bunches_56[0] = new Bunch(bunch);
              bunches_56[1] = new Bunch(k1);
              bunches_56[2] = new Bunch(k2);
              bunches_56[3] = new Bunch(k3);
              bunches_56[4] = new Bunch(k4);
              bunches_56[5] = new Bunch(k5);
              k56.findVertex(s56,bunches_56);
              accelerateBunch(k56,magField,pointCharge,eField,l,tott);
              Bunch k6_n = new Bunch(bunch);
              k6_n.eulerAhead(t,k56);

              Bunch k6 = new Bunch(bunch);
              k6.findkn(bunch,k6_n);

              Bunch[] bunches_fn = new Bunch[6];
              bunches_fn[0] = new Bunch(bunch);
              bunches_fn[1] = new Bunch(k1);
              bunches_fn[2] = new Bunch(k3);
              bunches_fn[3] = new Bunch(k4);
              bunches_fn[4] = new Bunch(k5);
              bunches_fn[5] = new Bunch(k6);

              opt_dt = t*bunch.rKF45(t,tol,bunches_fn);
            }

          // Re-calculates the average position and velocity of the updated bunch
          bunch.CalcAvgPos();
          bunch.CalcAvgVel();

          tott += t; // Keeps track of the total time of the simulation

          if((M==6)&&(opt_dt>=t_min)&&(opt_dt<=t_max))
            {
              t = opt_dt;
            }

          /*
          // Variable time-step controls to improve efficent accuracy
          if((bunch.getAvgPos().getY()<l)&&(bunch.getAvgPos().getY()>-l)&&(var_t_on_off==false))
            {
              var_t_on_off = true;
              if(M!=6)
                {
                  t = var_t;
                }
              if(M==6)
                {
                  if((0.01*opt_dt>=t_min)&&(opt_dt<=t_max))
                    {
                      t = 0.01*opt_dt;
                    }
                  else if((opt_dt>=t_min)&&(opt_dt<=t_max))
                    {
                      t = opt_dt;
                    }
                }
            }
          else if(var_t_on_off==true)
            {
              if((M==6)&&(opt_dt>=t_min)&&(opt_dt<=t_max))
                {
                  t = opt_dt;
                }
            }
          else if((bunch.getAvgPos().getY()>=l)&&(bunch.getAvgPos().getY()<=-l)&&(var_t_on_off==true))
            {
              var_t_on_off = false;
              if(M!=6)
                {
                  t = stan_t;
                }
              if((M==6)&&(opt_dt>=t_min)&&(opt_dt<=t_max))
                {
                  t = opt_dt;
                }
            }*/

          // Calculates conserved quantities and writes to appropiate strings
          // if at the write to file interval
          if(iNum % write_freq == 0)
            {
              if(writeConsvData==true)
                {
                  bunch.calcEnergy(Q,centre,magField.getMagnetic());
                  bunch.calcAngularMomentum(centre);
                  consvData += writeConsv(tott,bunch);
                }
              if(writePosData==true)
                {
                  positionData += writePosition(bunch);
                }
              if(writeSpreadData==true)
                {
                  // Writes the spread in x,y,z to string
                  spreadData += writeSpread(bunch);
                }
            }

          // Prints the state of the simulation at the defined print frequency
          if((iNum % print_freq == 0)&&(printStatus==true))
            {
              // Calculates conserved quantities for display
              bunch.calcEnergy(Q,centre,magField.getMagnetic());
              bunch.calcAngularMomentum(centre);

              System.out.println("\n\n" + iNum);
              System.out.println("TURN NUM: " + turnsDone);
              System.out.println("r       : " + bunch.getAvgPos().magnitude() + "m");
              System.out.println("|v|     : " + bunch.getAvgVel().magnitude() + "ms-1");
              if(M==6)
                {
                  System.out.println("dt      : " + t + "s");
                }
              System.out.println("x spread: " + bunch.calcSpreadx() + "m");
              System.out.println("y spread: " + bunch.calcSpready() + "m");
              System.out.println("z spread: " + bunch.calcSpreadz() + "m");
              System.out.println("Total KE: " + bunch.getTotKE() + "J");
              System.out.println("Total PE: " + bunch.getTotPE() + "J");
              System.out.println("Total E : " + bunch.getTotE() + "J");
            }


          // If the average velocity of the bunch in the x-direction at the end
          // of the iteration is positive AND the average velocity of the bunch
          // in the x-direction at the start of the iteration was negative,
          // by simple harmonic motion, the bunch must have passed through the
          // origin and thus a loop is complete.
          //
          // This negates the need for a tolerence window and as such is far more
          // accurate than EMSim V1
          if((iNum>1)&&(determineTurnEnd(bunch,oldbunch)))
            {
              turnsDone ++; // Keeps track of number of turns done
              double dE = bunch.calcEGain(tott,V,B,phi);
              totexpdE += dE;
              double simT = tott/turnsDone;

              /*if(Mode==10)
                {
                  double newOmega = (2*Math.PI)/simT;
                  eField.updateOmega(newOmega);
                }*/


              if((printTurn==true)||((Mode==10)&&(turnsDone%100==0)))
                {
                  bunch.calcEnergy(Q,centre,magField.getMagnetic());
                  bunch.calcAngularMomentum(centre);
                  double v_fn = bunch.getAvgVel().magnitude();

                  // Prints the state of the simulation at the end of the turn
                  System.out.println("\n*****************************************");
                  System.out.println("TURN NUMBER: " + turnsDone);
                  System.out.println("Simulated Period: " + tott/turnsDone + "s");
                  System.out.println("r       : " + bunch.getAvgPos().magnitude() + "m");
                  System.out.println("|v|     : " + v_fn + "ms^-1");

                  if(M==6)
                    {
                      System.out.println("dt      : " + t + "s");
                    }

                  if(cyclotron==true)
                    {
                      double expdv = Math.pow(Math.pow(v_in,2) + (2.0*dE)/(proton.getMass()*bunch.getNumParticle()),0.5) - v_in;
                      double deltaV = v_fn - v_in;
                      System.out.println("delta v     : " + deltaV + "ms^-1");
                      System.out.println("Expected dv : " + expdv + "ms^-1");
                      System.out.println("Expected dKE: " + dE + "J");
                      boostData += turnsDone + "\t" + deltaV + "\r\n";
                      v_in = v_fn;
                    }

                  System.out.println("x spread: " + bunch.calcSpreadx());
                  System.out.println("y spread: " + bunch.calcSpready());
                  System.out.println("z spread: " + bunch.calcSpreadz());
                  System.out.println("\n*****************************************");

                  // Writes data to string
                  if(writePosData==true)
                    {
                      positionData += writePosition(bunch);
                    }
                  if(writeConsvData==true)
                    {
                      consvData += writeConsv(tott,bunch);
                    }
                  if(writeSpreadData==true)
                    {
                      spreadData += writeSpread(bunch);
                    }
                }
            }

          // Checks if simulation end conditions are met
          if(((turnsDone == turnNum)&&(Mode!=3)&&(Mode!=10))
          ||((Mode==10)&&(bunch.getAvgVel().magnitude()>=0.1*c))
          ||((determineSpreadxSpreadyEqual(bunch))&&(Mode==3))
          ||(turnsDone>=maxTurns))
            {
              // Calculates and stores final conserved quantities to compare to
              // intial values to test how well they are conserved
              bunch.calcEnergy(pointCharge.getCharge(),centre,magField.getMagnetic());
              bunch.calcAngularMomentum(centre);
              double fnKE = bunch.getTotKE();
              double fnPE = bunch.getTotPE();
              double fnTotE = bunch.getTotE();
              double fnTotL = bunch.getTotL();
              double dKE = fnKE-inKE;
              double perdKE = 100.0*(dKE/inKE);
              double percexpdE = 100.0*(totexpdE/inKE);
              double dKE_syn = turnNum*bunch.calcESynGain(tott,V,B,phi);
              double percexpdE_syn = 100.0*(dKE_syn/inKE);
              double diffdKE = Math.abs(dKE-totexpdE);

              double errT = Math.abs(expT-(tott/turnsDone));
              double percErrT = 100.0*(errT/expT);

              double errE = Math.abs(inTotE-fnTotE);
              double percErrE = 100.0*(errE/inTotE);

              double errL = Math.abs(inTotL-fnTotL);
              double percErrL = 100.0*(errL/inTotL);

              double rwt_fn = System.currentTimeMillis();
              double compt = 1.0e-3*(rwt_fn - rwt_in);

              double simErr = 0.0;
              if(cyclotron==true)
                {
                  simErr = 100.0*(diffdKE/totexpdE);
                }
              if((magneticField==true)||(electricPointCharge==true))
                {
                  simErr = percErrE;
                }

              if(writeConsvData==true)
                {
                  // Writes final conserved quantities to string
                  consvData += tott + "\t" + fnKE + "\t" + fnPE + "\t" + fnTotE + "\t" + fnTotL + "\r\n";
                }
              if(writeSpreadData==true)
                {
                  spreadData += writeSpread(bunch);
                }

              if(Mode==7)
                {
                  sessionData += phi/Math.PI + "\t" + perdKE + "\t" + percexpdE + "\r\n";
                }
              if(Mode==8)
                {
                  if(spread_x>0.0)
                    {
                      sessionData += spread_x + "\t" + perdKE + "\r\n";
                    }
                  if(spread_y>0.0)
                    {
                      sessionData += spread_y + "\t" + perdKE + "\r\n";
                    }
                  if(spread_z>0.0)
                    {
                      sessionData += spread_z + "\t" + perdKE + "\r\n";
                    }
                }
              if(Mode==9)
                {
                  sessionData += v_sigma + "\t" + perdKE + "\r\n";
                }

              if(Mode==11)
                {
                  sessionData += t + "\t" + simErr + "\t" + compt + "\r\n";
                }
              if(Mode==12)
                {
                  sessionData += tol + "\t" + t +"\t" + simErr + "\t" + compt + "\r\n";
                }

              // Prints final results
              System.out.println("\n*****************************************");
              System.out.println("\nSimulation run complete!");
              System.out.println("\nComputational Time: " + compt + "s");

              // Prints period results
              System.out.println("Simulated Time: " + tott + "s");
              System.out.println("Simulated Period: " + tott/turnsDone + "s");
              System.out.println("Expected  Period: " + expT + "s");
              System.out.println("\n" + "Absolute change in period: " + errT + "s");
              System.out.println("Percentage change: " + percErrT + "%");

              // Prints energy and angular momentum results
              System.out.println("\nTotal Intial KE: " + inKE + "J");
              System.out.println("Total Intial PE: " + inPE + "J");
              System.out.println("Total Intial E : " + inTotE + "J");
              System.out.println("Total Intial L : " + inTotL + "kgm^2s^-1");
              System.out.println("Total Final KE: " + fnKE + "J");
              System.out.println("Total Final PE: " + fnPE + "J");
              System.out.println("Total Final E : " + fnTotE + "J");
              System.out.println("Total Final L : " + fnTotL + "kgm^2s^-1");


              System.out.println("\nAbsolute change in kinnetic energy: " + dKE + "J");
              System.out.println("Percentage change: " + perdKE + "%");

              if(cyclotron==true)
                {
                  System.out.println("\nExpected gain in energy: " + totexpdE + "J");
                  System.out.println("Percentage change: " + percexpdE + "%");
                  System.out.println("\nExpected gain in energy (synchronous): " + dKE_syn + "J");
                  System.out.println("Percentage change: " + percexpdE_syn + "%");
                }

              System.out.println("\nAbsolute change in energy: " + errE + "J");
              System.out.println("Percentage change: " + percErrE + "%");

              System.out.println("\nAbsolute change in angular momentum: " + errL + "kgm^2s^-1");
              System.out.println("Percentage change: " + percErrL + "%");

              System.out.println("\nSimulation error: " + simErr + "%");
              System.out.println("\n*****************************************\n");

              if(writeConsvData==true)
                {
                  // Writes error calculations to file in comments to refer to
                  consvData += "\r\n#Absolute change in energy: " + errE + "J"
                             + "\r\n#Percentage change: " + percErrE + "%"
                             + "\r\n#Absolute change in angular momentum: " + errL + "kgm^2s^-1"
                             + "\r\n#Percentage change: " + percErrL + "%";
                }

              simComplete = true; // Sets to true so simulation can end
            }
          else
            {
              // Makes sure simulation does not end if all turns are not complete
              simComplete = false;
            }
      }while(simComplete == false);

      String filename = "./Output/" + timeStamp() + "_"; // Creates filename to write data to

      // If in user defining mode, sets output filename to default name
      if(Mode==1)
        {
          filename += "EMSim_output";


          DataOutput.dataToFile(spreadData,filename + "_spread.txt");
          DataOutput.dataToFile(positionData,filename + "_pos.txt");
          DataOutput.dataToFile(consvData,filename + "_consv.txt");
          DataOutput.dataToFile(boostData,filename + "_boost.txt");
        }

      // If preset mode is active, sets filename to which run the simulation
      // has just completed (altering x,y or z spread to 0.1m and rest to 0)
      if((Mode==2)||(Mode==3))
        {
          if(Mode==2)
            {
              if(runNum==1)
                {
                  filename += "spread_x=0.1m.txt";
                }
              if(runNum==2)
                {
                  filename += "spread_y=0.1m.txt";
                }
              if(runNum==3)
                {
                  filename += "spread_z=0.1m.txt";
                }
            }

          // Sets filename for Mode 3
          if(Mode==3)
            {
              filename += "Failing_Field_Mode_output.txt";
            }

            DataOutput.dataToFile(spreadData,filename);
        }


      // Sets filename to appropiate name for algorithm run for Mode 4 and 5
      if((Mode==4)||(Mode==5))
        {
          if(Mode==4)
            {
              if(runNum==1)
                {
                  filename += "Euler_Test_B";
                }
              if(runNum==2)
                {
                  filename += "EulerCromer_Test_B";
                }
            }
          if(Mode==5)
            {
              if(runNum==1)
                {
                  filename += "Euler_Test_E";
                }
              if(runNum==2)
                {
                  filename += "EulerCromer_Test_E";
                }
            }

            DataOutput.dataToFile(positionData,filename + "_pos.txt");
            DataOutput.dataToFile(consvData,filename + "_consv.txt");
        }

      if(Mode==6)
        {
          filename += "EMSim_output";

          DataOutput.dataToFile(spreadData,filename + "_spread.txt");
          DataOutput.dataToFile(positionData,filename + "_pos.txt");
          DataOutput.dataToFile(boostData,filename + "_boost.txt");
        }

      if((Mode==7)&&(phi==2*Math.PI))
        {
          filename += "PhaseTest";

          DataOutput.dataToFile(sessionData,filename + ".txt");
        }

      if((Mode==8)&&((spread_x==0.1*radius)||(spread_y==0.1*radius)||(spread_z==0.1*radius)))
        {
          if(spread_x==0.1*radius)
            {
              filename += "x_spreadTest";
            }
          if(spread_y==0.1*radius)
            {
              filename += "y_spreadTest";
            }
          if(spread_z==0.1*radius)
            {
              filename += "z_spreadTest";
            }

            DataOutput.dataToFile(sessionData,filename + ".txt");
        }

      if((Mode==9)&&((v_sigma/v)>=0.5))
        {
          filename += "EnergiesTest";

          if(failingField==false)
            {
              filename += "_cyclotron";
            }
          if(failingField==true)
            {
              filename += "_defective-cyclotron";
            }

          DataOutput.dataToFile(sessionData,filename + ".txt");
        }

      if(Mode==10)
        {
          filename += "SpeedofLightTest";

          DataOutput.dataToFile(boostData,filename + ".txt");
        }

      if((Mode==11)&&(t<=t_min))
        {
          filename += algorithmName(M);

          if(cyclotron==true)
            {
              filename += "_cyclotron";
            }
          if(magneticField==true)
            {
              filename += "_non-cyclotron";
            }

          DataOutput.dataToFile(sessionData,filename + "_test.txt");
        }
      if((Mode==12)&&(tol<=1.0e-3))
        {
          filename += "RKF45_testing";

          if(cyclotron==true)
            {
              filename += "_cyclotron";
            }
          if(magneticField==true)
            {
              filename += "_non-cyclotron";
            }

          DataOutput.dataToFile(sessionData,filename + ".txt");
        }
    }

  public static void main(String[] argv)
    {
      // Welcome message to user
      System.out.println("***************WELCOME TO EMSim 5.5 BETA*******************");
      System.out.println("\nThis program simulates a bunch of protons in various preset modes");
      System.out.println("\nSee the README for more details on each mode and how to use this program");

      // User first must pick the mode the program shall run in
      chooseMode();

      // If user defined mode was selected, runs the methods to allow user to
      // input their desired parameters
      if(Mode==1)
        {
          chooseFields();
          chooseAlgorithm();
          enterVelocity();
          enterNumParticle();
          enterTimeStep();
          enterTurnNum();

          runSim();
        }

      // If user selected one of the preset modes, the parameters are configured
      // and the simulation run
      if(Mode!=1)
        {
          modeConfig();
        }

      // Informs user where the output files are to be found and prints exit message
      System.out.println("\n" + "*********************************************");
      System.out.println("Output files can be found in Output folder in the parent directory");
      System.out.println("Thank you for using EMSim!");
      System.out.println("Goodbye :)");
    }
}
// END PROGRAM
