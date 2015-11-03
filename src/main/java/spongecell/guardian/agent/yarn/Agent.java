package spongecell.guardian.agent.yarn;


/**
 * @author jbrinnand
 */
public interface Agent {
	/**
	 * Get the status of  managed object
	 * or component in an Infrastructure.
	 */
	public abstract void getStatus();

}