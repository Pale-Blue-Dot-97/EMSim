import java.io.*;
import java.util.*;
import java.lang.Math;

/**
 * Bunch is a class that is designed to simulate a bunch of charged particles
 *
 * This is acheived by Bunch consisting of an ArrayList of ChargedParticle objects
 *
 * @author Harry Baker
 * @version 5.0
 *
 * Date: 29.03.2019
 *
 */
public class Bunch{

  // Average position and velocity of the ChargedParticles in the bunch
  private PhysicsVector position;
  private PhysicsVector velocity;

  // ArrayList that holds all the ChargedParticles of the bunch
  private ArrayList<ChargedParticle> bunch;

  // Conserved quantities
  private double totKE;
  private double totPE;
  private double totE;
  private double totL;

  /**
   * Default constructor of Bunch
   * Creates a bunch of 10 protons with a spread in the x-direction of
   * sigma = 1.0e-6m
   * Sets the bunch position and velocity to zero
   */
  public Bunch()
    {
      position = new PhysicsVector();
      velocity = new PhysicsVector();
      totKE = 0.0;
      totPE = 0.0;
      totE = 0.0;

      bunch = new ArrayList<ChargedParticle>();

      for(int i=0; i<10; i++)
        {
          // Creates a proton
          ChargedParticle proton = new ChargedParticle(1.60217662e-19,1.6726219e-27);

          // Creates the proton's position with a random x component within sigma
          PhysicsVector r = new PhysicsVector((Math.random()*2*-1e-6 - 1e-6),0,0);

          // Sets this position to the proton and adds it to the bunch
          proton.setPosition(r);
          bunch.add(proton);
        }
    }

  /**
   * Old standard constructor of Bunch
   *
   * Allows the type of ChargedParticle to be determined, the number of particles,
   * the intial velocity of the particles and thus the bunch, and the spread of
   * the particles in x,y,z within the bunch
   *
   * @param particle Type of ChargedParticle the bunch will consist of
   * @param particle_num Number of particles to be in the bunch
   * @param velocityin Intial velocity of all the particles and the bunch
   * @param spread_x Intial maximum spread in x-direction of bunch
   * @param spread_y Intial maximum spread in y-direction of bunch
   * @param spread_z Intial maximum spread in z-direction of bunch
   *
   */
  public Bunch(ChargedParticle particle, int particle_num, PhysicsVector velocityin, double spread_x, double spread_y, double spread_z)
    {
      position = new PhysicsVector(); //Sets bunch position to default (0,0,0)
      velocity = new PhysicsVector(velocityin);

      bunch = new ArrayList<ChargedParticle>();

      // Fills bunch with the number of randomly placed particles defined
      for(int i=0; i<particle_num; i++)
        {
          // Randomly generates the position of the new particle within the limits defined
          double x = (Math.random()*2*spread_x - spread_x);
          double y = (Math.random()*2*spread_y - spread_y);
          double z = (Math.random()*2*spread_z - spread_z);
          PhysicsVector r = new PhysicsVector(x,y,z);

          // Creates the new particle to be a copy of the intial particle inputted
          ChargedParticle newParticle = new ChargedParticle(particle);

          //Sets this particle to have the position randomly generated above
          newParticle.setPosition(r);

          // All particles have same intial velocity
          newParticle.setVelocity(velocityin);

          bunch.add(newParticle); //Adds new particle to bunch
        }

      totKE = 0.0;
      totPE = 0.0;
      totE = 0.0;
    }

