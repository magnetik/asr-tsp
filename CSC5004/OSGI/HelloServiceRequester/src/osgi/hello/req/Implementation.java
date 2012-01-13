package osgi.hello.req;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import osgi.hello.HelloService;

public class Implementation implements Runnable {
	private Boolean end;
	private BundleContext context;

	Implementation (BundleContext context) {
		this.context = context;
		end = false;
	}

	@Override
	public void run() {
		while (!end) {
			ServiceReference refs[];
			try {
				refs = context.getServiceReferences(null, "(objectClass="
						+ HelloService.class.getName() + ")");
				if (refs != null && refs.length != 0) {
					for (ServiceReference servRef : refs) {
						HelloService service = (HelloService) context
						.getService(servRef);
						System.out.println(service.sayHello("les étudiants d'ASR"));
					}
				}
				Thread.sleep(3000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop(){
		end = true;
	}
}