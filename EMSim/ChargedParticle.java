/**
 * ChargedParticle is a sub-class of Particle, designed to model charged
 * particles. It has the same properties of Particle with the addition of
 * electric charge and an EMField component. Has been designed so that future
 * sub-class of ChargedParticle can be created.
 *
 * @author Harry Baker
 * @version 3.1
 *
 * Date: 05.03.2019
 *
 * VERSION UPDATES
 * -Added EMField component to allow ChargedParticle to generate a EMField
 *  or to hold EM vectors for calculations
 * -Added method to update the electric field vector based on particle's
 *  position from a charge
 * -Added methods to calculate potential energy due to moving in an EM field
 * -Also added Coulomb constant for ease of calculations
 */
public class ChargedParticle extends Particle implements Comparable{

  protected double eCharge;// protected so that future sub-classes may acsess
                           // this variable (i.e electron class etc)
  protected EMField emField;

  protected static final double k_e =  8.9875517873681764e9; // Coulomb Constant

  /**
   * Basic constructor inheriting the Particle basic constructor
   *
   */
  public ChargedParticle()
    {
      super();
      eCharge = 0;
      emField = new EMField();
    }

  /**
   * Constructor that allows setting of electric charge but only basic Particle
   * constructor
   *
   * @param inECharge Desired charge of particle
   *
   */
  public ChargedParticle(double inECharge)
    {
      super();
      eCharge = inECharge;
      emField = new EMField();
    }

  /**
   * Constructor that allows setting of mass and electric charge
   *
   * @param inECharge Desired charge of particle
   * @param inMass Desired mass of particle
   */
  public ChargedParticle(double inECharge, double inMass)
    {
      super(inMass);
      eCharge = inECharge;
      emField = new EMField();
    }

  /**
   * Constructor that allows setting of electric charge, mass, position and
   * velocity
   *
   * @param inECharge Desired charge of particle
   * @param inMass Desired mass of particle
   * @param inPosition Desired initial position of particle
   * @param inVelocity Desired iniitial velocity of particle
   */
  public ChargedParticle(double inECharge, double inMass, PhysicsVector inPosition, PhysicsVector inVelocity)
    {
      super(inMass,inPosition,inVelocity);
      eCharge = inECharge;
      emField = new EMField();
    }

  /**
   * Constructor that allows the setting of all parameters from creation
   *
   * @param inECharge Desired charge of particle
   * @param inMass Desired mass of particle
   * @param inPosition Desired initial position of particle
   * @param inVelocity Desired initial velocity of particle
   * @param inAcceleration Desired initial acceleration of particle
   */
  public ChargedParticle(double inECharge,double inMass,PhysicsVector inPosition,PhysicsVector inVelocity,PhysicsVector inAcceleration)
    {
      super(inMass, inPosition, inVelocity, inAcceleration);
      eCharge = inECharge;
      emField = new EMField();
    }

  /**
   * Copying constructor that copys another ChargedParticle into a new instance
   *
   * @param copyCParticle ChargedParticle instance to be copied
   */
  public ChargedParticle(ChargedParticle copyCParticle)
    {
      super(copyCParticle.mass,copyCParticle.position,copyCParticle.velocity,copyCParticle.acceleration);
      this.eCharge = copyCParticle.eCharge;
      this.emField = new EMField(copyCParticle.emField);
    }

  /**
   * Sets charge of ChargedParticle instance
   * May be less useful with instances that represent fundamental particles
   *
   * @param inECharge New charge for instance's charge to be set to
   */
  public void setCharge(double inECharge)
    {
      eCharge = inECharge;
    }

  /**
   * Allows the setting of the EMField component of this ChargedParticle to
   * electric and magnetic field vectors
   *
   * @param electricIn New electric field vector
   * @param magneticIn New magnetic field vector
   */
  public void setEMField(PhysicsVector electricIn, PhysicsVector magneticIn)
    {
      emField.setElectric(electricIn);
      emField.setMagnetic(magneticIn);
    }

  /**
   * Allows the setting of the EMField component of this ChargedParticle to a
   * EMField object as input
   *
   * @param emIn New EM field
   */
  public void setEMField(EMField emIn)
    {
      emField.setElectric(emIn.getElectric());
      emField.setMagnetic(emIn.getMagnetic());
    }

