package osgi.capteur.temp;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import osgi.capteur.CapteurService;


public class Activator implements BundleActivator {
	private CapteurService service;
	private ServiceRegistration reg;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting CapteurServiceTemp");
		
		service = new CapteurServiceTemp();
		
		reg = context.registerService(CapteurService.class.getName(), service, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (reg != null) {
			reg.unregister();
			reg = null;
		}
		
	}

}
