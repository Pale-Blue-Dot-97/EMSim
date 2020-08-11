/**
 * A Class to represent a massive particle
 * It can have position, velocity, acceleration and mass
 *
 * Includes several algorithms to update the position and velocity of the
 * particle. Algorithms included:
 *
 * -Euler
 * -Euler-Cromer
 * -Verlet
 * -Heun's
 * -RK4
 * -RKF45
 *
 * @author Alex Finch
 * @author Ian Bailey
 * @author Harry Baker
 * @version 3.0
 */
public class Particle{

	protected double mass; //the mass of the particle
	protected PhysicsVector position, velocity, acceleration;
	protected double ke; // Kinnetic Energy
	protected double pe; // Potential Energy

	/**
	 * The Default Constructor. Sets everything to zero.
	 *
	 */
	public Particle()
		{
			mass = 0;
			position = new PhysicsVector();
			velocity = new PhysicsVector();
			acceleration= new PhysicsVector();
			ke = 0;
			pe = 0;
		}

	/**
	 * Constructor with one input, the mass of the particle. Set everything else to zero.
	 * @param mIn mass of the particle
	 */
	public Particle(double mIn)
		{
			mass = mIn;
			position = new PhysicsVector();
			velocity = new PhysicsVector();
			acceleration= new PhysicsVector();
			ke = 0;
			pe = 0;
		}

	/**
	 *  Constructor that sets mass, position and velocity
	 *  @param mIn mass of the particle
	 *  @param positionIn initial position of particle
	 *  @param velocityIn initial velocity of particle
	 */
	public Particle(double mIn,PhysicsVector positionIn,PhysicsVector velocityIn)
		{
			mass = mIn;
			position = new PhysicsVector(positionIn);
			velocity = new PhysicsVector(velocityIn);
			acceleration= new PhysicsVector();
			calcKE();
			pe = 0;
		}

	/**
	 * Constructor that sets mass, position, velocity and acceleration
	 * @param mIn mass of the particle
	 * @param positionIn intial position of particle
	 * @param velocityIn intial position of particle
	 * @param accelerationIn intial acceleration of particle
	 */
	public Particle(double mIn,PhysicsVector positionIn,PhysicsVector velocityIn,PhysicsVector accelerationIn)
		{
			mass = mIn;
			position = new PhysicsVector(positionIn);
			velocity = new PhysicsVector(velocityIn);
			acceleration = new PhysicsVector(accelerationIn);
			calcKE();
			pe = 0;
		}

	/**
	 * Return the position
	 *
	 * @return position
	 */
	public PhysicsVector getPosition()
		{
			return new PhysicsVector(position);
		}

	/**
	 * Return the velocity
	 *
	 * @return velocity
	 */
	public PhysicsVector getVelocity()
		{
			return new PhysicsVector(velocity);
		}

	/**
	 * Return the acceleration
	 *
	 * @return acceleration
	 */
	public PhysicsVector getAcceleration()
		{
			return new PhysicsVector(acceleration);
		}


	/**
	 * Return the mass
	 *
	 * @return mass
	 */
	public double getMass()
		{
			return mass;
		}

	/**
	 * Get the kinetic energy
	 *
	 * @return Kinetic energy
	 */
	public double getKE()
		{
			return ke;
		}

	/**
	 * Get the potential energy
	 *
	 * @return Potential energy
	 */
	public double getPE()
		{
			return pe;
		}

	/**
	 * Gets the total energy
	 *
	 * @return Total energy
	 */
	public double getTotEnergy()
		{
			return ke + pe;
		}

	/**
	 * Set the mass
	 *
	 * @param massIn The new mass
	 */
	public void setMass(double massIn)
		{
			mass = massIn;
		}

	/**
	 * Set the position
	 *
	 * @param pIn The new position
	 */
	public void setPosition(PhysicsVector pIn)
		{
			position = new PhysicsVector(pIn);
		}

	/**
	 * Set the velocity
	 *
	 * @param velocityIn The new velocity
	 */
	public void setVelocity(PhysicsVector velocityIn)
		{
			velocity = new PhysicsVector(velocityIn);
		}

