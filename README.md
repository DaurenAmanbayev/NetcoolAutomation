## Introduction
NetcoolAutomation is my atempt to create a better integration with a dynamic script enviroment, groovy in this case, with
IBM Netcool.


## Development Journal
20/09/2014: 
 So far i was able to create a Omnibus Connection Pool, this is critical since we don´t want the jobs to crash or
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

Since I´m using an ObservableHashMap note that the commitChangedEvents method will only commit to the object server those events 
that were changed inside the groovy shell. The untouched events will not be commited and for the changed events only the changed fields will be update
the others will ne untouched.

Running this code in a desktop server took 9000ms to execute.



## Target Feattures
* SnmpGetRequest for simple host,community,oid
* SnmpWalkRequest form simple host,community,oid
* Database integration, easy query or store data from any jdbc compatible resource.
* Email Sender to send email, every manager like these kind of features.
* Integration with user created java classes,program,s etc...


## Deploying the server...