  /**
   * New standard constructor of Bunch
   *
   * Allows the type of ChargedParticle to be determined, the number of particles,
   * the intial velocity of the particles and thus the bunch, and the spread of
   * the particles in x,y,z within the bunch
   *
   * @param particle Type of ChargedParticle the bunch will consist of
   * @param particle_num Number of particles to be in the bunch
   * @param velocityin Intial velocity of all the particles and the bunch
   * @param spread_x Intial maximum spread in x-direction of bunch
   * @param spread_y Intial maximum spread in y-direction of bunch
   * @param spread_z Intial maximum spread in z-direction of bunch
   *
   */
  public Bunch(ChargedParticle particle, int particle_num, double v, double v_sigma, double spread_x, double spread_y, double spread_z)
    {
      position = new PhysicsVector(); //Sets bunch position to default (0,0,0)
      velocity = new PhysicsVector(0.0,v,0.0);

      double v_upper = v + v_sigma;
      double v_lower = v - v_sigma;

      bunch = new ArrayList<ChargedParticle>();

      // Fills bunch with the number of randomly placed particles defined
      for(int i=0; i<particle_num; i++)
        {
          // Randomly generates the position of the new particle within the limits defined
          double x = (Math.random()*2*spread_x - spread_x);
          double y = (Math.random()*2*spread_y - spread_y);
          double z = (Math.random()*2*spread_z - spread_z);
          PhysicsVector r = new PhysicsVector(x,y,z);

          // Creates the new particle to be a copy of the intial particle inputted
          ChargedParticle newParticle = new ChargedParticle(particle);

          //Sets this particle to have the position randomly generated above
          newParticle.setPosition(r);

          double random_v = (Math.random()*(v_upper - v_lower)) + v_lower;
          //System.out.println(random_v);
          PhysicsVector particle_v = new PhysicsVector(0.0,random_v,0.0);
          newParticle.setVelocity(particle_v);

          bunch.add(newParticle); //Adds new particle to bunch
        }

      totKE = 0.0;
      totPE = 0.0;
      totE = 0.0;
    }

  /**
   * Copy constructor of Bunch
   *
   * @param oldbunch Bunch to be copied
   *
   */
  public Bunch(Bunch oldbunch)
    {
      //Sets the new bunch to have the same position and velocity as the old
      position = new PhysicsVector(oldbunch.position);
      velocity = new PhysicsVector(oldbunch.velocity);
      bunch = new ArrayList<ChargedParticle>();

      // Loops through each particle in the old bunch and copies it to a new
      // particle in the new bunch
      for(int i=0;i<oldbunch.bunch.size();i++)
        {
          //Uses the copy constructor of ChargedParticle to copy to new particle
          ChargedParticle nParticle = new ChargedParticle(oldbunch.bunch.get(i));
          bunch.add(nParticle);
        }

      this.totKE = oldbunch.totKE;
      this.totPE = oldbunch.totPE;
      this.totE = oldbunch.totE;
    }

  /**
   * Method to calculate the average position of the bunch from the positions of
   * the particles within the bunch using r_avg = sum^n_i(r_i)/n
   */
  public void CalcAvgPos()
    {
      // PhysicsVector to hold the average position of the bunch
      PhysicsVector bunch_centre = new PhysicsVector();

      //Loops through the bunch, adding each particle's position to bunch_centre
      for(int i=0;i<bunch.size();i++)
        {
          bunch_centre.increaseBy(bunch.get(i).getPosition());
        }
      // Divides the sum of the bunch's particle's positions by the number of
      // particles to find the average position
      // r_avg = sum^n_i(r_i)/n
      bunch_centre.scale(1.0/bunch.size());

      // Updates the position of bunch with new position calculation
      position.setVector(bunch_centre);
    }

  /**
   * Method to calculate the average velocity of the bunch from the velocities of
   * the particles within the bunch using v_avg = sum^n_i(v_i)/n
   *
   */
  public void CalcAvgVel()
    {
      // PhysicsVector to hold the average velocity of the bunch
      PhysicsVector bunch_velocity = new PhysicsVector();

      //Loops through the bunch, adding each particle's velocity to bunch_centre
      for(int i=0;i<bunch.size();i++)
        {
          bunch_velocity.increaseBy(bunch.get(i).getVelocity());
        }
      // Divides the sum of the bunch's particle's velocities by the number of
      // particles to find the average velocity
      // v_avg = sum^n_i(v_i)/n
      bunch_velocity.scale(1.0/bunch.size());

      // Updates the velocity of bunch with new velocity calculation
      velocity.setVector(bunch_velocity);
    }

