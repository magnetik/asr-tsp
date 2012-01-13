package osgi.hello.fr;

import osgi.hello.HelloService;

public class HelloServiceFr implements HelloService {

	@Override
	public String sayHello(String name) {
		return "Bonjour " + name;
	}

}
