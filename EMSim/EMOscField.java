import java.lang.Math;

/**
 * EMOscField is an extension of EMField, designed to simulate a sinosudially
 * oscillating field
 *
 *
 * @author Harry Baker
 * @version 1.1
 *
 * Date 08.04.2019
 *
 */
public class EMOscField extends EMField{

  protected PhysicsVector E; // Amplitude of electric field
  protected PhysicsVector B; // Amplitude of magnetic field

  protected PhysicsVector w_E; // Angular frequency of E-field
  protected PhysicsVector w_B; // Angular frequency of B-field

  protected PhysicsVector phi_E; // Phase of E-field
  protected PhysicsVector phi_B; // Phase of B-field

  /**
   * Default constructor of EMOscField
   *
   * Uses the default constructor of EMField and then sets all data members to
   * zero
   *
   */
  public EMOscField()
    {
      super();
      this.E = new PhysicsVector(0.0,0.0,0.0);
      this.B = new PhysicsVector(0.0,0.0,0.0);
      this.phi_E = new PhysicsVector(0.0,0.0,0.0);
      this.phi_B = new PhysicsVector(0.0,0.0,0.0);
      this.w_E = new PhysicsVector(0.0,0.0,0.0);
      this.w_B = new PhysicsVector(0.0,0.0,0.0);
    }

  /**
   * Standard constructor of EMOscField
   *
   * Takes in all the desired parameters of the field to construct a EM field
   * with the desired phase, angular frequency and amplitude
   *
   * @param E_in Desired amplitude of electric field in x,y,z directions
   * @param B_in Desired amplitude of magnetic field in x,y,z directions
   * @param phi_E_in Desired phase of electric field in x,y,z directions
   * @param phi_B_in Desired phase of magnetic field in x,y,z directions
   * @param w_E_in Desired angular frequency of electric field in x,y,z directions
   * @param w_B_in Desired angular frequency of magnetic field in x,y,z directions
   *
   */
  public EMOscField(PhysicsVector E_in, PhysicsVector B_in, PhysicsVector phi_E_in,
  PhysicsVector phi_B_in, PhysicsVector w_E_in, PhysicsVector w_B_in)
    {
      super(); // Constructs default EMField component

      this.E = new PhysicsVector(E_in);
      this.B = new PhysicsVector(B_in);
      this.phi_E = new PhysicsVector(phi_E_in);
      this.phi_B = new PhysicsVector(phi_B_in);
      this.w_E = new PhysicsVector(w_E_in);
      this.w_B = new PhysicsVector(w_B_in);
    }

  /**
   * Copy constructor of EMOscField
   *
   * @param copyEM EMOscField to be copied
   *
   */
  public EMOscField(EMOscField copyEM)
    {
      super(copyEM.electric,copyEM.magnetic);
      this.E = new PhysicsVector(copyEM.E);
      this.B = new PhysicsVector(copyEM.B);
      this.phi_E = new PhysicsVector(copyEM.phi_E);
      this.phi_B = new PhysicsVector(copyEM.phi_B);
      this.w_E = new PhysicsVector(copyEM.w_E);
      this.w_B = new PhysicsVector(copyEM.w_B);
    }

  /**
   * Updates the angular frequency of the electric field with a new value for
   * omega that scales the unit vector of the old angular frequency
   *
   * @param w New angular frequency of the electric field
   */
  public void updateOmega(double w)
    {
      this.w_E.setVector(PhysicsVector.scale(w,this.w_E.getUnitVector()));
    }

  /**
   * Update the EMOscField based on the current time of the simulation it is
   * embedded in
   *
   * @param t Current time of simulation
   *
   */
  public void updateEM(double t)
    {
      // New values of E field in each direction using
      // E(t) = E_o*Sin(wt + phi)
      double newE_x = this.E.getX()*Math.sin(this.w_E.getX()*t + this.phi_E.getX());
      double newE_y = this.E.getY()*Math.sin(this.w_E.getY()*t + this.phi_E.getY());
      double newE_z = this.E.getZ()*Math.sin(this.w_E.getZ()*t + this.phi_E.getZ());

      // New values of B field in each direction using
      // B(t) = B_o*Sin(wt + phi)
      double newB_x = this.B.getX()*Math.sin(this.w_B.getX()*t + this.phi_B.getX());
      double newB_y = this.B.getY()*Math.sin(this.w_B.getY()*t + this.phi_B.getY());
      double newB_z = this.B.getZ()*Math.sin(this.w_B.getZ()*t + this.phi_B.getZ());

      // Creates new PhysicsVectors to hold E(t) and B(t)
      PhysicsVector newElectric = new PhysicsVector(newE_x,newE_y,newE_z);
      PhysicsVector newMagnetic = new PhysicsVector(newB_x,newB_y,newB_z);

      // Sets the electric and magnetic components of EMOscField object to
      // E(t) and B(t) just calculated
      this.electric.setVector(newElectric);
      this.magnetic.setVector(newMagnetic);
    }
}
