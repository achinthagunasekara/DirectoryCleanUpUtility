# DirectoryCleanUpUtility

Utility for easy directory clean up. Can be schedule to run via cron on Windows task scheduler

##Compiling the Utility.

This application uses folliwnig third party package.

```java
import org.joda.time.*;
```
Please make sure to download the latest version from http://www.joda.org/joda-time/ and copy it to your class path.

###Using the utility

Application configuration is stored in a file called config.properties. This file contains the following configration,

```properties
directory=/Users/archieg/Documents/TEST #Directory to clean up
clean_up_after_unit=MINUTES #Clean up after unit
clean_up_after_value=1 #Delete any files older than this many untis (clean_up_after_unit)
data_file=data.dat #Database to store history of the directory
dry_run=true #Test mode, only print the output, no files will be deleted
```

You have following opitons avaiable for clean_up_after_unit variable.
* MINUTES
* HOURS
* DAYS
* MONTHS
