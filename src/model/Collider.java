package model;

/**
 * 
 * Collider class to store user wanted
 * physical and trigger colliders of map
 * 
 * @author Pedro Sampaio
 * @since 1.7
 *
 */
public class Collider {
	private boolean isTrigger;		// is it a trigger or a physical collider?
	private int indI;				// the i-index of collider
	private int indJ;				// the j-index of collider
	private int id;					// id of collider (1 - physical, 2 - trigger)
	
	/**
	 * Constructor of collider object
	 * 
	 * @param id		is it a trigger or a physical collider?
	 * @param indI		the i-index of collider
	 * @param indJ		the j-index of collider
	 * @param isTrigger	id of collider (1 - physical, 2 or more trigger colliders with different id)
	 */
	public Collider(int id, int indI, int indJ, boolean isTrigger) {
		this.isTrigger = isTrigger;
		this.indI = indI;
		this.indJ = indJ;
		this.id = id;
	}
	
	/**
	 * @return the isTrigger
	 */
	public boolean isTrigger() {
		return isTrigger;
	}
	/**
	 * @param isTrigger the isTrigger to set
	 */
	public void setTrigger(boolean isTrigger) {
		this.isTrigger = isTrigger;
	}
	/**
	 * @return the indI
	 */
	public int getIndI() {
		return indI;
	}
	/**
	 * @param indI the indI to set
	 */
	public void setIndI(int indI) {
		this.indI = indI;
	}
	/**
	 * @return the indJ
	 */
	public int getIndJ() {
		return indJ;
	}
	/**
	 * @param indJ the indJ to set
	 */
	public void setIndJ(int indJ) {
		this.indJ = indJ;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
}
