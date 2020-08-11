/**
 * A 3-dimensional vector class to be used in the development of
 * computer simulations of various physical systems.
 *
 *
 * The 3-dimensional vector represents a standard cartesian vector
 * given by <b> xi + yj +zk</b> where <b>i</b>, <b>j</b>, and <b>k</b> are unit
 * vectors in the x, y, and z direction respectively.
 *
 * @author Iain A. Bertram
 * @author Harry Baker
 * @version 1.4
 */
public class PhysicsVector {
	// Fix the dimension of the array representing the vectors
	private static final int  vectorSize=3;

	// In this case we have a three dimensional vector, the x component is [0] with y[1] and z [2]
	private double[]  vectorComponents = new double[vectorSize];

  /**
   * Default constructor that creates a PhysicsVector with zero magnitude
   */
  public PhysicsVector()
		{
			for (int i=0; i<vectorComponents.length; i++)
				{
					vectorComponents[i] =0.;
				}
    }


  /**
   * Constructor that takes two arguments to create a PhysicsVector
   * with the first argument x giving the component in the <b>i</b>
   * direction and the second argument y giving the component in the
   * <b>j</b> direction. The z component is set to zero.
	 *
	 * @param x Double to be set to x-component of vector
	 * @param y Double to be set to y-component of vector
	 *
   */
  public PhysicsVector(double x, double y)
		{
			vectorComponents[0] = x;
			vectorComponents[1] = y;
			vectorComponents[2] = 0.;
	  }

  /**
   * Constructor that takes three arguments to create a PhysicsVector
   * with the first argumentgiving the component in the <b>i</b>
   * direction, the second argument giving the component in the
   * <b>j</b> direction and the third argument giving the component
   * in the <b>k</b> direction.
	 *
	 * @param x Double to be set to x-component of vector
	 * @param y Double to be set to y-component of vector
	 * @param z Double to be set to z-component of vector
	 *
   */
	public PhysicsVector(double x, double y, double z)
		{
			vectorComponents[0] = x;
			vectorComponents[1] = y;
			vectorComponents[2] = z;
		}

  /**
   * Constructor that takes an array of length 3 and sets the components of
	 * the vector to the values stored in the array. If array length = 2 it sets
	 * the x and y components. Returns a default vector if the array is not
   * length 2 or 3 and prints a warning message.
	 *
	 * @param x Array to be converted to a PhysicsVector
	 *
   */
  public PhysicsVector(double [] x)
		{
			if (x.length == vectorSize )
				{
					for (int i=0; i<vectorComponents.length; i++)
						{
							vectorComponents[i] = x[i];
						}
				}

			else if (x.length == vectorSize-1 )
				{
					for (int i=0; i<x.length; i++)
						{
							vectorComponents[i] = x[i];
						}
					vectorComponents[vectorComponents.length-1] = 0.;
				}
			else
				{
					this.setVector(new PhysicsVector());
					System.out.println(" WARNING: PhysicsVector(double [] x) requires an array of length " + vectorSize);
				}
	  }


  /**
   * Constructor that creates a vector with the same value as the
   * PhysicsVector passed as an argument.
	 *
	 * @param v PhysicsVector to be copied
	 *
   */
  public PhysicsVector(PhysicsVector v)
		{
		// Note to students: need to copy values not the array itself.
			for (int i=0; i<vectorComponents.length; i++)
				{
					vectorComponents[i] = v.vectorComponents[i];
				}
	  }

  /**
   * Method to return a String which represents the vector and has
   * the form <b> xi + yj</b>.
	 *
   * @return String <b> xi + yj</b>
	 *
   */
  public String return2DString()
		{
			String text = ""+vectorComponents[0]+"i ";

			if (vectorComponents[1] < 0)
				{
					text += " -"+(-1*vectorComponents[1]);
				}
			else
				{
					text += " +"+(vectorComponents[1]);
				}

			text+="j ";
			return text;
	  }

  /**
   * Method to return a String which represents the vector and has
   * the form <b> x y</b> (I.e. separated with spaces).
	 *
   * @return String <b>x y</b>
	 *
   */
  public String returnSimple2DString()
		{
			String text = "";
			text += vectorComponents[0] +" ";
			text += vectorComponents[1] +" ";
			return text;
	  }


