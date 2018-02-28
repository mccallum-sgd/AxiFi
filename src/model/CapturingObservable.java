package model;

import java.util.Observable;
import java.util.Observer;

abstract class CapturingObservable <O> extends Observable implements Cloneable {
	private Class<O> thisClass;
	private O capture; // capture previous state before change for updating
	
	protected CapturingObservable(Class<O> thisClass) {
		this.thisClass = thisClass;
	}
	
	/**
	 * Captures the state of this CapturingObservable before changes.
	 * Call this before any alterations to state.
	 */
	protected void captureState() {
		capture = (O)clone();
	}
	
	/**
	 * Calls {@link Observable#setChanged()} and then {@link Observable#notifyObservers(Object)} passing in the 
	 * clone captured before the update with {@link CapturingObservable#captureState()}.
	 * 
	 * @throws IllegalStateException - if captureState() was not called before the update or clone() returns null
	 */
	protected void notifyUpdate() {
		if (capture == null || !thisClass.isInstance(capture))
			throw new IllegalStateException("Either captureState() was not called before notifyUpdate() or clone() returns null.");
		setChanged();
		notifyObservers(capture);
	}

	protected abstract O clone();

	public static void main (String[] args) {
		Observer adminListener = new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				CsAdmin oldAdmin = null;
				CsAdmin newAdmin = null;
				try {
					oldAdmin = (CsAdmin)arg;
				} catch (ClassCastException e) {
					System.out.println("New admin isn't a CsAdmin");
				}
				try {
					newAdmin = (CsAdmin)o;
				} catch (ClassCastException e) {
					System.out.println("New admin isn't a CsAdmin");
				}
				
				
				System.out.println("OldAdmin received: " + oldAdmin.getUsername() + "\n" +
									"NewAdmin received: " + newAdmin.getUsername());
			}
		};
		CsAdmin admin = new CsAdmin("testuser", "pass", "mr", "test");
		admin.addObserver(adminListener);
		admin.setUsername("newtestuser");
	}
}