  /**
   * Method to re-align the bunch to the origin after the particles have been
   * randomly placed in the bunch. These random placements will result in the
   * bunch not being located exactly at the origin so a re-alignment is required
   *
   * This is acheived by calculating the average position of the bunch using
   * CalcAvgPos() and then subtracting the position from the origin off every
   * particle in the bunch, thus re-aligning the bunch onto the origin
   *
   */
  public void reAlign()
    {
      // Calculates average position to find the correction vector needed
      CalcAvgPos();

      // Loops over bunch to subtract the average position from every particle
      for(int i=0;i<bunch.size();i++)
        {
          bunch.get(i).getPosition().decreaseBy(position);
        }
      // Re-calculates the average position which should now be the origin
      CalcAvgPos();
    }

  /**
   * Get method for the average position of the bunch
   *
   * @return Average position of the bunch referenced to origin
   */
  public PhysicsVector getAvgPos()
    {
      return position;
    }
  /**
   * Get method for the average velocity of the bunch
   *
   * @return Average velocity of the bunch referenced to origin
   */
  public PhysicsVector getAvgVel()
    {
      return velocity;
    }

  /**
   * Gets a particle from the bunch
   *
   * @param particle The index entry number for the particle desired
   * @return The ChargedParticle in the index defined by particle
   */
  public ChargedParticle getParticle(int particle)
    {
      return bunch.get(particle);
    }

  /**
   * Gets the bunch
   *
   * @return The bunch ArrayList
   */
  public ArrayList<ChargedParticle> getBunch()
    {
      return bunch;
    }

  /**
   * Gets the size of the bunch, i.e the number of particles
   *
   * @return The number of particles in the bunch
   */
  public int getNumParticle()
    {
      return bunch.size();
    }

  /**
   * Gets the total kinnetic energy of the bunch
   *
   * @return Total kinnetic energy of bunch
   */
  public double getTotKE()
    {
      return totKE;
    }

  /**
   * Gets the total potential energy of the bunch
   *
   * @return Total potential energy of bunch
   */
  public double getTotPE()
    {
      return totPE;
    }

  /**
   * Gets the total energy of the bunch
   *
   * @return Total energy of bunch
   */
  public double getTotE()
    {
      return totE;
    }

  /**
   * Gets the total angular momentum of the bunch
   *
   * @return Total angular momentum of bunch
   */
  public double getTotL()
    {
      return totL;
    }

  /**
   * Method to calculate the relative distance of a particle in a bunch from
   * the centre of the bunch
   *
   * @param particle Particle in bunch for relative distance to centre to be calculated
   * @return The relative distance of particle from the centre of the bunch
   */
  public PhysicsVector calcRelDist(ChargedParticle particle)
    {
      PhysicsVector relDist = new PhysicsVector();
      relDist.setVector(PhysicsVector.subtract(particle.getPosition(),position));
      return relDist;
    }

  /**
   * Method to calculate the spread of the particles in the x-direction from
   * the bunch centre
   *
   * @return The magnitude of the maximum distance in x from the bunch centre of any particle
   */
  public double calcSpreadx()
    {
      // First sets the first particle to be at the greatest distance in x
      double max_x = Math.abs(calcRelDist(bunch.get(0)).getX());

      // Now loops over the bunch, comparing the magnitude of the x component
      // of the relative distance of each particle from the bunch centre
      // compared to that of the largest found thus far
      for(int i=1;i<bunch.size();i++)
        {
          if(max_x < Math.abs(calcRelDist(bunch.get(i)).getX()))
            {
              max_x = Math.abs(calcRelDist(bunch.get(i)).getX());
            }
        }
      return max_x;
    }