  /**
   * Method to return a String which represents the vector and has
   * the form <b> xi + yj +zk</b>.
	 *
   * @return String <b> xi + yj + zk</b>
	 *
   */
  public String returnString()
		{
			String text = ""+vectorComponents[0]+"i ";

			if (vectorComponents[1] < 0)
				{
					text += " -"+(-1*vectorComponents[1]);
				}
			else
				{
					text += " +"+(vectorComponents[1]);
				}

			text+="j ";

			if (vectorComponents[2] < 0)
				{
					text += " -"+(-1*vectorComponents[2]);
				}
			else
				{
					text += " +"+(vectorComponents[2]);
				}

			text+="k ";
			return text;
	  }

  /**
   * Method to return a String which represents the vector and has
   * the form <b>x y z</b> (I.e. only separated by spaces).
	 *
   * @return String <b>"x y z"</b>
	 *
   */
  public String returnSimpleString()
		{
			String text = "";

			for (int i=0; i<vectorComponents.length; i++)
				{
					text+=vectorComponents[i]+" ";
				}

			return text;
	  }

  /**
   * Prints the vector to 'standard out'. Note: it will include
   * a carriage return. Prints: <b> xi + yj</b>
   *
   */
  public void print2D()
		{
			String text = this.return2DString();
			System.out.println(text);
	  }

  /**
   * Prints the vector to 'standard out'. Note: it will include
   * a carriage return. Prints: <b> xi + yj + zk</b>
   *
   */
  public void print()
		{
			String text = this.returnString();
			System.out.println(text);
	  }

  /**
   * Change the value of the vector to <b> xi + yj + zk</b>
   *
   * @param x <code>x-component</code> of the vector
   * @param y <code>y-component</code> of the vector
   * @param z <code>z-component</code> of the vector
	 *
   */
  public void setVector(double x, double y, double z)
		{
			vectorComponents[0] = x;
			vectorComponents[1] = y;
			vectorComponents[2] = z;
	  }

  /**
   * Change the value of the vector to <b> xi + yj</b>.
	 * The z-component is set to zero.
   *
   * @param x <code>x-component</code> of the vector
   * @param y <code>y-component</code> of the vector
	 *
   */
  public void setVector(double x, double y)
		{
			vectorComponents[0] = x;
			vectorComponents[1] = y;
			vectorComponents[2] = 0.;
	  }

  /**
   * Modify the vector so that it is equal to vector <b>v</b>
   *
   * @param v the vector that is then used to set the values of the
   * modified vector.
	 *
   */
  public void setVector(PhysicsVector v)
		{
			for (int i=0; i<vectorComponents.length; i++)
				{
					vectorComponents[i] = v.vectorComponents[i];
				}
	  }

  /**
   * Add a vector <b>v</b> to the original vector. Normal vector
   * addition is carried out. I.e. the x-components are added and
   * the y components are added, etc.
   *
   * @param v vector to be added to original vector.
	 *
   */
  public void increaseBy(PhysicsVector v)
		{
			for (int i=0; i<vectorComponents.length; i++)
				{
					vectorComponents[i] += v.vectorComponents[i];
				}
	  }

  /**
   * Subtract a vector <b>v</b> from the original vector. Normal vector
   * addition is carried out. I.e. the x-components are subtracted and
   * the y components are subtracted, etc.
   *
   * @param v Vector to be subtracted from the original vector.
	 *
   */
  public void decreaseBy(PhysicsVector v)
		{
			for (int i=0; i<vectorComponents.length; i++)
				{
					vectorComponents[i] -= v.vectorComponents[i];
				}
	  }

  /**
   * Returns the magnitude of the vector. If the vector is given by
   * <b>xi +yj +zk</b> Then the magnitude is Math.sqrt(x*x + y*y + zz)
   *
   * @return A scalar with the magnitude
	 *
   */
	public double magnitude()
		{
			double mag = dot(this,this);
			mag = Math.sqrt(mag);
			return mag;
		}

  /**
   * return the x-component of the vector
   *
   * @return x-component
	 *
   */
  public double getX()
		{
			return vectorComponents[0];
	  }

  /**
   * return the y-component of the vector
   *
   * @return y-component#
	 *
   */
  public double getY()
		{
			return vectorComponents[1];
	  }

