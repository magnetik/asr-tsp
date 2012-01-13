package osgi.capteur.req;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {
	BundleContext context;
	Boolean end;
	Thread t;
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context; // Sauvegarde du contexte

		Implementation imp = new Implementation(context);
		t = new Thread(imp);
		t.start();
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		t.stop();
	}

}
