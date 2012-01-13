package osgi.hello.en;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import osgi.hello.HelloService;

public class Activator implements BundleActivator {
	private HelloService service;
	private ServiceRegistration reg;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting HelloServiceEn");
		
		service = new HelloServiceEn();
		
		reg = context.registerService(HelloService.class.getName(), service, null);
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (reg != null) {
			reg.unregister();
			reg = null;
		}
		
	}

}