  /**
   * Method to calculate the spread of the particles in the y-direction from
   * the bunch centre
   *
   * @return The magnitude of the maximum distance in y from the bunch centre of any particle
   */
  public double calcSpready()
    {
      // First sets the first particle to be at the greatest distance in y
      double max_y = Math.abs(calcRelDist(bunch.get(0)).getY());

      // Now loops over the bunch, comparing the magnitude of the y component
      // of the relative distance of each particle from the bunch centre
      // compared to that of the largest found thus far
      for(int i=1;i<bunch.size();i++)
        {
          if(max_y < Math.abs(calcRelDist(bunch.get(i)).getY()))
            {
              max_y = Math.abs(calcRelDist(bunch.get(i)).getY());
            }
        }
      return max_y;
    }

   /**
    * Method to calculate the spread of the particles in the z-direction from
    * the bunch centre
    *
    * @return The magnitude of the maximum distance in z from the bunch centre of any particle
    */
  public double calcSpreadz()
    {
      // First sets the first particle to be at the greatest distance in z
      double max_z = Math.abs(calcRelDist(bunch.get(0)).getZ());

      // Now loops over the bunch, comparing the magnitude of the z component
      // of the relative distance of each particle from the bunch centre
      // compared to that of the largest found thus far
      for(int i=1;i<bunch.size();i++)
        {
          if(max_z < Math.abs(calcRelDist(bunch.get(i)).getZ()))
            {
              max_z = Math.abs(calcRelDist(bunch.get(i)).getZ());
            }
        }
      return max_z;
    }

  /**
   * Runs the method from ChargedParticle class to update the electric Field
   * vector for each particle due to a point charge
   *
   * @param pointCharge Charge of point charge at centre of orbit of bunch
   */
  public void updateElectric(ChargedParticle pointCharge)
    {
      for(int i=0;i<bunch.size();i++)
        {
          bunch.get(i).updateElectric(pointCharge);
        }
    }

  /**
   * Calculates the kinnetic, potential and total energy of the bunch
   *
   * @param Q Charge of point charge at centre of bunch orbit
   * @param centre Position of point charge and centre of bunch orbit
   * @param B Magnetic field strength
   */
  public void calcEnergy(double Q, PhysicsVector centre,PhysicsVector B)
    {
      // 'cleans' conserved quantities ready for new values
      this.totKE = 0.0;
      this.totPE = 0.0;
      this.totE = 0.0;

      // Calculates kinnetic energy of each particle in bunch and sums together
      for(int i=0;i<bunch.size();i++)
        {
          bunch.get(i).calcKE();
          this.totKE += bunch.get(i).getKE();
        }

      // Calculates potential energy of each particle in bunch and sums together
      for(int i=0;i<bunch.size();i++)
        {
          bunch.get(i).calcPE(Q,centre,B);
          this.totPE += bunch.get(i).getPE();
        }

      // Sums potential and kinnetic energy together for total energy of bunch
      this.totE = this.totKE + this.totPE;
    }

  /**
   * Calculates the total angular momentum of the bunch
   *
   * @param centre Position of the centre of the bunch's orbit
   */
  public void calcAngularMomentum(PhysicsVector centre)
    {
      // 'cleans' the angular momentum ready for a new value
      this.totL = 0.0;

      // Creates a new vector to sum the angular momenta of each particle
      PhysicsVector totLVector = new PhysicsVector();

      for(int i=0;i<bunch.size();i++)
        {
          // Angular momentum of this particle
          PhysicsVector l = new PhysicsVector();

          // Displacement of this particle from centre of orbit
          PhysicsVector r = new PhysicsVector();

          // Calculates displacement of particle from centre of orbit
          r.setVector(PhysicsVector.subtract(bunch.get(i).getPosition(),centre));

          // Calculates the angular momentum of this particle
          // l = m*(r x v)
          l.setVector(PhysicsVector.vectorProduct(r,bunch.get(i).getVelocity()));
          l.scale(bunch.get(i).getMass());

          // Vector sums to total angular momentum of bunch
          totLVector.increaseBy(l);
        }

      // Converts vector angular momentum into magnitude for comparison
      this.totL = totLVector.magnitude();
    }