	/**
	 * Set the acceleration
	 *
	 * @param accIn The new acceleration
	 */
	public void setAcceleration(PhysicsVector accIn)
		{
			acceleration= new PhysicsVector(accIn);
		}

	/**
	 * Sets the kinetic energy of this particle to input
	 *
	 * @param keIn New kinetic energy
	 */
	public void setKE(double keIn)
		{
			ke = keIn;
		}

	/**
	 * Sets the potential energy of this particle to input
	 *
	 * @param peIn New potential energy
	 */
	public void setPE(double peIn)
		{
			pe = peIn;
		}

	/**
	 * Calculates the kinetic energy of the particle from KE = 0.5*m*v^2
	 * and stores in the ke parameter of this object
	 */
	public void calcKE()
		{
			ke = 0.5*mass*Math.pow(velocity.magnitude(),2);
		}

	/**
	 * Update the position and velocity of the particle subject to a constant acceleration for a time.
	 * After the time has passed the acceleration reverts to its previous value.
	 *
	 * @param deltaTime  The change in time
	 * @param accelIn    The applied acceleration
	 */
	public void update(double deltaTime, PhysicsVector accelIn)
		{
			PhysicsVector savedAcceleration = acceleration;

			// apply the new acceleration for a short time
			acceleration = new PhysicsVector(accelIn);
			update(deltaTime);

			// revert acceleration to previous value
			acceleration = savedAcceleration;
		}

	/**
	 * Update the position and velocity of the particle after a short time has
	 * passed when the particle is experiencing the acceleration stored in the class.
	 * Applies the formula s = ut + 1/2 at**2 to the position
	 * Applies the formula v=u+at to the velocity
	 * @param deltaTime  The change in time
	 */
	public void update(double deltaTime)
		{
			position.increaseBy(PhysicsVector.scale(deltaTime, velocity)); // old position + ut
			position.increaseBy(PhysicsVector.scale(0.5*deltaTime*deltaTime, acceleration)); // + 1/2 at**2

			velocity.increaseBy(PhysicsVector.scale(deltaTime, acceleration)); // v = u + at
		}

	/**
	 * Uses the Euler alogrithm to move a body foward by a time step
	 * r(t+dt) = r(t) + v(t)dt
	 * v(t+dt) = v(t) + a(t)dt
	 * Where dt is the time step, t is the intial time of the iteration
	 *
	 * @param t Time step of the iteration
	 *
	 */
	public void euler(double t)
		{
			position.increaseBy(PhysicsVector.scale(t,velocity));
			velocity.increaseBy(PhysicsVector.scale(t,acceleration));
		}

	/**
	 * Uses the Euler-Cromer alogrithm to move a body foward by a time step
	 * v(t+dt) = v(t) + a(t)dt
	 * r(t+dt) = r(t) + v(t+dt)dt
	 * Where dt is the time step, t is the intial time of the iteration
	 *
	 * @param t Time step of the iteration
	 *
	 */
	public void eulerCromer(double t)
		{
			velocity.increaseBy(PhysicsVector.scale(t,acceleration));
			position.increaseBy(PhysicsVector.scale(t,velocity));
		}

	/**
	 * Implements the first part of the Verlet alogrithm to move a body foward
	 * by a time step.
	 * r(t+dt) = r(t) + v(t)dt + 0.5a(t)(dt)^2
	 * Where dt is the time step, t is the intial time of the iteration
	 *
	 * @param t Time step of the iteration
	 *
	 */
	public void verletPtOne(double t)
		{
			position.increaseBy(PhysicsVector.add(PhysicsVector.scale(t,velocity),
			PhysicsVector.scale(0.5*t*t,acceleration)));
		}

	/**
	 * Implements the last part of the Verlet algorithm to move a body foward
	 * by a time step
	 * v(t+dt) = v(t) + 0.5*(a(t+dt) + a(t))dt
	 * Where dt is the time step, t is the intial time of the iteration
	 *
	 * @param t Time step of the iteration
	 * @param a_in Average acceleration of body over time-step
	 *
	 */
	public void verletPtTwo(PhysicsVector a_in, double t)
		{
			// Calculates average acceleration across the time-step for the body
			PhysicsVector aAvg = new PhysicsVector();
			aAvg.setVector(PhysicsVector.scale(
			0.5,PhysicsVector.add(acceleration,a_in)));

			// Sets the velocity of the body at v(t+dt)
			velocity.increaseBy(PhysicsVector.scale(t,aAvg));
		}

