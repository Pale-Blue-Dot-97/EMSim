/**
 * Represents an arbitrary electromagnetic field.  SI units are used throughout.
 *
 * @author Ian Bailey
 * @author Harry Baker
 * @version 1.1
 *
 * Date: 20.03.2019
 *
 */
public class EMField
  {
   	protected PhysicsVector electric; // electric field strength
   	protected PhysicsVector magnetic; // magnetic flux density

    protected static final double k_e =  8.9875517873681764e9; // Coulomb Constant

   	    /**
      	 * Default constructor. Set data members to zero.
      	 *
      	 */
      	public EMField()
          {
        		electric = new PhysicsVector();
        		magnetic = new PhysicsVector();
        	}

   	    /**
      	 * Constructor with two inputs - the electric field strength and magnetic
         * flux density
      	 *
      	 * @param electricIn The electric field strength
      	 * @param magneticIn The magnetic flux density
      	 */
      	public EMField(PhysicsVector electricIn, PhysicsVector magneticIn)
          {
        		electric = new PhysicsVector(electricIn);
        		magnetic = new PhysicsVector(magneticIn);
        	}

        /**
         * Copy constructor for EMField
         *
         * @param oldEMField EMField to be copied
         */
        public EMField(EMField oldEMField)
          {
            this.electric = new PhysicsVector(oldEMField.electric);
            this.magnetic = new PhysicsVector(oldEMField.magnetic);
          }

        /**
      	 *  Set the electric field strength
      	 *
      	 * @param electricIn The electric field strength
      	 */
      	public void setElectric(PhysicsVector electricIn)
          {
        		electric = new PhysicsVector(electricIn);
        	}

      	/**
      	 *  Set the magnetic flux density
      	 *
      	 * @param magneticIn The magnetic flux density
      	 */
      	public void setMagnetic(PhysicsVector magneticIn)
          {
        		magnetic = new PhysicsVector(magneticIn);
        	}

      	/**
      	 *  Return the electric field strength
      	 *
      	 * @return The current value of the electric field strength
       	 */
      	public PhysicsVector getElectric()
          {
        		return new PhysicsVector(electric);
        	}

      	/**
      	 *  Get the magnetic flux density
      	 *
      	 * @return The current value of the magnetic flux density
      	 */
      	public PhysicsVector getMagnetic()
          {
        		return new PhysicsVector(magnetic);
        	}

      	/**
      	 * Returns the acceleration experienced by a charged particle according to
         * the Lorentz force law (non-relativistic).
         *
      	 * @param theParticle - the charged particle moving in the field
      	 * @return the acceleration calculated from (q/m)(E + vXB)
      	 */
      	public PhysicsVector getAcceleration(ChargedParticle theParticle)
        	{
            // New PhysicsVector to hold the new acceleration
            PhysicsVector emAcceleration = new PhysicsVector();

            // a = (q/m)(E + vXB)
            emAcceleration.setVector(electric);
            emAcceleration.increaseBy(PhysicsVector.vectorProduct(theParticle.velocity,magnetic));
            emAcceleration.scale((1.0/theParticle.mass)*theParticle.eCharge);

            return emAcceleration;
        	}
  }