  /**
   * return the z-component of the vector
   *
   * @return z-component
	 *
   */
  public double getZ()
		{
			return vectorComponents[2];
	  }

  /**
   * Returns a vector with magnitude of one and the origibal
   * direction of the original vector. I.e. scale the vector by
   * 1/magnitude.
   *
   * If vector is a zero vector then returns zero length vector
   *
   *
   * @return unit vector with direction of original vector
	 *
   */
	public PhysicsVector getUnitVector()
		{
			PhysicsVector unit = new PhysicsVector(this);
			double magnitude = this.magnitude();

			if (Math.abs(magnitude) > 1.e-34)
				{
					unit.scale(1/magnitude);
				}
			else
				{
					unit = new PhysicsVector();
				}

			return unit;
		}

  /**
   * scale the components of the vector by the scalar. I.e. If the
   * scalar is s then the vector transfoms to <b>s*xComponent i +
   * s*yComponent j</b>.
   *
   * @param x the factor to scale the vector by.
	 *
   */
  public void scale(double x)
		{
			for (int i=0; i<vectorComponents.length; i++)
				{
					vectorComponents[i] *= x;
				}
	  }

  /**
   * Scale the components of the vector by a scalar. I.e. If the
   * scalar is s then the vector transfoms to <b>s*xComponent i +
   * s*yComponent j</b>.
   *
   * @param x The factor to scale the vector by.
   * @param v The vector to scale
	 *
	 * @return The scaled vector
   */
  public static PhysicsVector scale(double x, PhysicsVector v)
		{
			PhysicsVector scaled = new PhysicsVector(v);

			for (int i=0; i<scaled.vectorComponents.length; i++)
				{
					scaled.vectorComponents[i] *=x;
				}
			return scaled;
	  }

  /**
   * Dot is a standard vector scalar product. If <b> v = xi + yj</b>
   * and <b>u = wi + zy</b>, then the dot product returns <b>xw +
   * yz</b>
   *
   * @param v first vector in product
   * @param u second vector in product
	 *
   * @return return scalar product
	 *
   */
  public static double dot(PhysicsVector v, PhysicsVector u)
		{
			double product = 0.;

			for(int i=0; i<vectorSize; i++)
				{
					product += v.vectorComponents[i]*u.vectorComponents[i];
				}
	    return product;
	  }

	/**
	 * vectorProduct is a standard 3D vector product.
	 *
	 * @param v first vector in cross product
	 * @param u second vector in cross product
	 *
	 * @return return vector product
	 *
	 */
	public static PhysicsVector vectorProduct(PhysicsVector v, PhysicsVector u)
		{
			PhysicsVector cross = new PhysicsVector();

			cross.vectorComponents[0] += v.vectorComponents[1]*u.vectorComponents[2]
			- v.vectorComponents[2]*u.vectorComponents[1];
			cross.vectorComponents[1] += v.vectorComponents[2]*u.vectorComponents[0]
			- v.vectorComponents[0]*u.vectorComponents[2];
			cross.vectorComponents[2] += v.vectorComponents[0]*u.vectorComponents[1]
			- v.vectorComponents[1]*u.vectorComponents[0];

			return cross;
		}

  /**
   * standard vector addition. If <b> v = xi + yj</b>
   * and <b>u = wi + zy</b>, then the method returns a vector
   * <b>(x+w)i + (y+z)j</b>
   *
   * @param v first vector in sum
   * @param u second vector in sum
	 *
   * @return return summed vector
	 *
   */
  public static PhysicsVector add(PhysicsVector v, PhysicsVector u)
		{
			PhysicsVector sum = new PhysicsVector(v);
			sum.increaseBy(u);
			return sum;
	  }

  /**
   * vector subtraction. If <b> v = xi + yj</b> and <b>u = wi +
   * zy</b>, then the vector returned is <b>(x-w)i +
   * (y-z)j</b>
   *
   * @param v first vector in sum
   * @param u second vector in sum
	 *
   * @return return resultant vector
	 *
   */
  public static PhysicsVector subtract(PhysicsVector v, PhysicsVector u)
		{
			PhysicsVector sum = new PhysicsVector(v);
			sum.decreaseBy(u);
			return sum;
	  }
		
}
