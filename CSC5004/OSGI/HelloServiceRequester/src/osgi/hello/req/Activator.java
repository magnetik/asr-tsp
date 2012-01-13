package osgi.hello.req;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import osgi.hello.HelloService;

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
	public void stop(BundleContext context) throws Exception {
		t.stop();
	}

}