  public void setLorentzForce(EMField magField, ChargedParticle pointCharge, EMOscField eField,
  double l, boolean magneticField, boolean failingField, boolean electricPointCharge, boolean cyclotron)
    {
      // Loops over the bunch to calculate and set the acceleration on each
      // particle from the EM field
      for(int i=0;i<bunch.size();i++)
        {
          // Creates a new PhysicsVector to hold the new acceleration
          PhysicsVector newA = new PhysicsVector();

          // Sets acceleration based on a uniform magnetic field
          if((magneticField==true)||((cyclotron==true)&&(failingField==false)))
            {
              // Sets newA to the acceleration on the particle due to the Lorentz force
              newA.setVector(magField.getAcceleration(bunch.get(i)));
            }

          // Uses a reduced by 10% magnetic field for x<0
          if(failingField==true)
            {
              EMField reducedMagField = new EMField();
              reducedMagField.setMagnetic(PhysicsVector.scale(0.9,magField.getMagnetic()));

              if(bunch.get(i).getPosition().getX()>=0.0)
                {
                  // Sets newA to the acceleration on the particle due to the Lorentz force
                  newA.setVector(magField.getAcceleration(bunch.get(i)));
                }
              if(bunch.get(i).getPosition().getX()<0.0)
                {
                  // Sets newA to the acceleration on the particle due to the Lorentz force
                  newA.setVector(reducedMagField.getAcceleration(bunch.get(i)));
                }
            }

          // Uses an electric point charge. Extracts E vectors from each particle
          if(electricPointCharge==true)
            {
              // Calculates E for this particle based on the point charge
              updateElectric(pointCharge);
              // Sets newA to the acceleration on the particle due to the point charge
              newA.setVector(bunch.get(i).getEMField().getAcceleration(bunch.get(i)));
            }

          // calculates lorentz force based on B and E field and checks if particle
          // is within the accelerating gap of the cyclotron
          if(cyclotron==true)
            {
              // Adds acceleration due to accelerating E field if particle is
              // within boundaries of the gap defined by l
              if((bunch.get(i).getPosition().getY()<l)&&(bunch.get(i).getPosition().getY()>-l))
                {
                  newA.increaseBy(eField.getAcceleration(bunch.get(i)));
                }
            }

          // Sets newA to the particle's acceleration attribute
          bunch.get(i).setAcceleration(newA);
        }
    }

  public double calcEGain(double t, double V, double B, double phi)
    {
      double dE = 0;
      for(int i=0;i<bunch.size();i++)
        {
          dE += bunch.get(i).calcEGain(t,V,B,phi);
        }
      return dE;
    }

  public double calcESynGain(double t, double V, double B, double phi)
    {
      double dE_syn = 0;
      for(int i=0;i<bunch.size();i++)
        {
          dE_syn += bunch.get(i).calcESynGain(t,V,B,phi);
        }
      return dE_syn;
    }

  /**
   * Prints the position of every particle in the bunch
   */
  public void printPos()
    {
      for(int i=0;i<bunch.size();i++)
        {
          bunch.get(i).getPosition().print();
        }
    }

  /**
   * Runs the Euler algorithm on every particle in the bunch
   * r(t+dt) = r(t) + v(t)dt
	 * v(t+dt) = v(t) + a(t)dt
	 * Where dt is the time step, t is the intial time of the iteration
   *
   * @param t Time-step of the algorithm
   */
  public void euler(double t)
    {
      for(int i=0;i<bunch.size();i++)
        {
          // Uses Euler algorithm method in Particle
          bunch.get(i).euler(t);
        }
    }

