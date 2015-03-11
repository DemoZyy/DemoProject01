package com.pubnub.api;

import java.util.*;

public class OriginManager {
    private static Logger log = new Logger(OriginManager.class);
    HashSet<String> deadOrigins = new HashSet<String>();
    private int interval;
    private int maxRetries;
    private int maxRetriesAfterFailure;
    private int currentOriginManager;
    private int failbackOriginManager;
    private PubnubCoreShared app;
    private String http;

    public OriginManager(PubnubCoreShared app) {
        System.out.println("Origin Manager created");
        this.app = app;
        this.http = app.http();
        this.interval = app.getOriginHeartbeatInterval();
        this.maxRetries = app.getOriginHeartbeatMaxRetries();
        this.maxRetriesAfterFailure = app.getOriginHeartbeatIntervalAfterFailure();
    }

    public void start() {
        startCurrentOriginManager();
        startFailbackOriginManager();
    }

    public void stop() {
        stopCurrentOriginManager();
        stopFailbackOriginManager();
    }

    private void startCurrentOriginManager() {
        System.out.println("Origin Manager started");
        currentOriginManager = 
                app.getTimedTaskManager().addTask("Current Origin Manager", new CurrentOriginManager(interval));
    }

    private void startFailbackOriginManager() {
        if (deadOrigins.size() != 0) {
            failbackOriginManager =
                    app.getTimedTaskManager().addTask("Failback Manager", new FailbackOriginManager(interval));
            System.out.println("Failback Manager STARTED");
        }
    }

    private void stopCurrentOriginManager() {
        if (currentOriginManager != 0) {
            app.getTimedTaskManager().removeTask(currentOriginManager);
            currentOriginManager = 0;
            System.out.println("Current Origin Manager STOP");
        }
    }

    private void stopFailbackOriginManager() {
        if (failbackOriginManager != 0) {
            app.getTimedTaskManager().removeTask(failbackOriginManager);
            failbackOriginManager = 0;
            System.out.println("Failback Manager stopped");
        } else {
            System.out.println("Failback Manager is NULL");
        }
    }

    public boolean isCurrentOriginManagerRunning() {
        System.out.println("OM state requested: " + (currentOriginManager != 0));
        return currentOriginManager != 0;
    }

    public boolean isFailbackManagerRunning() {
        return failbackOriginManager != 0;
    }

    public class CurrentOriginManager extends TimedTask {
        Date lastAlivePingStart;
        String currentOrigin;
        int failures;

        CurrentOriginManager(int interval) {
            super(interval);
        }

        public void run() {
            lastAlivePingStart = new Date();
            Set<String> originsPool = app.getOriginsPool();
            Iterator originsIterator = originsPool.iterator();

            if (!originsIterator.hasNext()) {
                log.verbose("Origins Pool is empty");
                return;
            }

            try {
                currentOrigin = (String) originsIterator.next();

                // TODO: remove
                System.out.println("Pinging origin " + currentOrigin + "." + app.getDomain() + "/time/0");

                isOriginOnline(currentOrigin, new Callback() {
                    public void successCallback(String channel, Object message) {
                        failures = 0;
                        checkFailures();
                    }

                    public void errorCallback(String channel, PubnubError error) {
                        failures += 1;
                        checkFailures();
                    }
                });
            } catch (Exception e) {
                failures += 1;
                checkFailures();
            }
        }

        private void checkFailures() {
            if (failures > maxRetries) {
                failures = 0;
                setOriginOffline(currentOrigin);
                restartSubscription();
                if (!isFailbackManagerRunning()) {
                    startFailbackOriginManager();
                }
            }
        }
    }

    public class FailbackOriginManager extends TimedTask {
        Date lastFailbackPingStart;
        String deadOriginToTest;
        HashMap<String, Integer> deadOriginsSuccessCounter = new HashMap<String, Integer>();

        FailbackOriginManager(int interval) {
            super(interval);
        }

        public void run() {
            // last_failback_ping_start
            synchronized (deadOrigins) {
                Iterator deadOriginsIterator = deadOrigins.iterator();

                lastFailbackPingStart = new Date();

                try {
                    while (deadOriginsIterator.hasNext()) {
                        deadOriginToTest = (String) deadOriginsIterator.next();

                        isOriginOnline(deadOriginToTest, new Callback() {
                            public void successCallback(String channel, Object message) {
                                // TODO: remove
                                System.out.println("Dead origin " + deadOriginToTest + " is online now");
                                setSuccesses(deadOriginToTest);
                                checkSuccesses(deadOriginToTest);
                            }

                            public void errorCallback(String channel, PubnubError error) {
                                // TODO: remove
                                System.out.println("Dead origin " + deadOriginToTest + " is still offline");
                                setFailure(deadOriginToTest);
                                checkSuccesses(deadOriginToTest);
                            }
                        });
                    }
                } catch (Exception e) {
                    setFailure(deadOriginToTest);
                    checkSuccesses(deadOriginToTest);
                }
            }
        }

        private void setSuccesses(String deadOrigin) {
            Integer counter = deadOriginsSuccessCounter.get(deadOrigin);

            if (counter == null) {
                counter = 0;
            }

            deadOriginsSuccessCounter.put(deadOrigin, counter);
        }

        private void setFailure(String deadOrigin) {
            deadOriginsSuccessCounter.put(deadOrigin, 0);
        }

        private void checkSuccesses(String deadOrigin) {
            Integer counter = deadOriginsSuccessCounter.get(deadOrigin);

            if (counter == maxRetriesAfterFailure) {
                deadOriginsSuccessCounter.remove(deadOrigin);
                setOriginOnline(deadOriginToTest);
                restartSubscription();

                if (deadOrigins.isEmpty()) {
                    interruptWorker();
                }
            }
        }
    }

    private void setOriginOffline(String origin) {
        synchronized (this.deadOrigins) {
            app.getOriginsPool().remove(origin);
            deadOrigins.add(origin);
        }

        log.verbose("Marked origin \"" + origin + "\" as offline.");
    }

    private void setOriginOnline(String origin) {
        synchronized (this.deadOrigins) {
            deadOrigins.remove(origin);
            app.getOriginsPool().add(origin);
        }

        log.verbose("Marked origin \"" + origin + "\" as online.");
    }

    private void isOriginOnline(String origin, final Callback callback) {
        String[] urlComponents = new String[]{http + origin + "." + app.getDomain(), "time", "0"};

        Hashtable parameters = PubnubUtil.hashtableClone(app.params);

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
                new ResponseHandler() {
                    public void handleResponse(HttpRequest hreq, String response) {
                        callback.successCallback(null, response);
                    }

                    public void handleError(HttpRequest hreq, PubnubError error) {
                        callback.errorCallback(null, error);
                    }
                });

        app._request(hreq, app.nonSubscribeManager);
    }

    public void restartSubscription() {
        // TODO: possible incorrect method invocation
        app.disconnectAndResubscribe();
    }
}
