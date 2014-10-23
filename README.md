
![alt tag](https://travis-ci.org/nishisan/NetcoolAutomation.svg?branch=dev)

## Introduction
NetcoolAutomation is my atempt to create a better integration with a dynamic script enviroment, groovy in this case, with
IBM Netcool.


## Development Journal
**20/09/2014**: 
 So far i was able to create a Omnibus Connection Pool, this is critical since we donÂ´t want the jobs to crash or
overloaded omnibus object server.
 Before you continue you shoud know that by "event" I mean alert in alerts.status table...
 
 I also created a Dynamic mechanism to execute jobs for centais query so,if we create a "select * from alerts.status where Severity >3"
 The job will create an nested map and pass it as reference to a groovy shell context where we can handle the events and 
 transform then.
 

 So lets see the "DefaultEventReader";
 
```java
@Override
    public void executeContext(Connection con) {
        // Execution Context
        String sql = "select * from alerts.status where Severity > 2";
        ArrayList<EventMap> events = this.omniClient.executeQuery(sql);
        
        HashMap<String, ArrayList<HashMap<String, Object>>> changedEvents = new HashMap<>();
        Long beforeMap = System.currentTimeMillis();        
        for (EventMap e : events) {
            e.setChangedMap(changedEvents);
        }
        Long afterMap = System.currentTimeMillis();
        logger.debug("Map Took: " +(afterMap-beforeMap) + " ms");
        Binding binding = new Binding();
        binding.setVariable("events", events);
        try {
            Long startTime = System.currentTimeMillis();
            GroovyShell shell = new GroovyShell(binding);            
            shell.evaluate("for (x in events){"
                    + " if (x.Summary =~/Node Down/){"
                    + "   "
                    + "   x.Summary = 'This Node Is down...please check...'; "
                    + "   x.Severity = 5; "
                    + " } "
                    + "} ");
            Long endTime = System.currentTimeMillis();
            logger.debug("Groovy Script Execution Time: " + (endTime - startTime) + "ms");
        } catch (Exception ex) {
            logger.error("fail to execute script at job: " + this.jobName, ex);
        }
        
        omniClient.commitChangedEvents(changedEvents);
    }
```
 
The code above will run the:

```sql
  select * from alerts.status where Severity > 2
```
Against the object server and for every result it will pass to a groovy shell.
If the alarm summary fields contains the word "Node Down" it will change the summary to 'This Node Is down...please check...'.

Since I'm using an ObservableHashMap note that the commitChangedEvents method will only commit to the object server those events 
that were changed inside the groovy shell. The untouched events will not be commited and for the changed events only the changed fields will be update
the others will ne untouched.

Running this code in a desktop server took 9000ms to execute.

**24/09/2014**: So I´ve changed a few stuff on the event reader. Now the event reader holds a list of policies that is linked to it and before execution 
it refreshes the groovy script. The previous version  had to be reloaded if you update the policy.
I´ve also realized that is more eficient to use StateChange Collumn to keep in track what events had changed since last execution.
So even if you create a reader with this sql:

```sql
  select * from alerts.status where Severity > 2
```

You will see inside omniclient that i concatenate the sql as:
```java
 public ArrayList<EventMap> executeQuery(String filter, String connName, AutomationReader reader) {
        ArrayList<EventMap> list = new ArrayList<>();
        logger.debug("Executing Query on:" + connName);

        String sql = "select * from alerts.status where 1=1 and StateChange >  " + reader.getStateChanged() + " and " + filter + " order by StateChange ";
        logger.debug("SQL:::" + sql);
        try {
........
```

The "order by" clause is for us to get later the last StateChange timestamp and run the same from that point.
This gave a very high performance in event parsing.

I still have plans to create the web interface, so far im updating the scripts using a local desktop editor workarround :( sorry for that.

I also have plans to create a plugin architecture.

Tomorrow i will implement the Policy local storage, this storage will keep objects as it is in the policy execution context and persist it over time.
This will give the flexibility to create X in Y Synthetic events.
I still didnt decide the best aproach to implement the local storage.

Im sorry for my terrible english today, Im tired.


**17/10/2014**: This version is in production for testing. I started to write the bootrap for the embedded glassfish server.



## Target Features
* SnmpGetRequest for simple host,community,oid
* SnmpWalkRequest form simple host,community,oid
* Database integration, easy query or store data from any jdbc compatible resource.
* Email Sender to send email, every manager like these kind of features.
* Integration with user created java classes,program,s etc...

## Dependencies
In order to compile this project you will  need the following jar´s in your classpath:
 * commons-codec-1.6.jar
 * commons-configuration-1.9.jar
 * commons-dbcp2-2.0.1.jar
 * commons-io-2.1.jar
 * commons-lang3-3.0.1.jar
 * commons-logging-1.1.3.jar
 * commons-net-3.1.jar
 * commons-pool2-2.2.jar
 * groovy-all-2.3.6.jar
 * gson-2.3.jar
 * guava-18.0.jar
 * jconn3-6.0.5.jar
 * log4j-1.2.16.jar
 * quartz-2.2.1.jar
 * quartz-jobs-2.2.1.jar
 * slf4j-api-1.6.6.jar
 * slf4j-log4j12-1.6.6.jar

## Deploying the server...
