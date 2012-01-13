package osgi.hello.en;

import osgi.hello.HelloService;

public class HelloServiceEn implements HelloService {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}

}