	/**
	 * Uses Heun's alogrithm to move a body foward by a time-step
	 *
	 * First calculates an estimate of the iteration using Euler-Cromer
	 * v*(t+dt) = v(t) + a(t)dt
	 * r*(t+dt) = r(t) + v(t+dt)dt
	 * Where dt is the time step, t is the intial time of the iteration
	 *
	 * Acceleration is re-calculated for this estimate, a*(t+dt), outside of Particle
	 *
	 * Then uses this end-point to produce a better estimate of the iteration,
	 * which occurs in this method using:
	 * r(t+dt) = r(t) + 0.5(v(t) + v*(t+dt))dt
	 * v(t+dt) = v(t) + 0.5(a(t) + a*(t+dt))dt
	 *
	 * @param t Time step of the iteration
	 * @param ahead Intial estimate of the Particle at the end of the iteration
	 *
	 */
	public void heun(double t, Particle ahead)
		{
			this.position.increaseBy(PhysicsVector.scale(0.5*t,PhysicsVector.add(
			this.velocity,ahead.velocity)));
			this.velocity.increaseBy(PhysicsVector.scale(0.5*t,PhysicsVector.add(
			this.acceleration,ahead.acceleration)));
		}

	/**
	 * Finds the midpoint between two instanctes of a particle displaced in time
	 * and sets this to copy of this particle intended for that purpose
	 *
	 * r(t+0.5dt) = r(t) + 0.5(r(t+dt) - r(t))
	 * v(t+0.5dt) = v(t) + 0.5(v(t+dt) - v(t))
	 * Where dt is the time step, t is the intial time of the iteration
	 *
	 * @param in Intial particle instance
	 * @param fn Final particle instance
	 */
	public void findMidPoint(Particle in, Particle fn)
		{
			this.position.setVector(PhysicsVector.add(in.position,PhysicsVector.scale(0.5,
			PhysicsVector.subtract(fn.position,in.position))));

			this.velocity.setVector(PhysicsVector.add(in.velocity,PhysicsVector.scale(0.5,
			PhysicsVector.subtract(fn.velocity,in.velocity))));
		}

	/**
	 * Finds k_n by subtracting off the intial state
	 * k_n = r(t) = k_n(r(t+s*dt)) - r(t)
	 *		 = v(t) = k_n(v(t+s*dt)) - v(t)
	 * where s is some scalar value specific to k_n
	 *
	 * @param in Intial state
	 * @param fn Final state for this k_n and dt is the time-step
	 */
	public void findkn(Particle in, Particle fn)
		{
			this.position.setVector(PhysicsVector.subtract(fn.position,in.position));
			this.velocity.setVector(PhysicsVector.subtract(fn.velocity,in.velocity));
		}

	/**
	 * Finds the particle's velocity and position at some factor of the time-step
	 * ahead using a modified form of the Euler method
	 * v(t+s*dt) = v(t) + a(t+s*dt)dt
	 * r(t+s*dt) = r(t) + v(t+s*dt)dt
	 * where s is some scalar value specific 'otherPoint' and dt is the time-step
	 *
	 * @param t Time-step of iteration
	 * @param otherPoint Some point in the time-step
	 */
	public void eulerAhead(double t, Particle otherPoint)
		{
			this.velocity.increaseBy(PhysicsVector.scale(t,otherPoint.acceleration));
			this.position.increaseBy(PhysicsVector.scale(t,otherPoint.velocity));
		}