  /**
   * Runs the Euler-Cromer algorithm on every particle in the bunch
   * v(t+dt) = v(t) + a(t)dt
	 * r(t+dt) = r(t) + v(t+dt)dt
	 * Where dt is the time step, t is the intial time of the iteration
   *
   * @param t Time-step of the algorithm
   */
  public void eulerCromer(double t)
    {
      for(int i=0;i<bunch.size();i++)
        {
          // Uses Euler-Cromer algorithm method in Particle
          bunch.get(i).eulerCromer(t);
        }
    }

  public void verletPtOne(double t)
    {
      // Now each particle gets moved forward by using the first half of the
      // Verlet algorithm
      for(int i=0; i<bunch.size(); i++)
        {
          bunch.get(i).verletPtOne(t);
        }
    }

  public void verletPtTwo(PhysicsVector[] a_in, double t)
    {
      // The average acceleration is then calculated to find the velocities
      // of each body at the end of the time-step using the second half of the
      // Verlet algorithm
      for(int i=0; i<bunch.size(); i++)
        {
          bunch.get(i).verletPtTwo(a_in[i],t);
        }
    }

  public void findMidPoint(Bunch in, Bunch fn)
    {
      for(int i=0;i<bunch.size();i++)
        {
          this.bunch.get(i).findMidPoint(in.bunch.get(i),fn.bunch.get(i));
        }
    }

  public void findkn(Bunch in, Bunch fn)
    {
      for(int i=0;i<bunch.size();i++)
        {
          this.bunch.get(i).findkn(in.bunch.get(i),fn.bunch.get(i));
        }
    }

  public void findVertex(double s[], Bunch bunches_n[])
    {
      for(int i=0;i<bunch.size();i++)
        {
          ChargedParticle kn[] = new ChargedParticle[6];
          for(int j=0;j<6;j++)
            {
              kn[j] = new ChargedParticle(bunches_n[j].bunch.get(i));
            }

          this.bunch.get(i).findVertex(s,kn);
        }
    }

  public void eulerAhead(double t, Bunch otherPoint)
    {
      for(int i=0;i<bunch.size();i++)
        {
          this.bunch.get(i).eulerAhead(t,otherPoint.bunch.get(i));
        }
    }

  public void heun(double t, Bunch ahead)
    {
      for(int i=0;i<bunch.size();i++)
        {
          this.bunch.get(i).heun(t,ahead.bunch.get(i));
        }
    }

  public void rK4(Bunch k1, Bunch k2, Bunch k3, Bunch k4)
    {
      for(int i=0;i<bunch.size();i++)
        {
          this.bunch.get(i).rK4(k1.bunch.get(i),k2.bunch.get(i),k3.bunch.get(i),k4.bunch.get(i));
        }
    }

  public double rKF45(double t, double tol, Bunch bunches_n[])
    {
      double opt_dt = 0.0;
      for(int i=0;i<bunch.size();i++)
        {
          ChargedParticle kn[] = new ChargedParticle[6];
          for(int j=0;j<6;j++)
            {
              kn[j] = new ChargedParticle(bunches_n[j].bunch.get(i));
            }

          opt_dt += this.bunch.get(i).rKF45(t,tol,kn);
        }
      double avgOptdt = opt_dt/bunch.size();
      return avgOptdt;
    }


  /**
   * Method to send the entire characteristics of the bunch to string
   *
   * @return String containing a formatted output of the bunch characteristics
   */
  public String bunchToString()
    {
      String bunch_makeup = "STATE OF BUNCH";
      bunch_makeup += "\r\n**************** BUNCH CHARACTERISTICS **********************" +
                      "\r\nNUMBER OF PARTICLES | " + bunch.size() +
                      "\r\nAVERAGE POSITION    | " + this.position.toString() +
                      "\r\nAVERAGE VELOCITY    | " + this.velocity.toString() +
                      "\r\n***************** PARTICLES IN BUNCH ************************";
      for(int i=0;i<bunch.size();i++)
        {
          bunch_makeup += "\r\n" + bunch.get(i).cpToString();
        }

      return bunch_makeup;
    }
}