  /**
   * Method to fetch the electric charge of ChargedParticle instance
   *
   * @return Electric charge of particle
   */
  public double getCharge()
    {
      return(eCharge);
    }

  /**
   * Gets the EMField of this ChargedParticle in the form of an EMField object
   *
   * @return EM field of this ChargedParticle
   */
  public EMField getEMField()
    {
      return this.emField;
    }

  /**
   * Gets the electric field vector
   *
   * @return Electric field vector of EMField component of this ChargedParticle
   */
  public PhysicsVector getElectric()
    {
      return this.emField.getElectric();
    }

  /**
   * Gets the magnetic field vector
   *
   * @return Magnetic field vector of EMField component of this ChargedParticle
   */
  public PhysicsVector getMagnetic()
    {
      return this.emField.getMagnetic();
    }

  /**
   * Gets the coulomb constant stored in this class for calculations
   *
   * @return Coulomb constant
   */
  public double getCoulombConst()
    {
      return k_e;
    }

  /**
   * Calculates the potential energy of the ChargedParticle due to EM fields.
   * Then sums together to give total potential energy
   *
   * U_E = (k_e*q*Q)/r_Qq
   * U_B = -0.5*q*dot(B,(r x v))
   *
   * @param Q Charge of point charge at centre of orbit
   * @param centre Position of point charge and/or centre of orbit
   * @param B Magnetic field vector
   */
  public void calcPE(double Q, PhysicsVector centre,PhysicsVector B)
    {
      // Displacement of particle from centre of orbit
      PhysicsVector displacement = new PhysicsVector();
      displacement.setVector(PhysicsVector.subtract(this.position,centre));

      double uE = (k_e*this.eCharge*Q)/(displacement.magnitude());

      double uB = -0.5*this.eCharge*(PhysicsVector.dot(B,PhysicsVector.vectorProduct(displacement,this.velocity)));

      this.pe = uE + uB;
    }

  public double calcEGain(double t, double V, double B, double phi)
    {
      double omega = (this.eCharge*B)/(this.mass);
      double dphase = (this.position.getY()*this.eCharge*B)/(this.velocity.getY()*this.mass);
      double dE = 2.0*this.eCharge*V*Math.sin(omega*t + phi + dphase);
      return dE;
    }

  public double calcESynGain(double t, double V, double B, double phi)
    {
      double omega = (this.eCharge*B)/(this.mass);
      double dE_syn = 2.0*this.eCharge*V*Math.sin(omega*t + phi);
      return dE_syn;
    }

  /**
   * Updates the electric field vector for this particle based on the position
   * and charge of a point charge influencing this particle
   *
   * @param pointCharge Point charge influencing this particle
   */
  public void updateElectric(ChargedParticle pointCharge)
    {
      // Fresh vector to hold new E
      PhysicsVector newElectric = new PhysicsVector();

      // Displacement of this particle from the point charge
      PhysicsVector displacement = new PhysicsVector();
      displacement.setVector(PhysicsVector.subtract(this.position,pointCharge.getPosition()));

      // E = (k_e*Q)/(|r|^3)*r_Qq
      newElectric.setVector(displacement);
      newElectric.scale((k_e*pointCharge.eCharge)/(Math.pow(displacement.magnitude(),3)));

      // Sets new E to E in EMField component of this particle
      this.emField.setElectric(newElectric);
    }

  /**
   * Comparable method for the y-position of a ChargedParticle to another
   *
   * @param other ChargedParticle to be compared to
   *
   * @return -1 if y-position is smaller than other ChargedParticle's, 0 if same, 1 if more
   *
   */
  public int compareTo(Object other) throws ClassCastException
    {
      double mySize = Math.abs(this.getPosition().getY());
    	double otherSize = Math.abs(((ChargedParticle) other).getPosition().getY());

      if (mySize< otherSize)
        {
      		return(-1);
      	}
    	else if (mySize==otherSize)
        {
      		return(0);
      	}
    	else
      	{
      		return(1);
      	}
    }

  /**
   * Returns a string detailing the components of the ChargedParticle
   * Uses the toString() method from Particle
   *
   * @return String detailing the components of ChargedParticle
   */
  public String cpToString()
    {
      return " Electric Charge "+eCharge + this.toString();
    }
}