	/**
	 * Finds the some increment of the particle's state within the time-step
	 * defined by an addition of contributions from k_n's weighted by a set of
	 * scalars
	 * r(t + S*dt) = s[0]*k[1] + s[1]*k[1] + s[2]*k[2] + s[3]*k[3] + s[4]*k[4] + s[5]*k[5]
	 * v(t + S*dt) = s[0]*k[1] + s[1]*k[1] + s[2]*k[2] + s[3]*k[3] + s[4]*k[4] + s[5]*k[5]
	 *
	 */
	public void findVertex(double[] s, Particle[] kn)
		{
			this.position.setVector(PhysicsVector.add(PhysicsVector.add(
			PhysicsVector.scale(s[0],kn[0].position),
			PhysicsVector.scale(s[1],kn[1].position)),
			PhysicsVector.add(PhysicsVector.add(
			PhysicsVector.scale(s[2],kn[2].position),
			PhysicsVector.scale(s[3],kn[3].position)),
			PhysicsVector.add(
			PhysicsVector.scale(s[4],kn[4].position),
			PhysicsVector.scale(s[5],kn[5].position)))));

			this.velocity.setVector(PhysicsVector.add(PhysicsVector.add(
			PhysicsVector.scale(s[0],kn[0].velocity),
			PhysicsVector.scale(s[1],kn[1].velocity)),
			PhysicsVector.add(PhysicsVector.add(
			PhysicsVector.scale(s[2],kn[2].velocity),
			PhysicsVector.scale(s[3],kn[3].velocity)),
			PhysicsVector.add(
			PhysicsVector.scale(s[4],kn[4].velocity),
			PhysicsVector.scale(s[5],kn[5].velocity)))));
		}

	public void rK4(Particle k1, Particle k2, Particle k3, Particle k4)
		{
			this.velocity.setVector(PhysicsVector.scale(1.0/6.0,PhysicsVector.add(
			PhysicsVector.add(k1.velocity,PhysicsVector.scale(2.0,k2.velocity)),
			PhysicsVector.add(PhysicsVector.scale(2.0,k3.velocity),k4.velocity))));

			this.position.setVector(PhysicsVector.scale(1.0/6.0,PhysicsVector.add(
			PhysicsVector.add(k1.position,PhysicsVector.scale(2.0,k2.position)),
			PhysicsVector.add(PhysicsVector.scale(2.0,k3.position),k4.position))));
		}

	public double rKF45(double t, double tol, Particle[] kn)
		{
			Particle y = new Particle(this.mass,this.position,this.velocity);
			double[] sy = new double[6];
			sy[0] = 1.0;
			sy[1] = 25.0/216.0;
			sy[2] = 1408.0/2565.0;
			sy[3] = 2197.0/4101.0;
			sy[4] = -0.2;
			sy[5] = 0;
			y.findVertex(sy,kn);

			Particle z = new Particle(this.mass,this.position,this.velocity);
			double[] sz = new double[6];
			sz[0] = 1.0;
			sz[1] = 16.0/135.0;
			sz[2] = 6656.0/12825.0;
			sz[3] = 28561.0/56430.0;
			sz[4] = -1*(9.0/50.0);
			sz[5] = 2.0/55.0;
			z.findVertex(sz,kn);

			double posDiff = PhysicsVector.subtract(y.position,z.position).magnitude();
			double velDiff = PhysicsVector.subtract(y.velocity,z.velocity).magnitude();

			if(posDiff>=tol*Math.abs(z.position.magnitude()))
				{
					this.position.setVector(z.position);
				}
			if(posDiff<tol*Math.abs(z.position.magnitude()))
				{
					this.position.setVector(y.position);
				}

			if(velDiff>=tol*Math.abs(z.velocity.magnitude()))
				{
					this.velocity.setVector(z.velocity);
				}
			if(velDiff<tol*Math.abs(z.velocity.magnitude()))
				{
					this.velocity.setVector(y.velocity);
				}

			double optPosdt = Math.pow(((tol*t)/(2.0*posDiff)),0.25);
			double optVeldt = Math.pow(((tol*t)/(2.0*velDiff)),0.25);

			double avgOptdt = 0.5*(optPosdt+optVeldt);

			return avgOptdt;
		}

	/**
	 * Create a string containing the mass, position, velocity, and acceleration of the particle.
	 * This method is called automatically by System.out.println(someparticle)
	 * @return string with the format
	 * " mass "+mass+" Position: "+position+" Velocity: "+velocity+" Acceleration: "+acceleration
	 */
	public String toString()
		{
			return " mass "+mass+" Position: "+position.returnSimpleString()+" Velocity: "+velocity.returnSimpleString()+" Acceleration: "+acceleration.returnSimpleString();
		}
}
