
/*
 JavActProbe tests the presence of JVM Javact on a list of adresses
 Copyright (C) 2008-2010 Sebastien Leriche, sebastien.leriche@it-sudparis.eu

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author leriche
 * 
 */
public class JavActProbe implements Callable<String> {

    private String address;

    private int port;

    private static final int ADDRESSSTART = 2; // 1 is usually the router

    private static final int ADDRESSEND = 254; // 255 is a broadcast adress

    private static final int TIMEOUT = 500;

    private static ArrayList<String> addressList = new ArrayList<String>();

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static ArrayList<Future<String>> futureList = new ArrayList<Future<String>>();

    public synchronized static String[] probe(int port) {
        futureList.clear();

        for (int i = ADDRESSSTART; i < ADDRESSEND; i++) {
            futureList.add(threadPool.submit(new JavActProbe(
                    "157.159.110." + i, port)));
        }

        try {
            threadPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (Exception e) {
        }

        threadPool.shutdownNow();

        try {
            for (Future<String> future : futureList)
                if (future.isDone() && future.get() != null)

                    addressList.add(future.get());
        } catch (Exception e) {
        }

        return addressList.toArray(new String[0]);
    }

    private JavActProbe(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String call() {
        boolean result = false;

        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(address, port), TIMEOUT);
            result = socket.isConnected();
            socket.close();
          } catch (Exception e) {
          }

        return (result) ? address + ":" + port : null;
    }

}