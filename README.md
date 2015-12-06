# DirectoryCleanUpUtility

Utility for easy directory clean up. Can be schedule to run via cron on Windows task scheduler

##Compiling the Utility.

This application uses folliwnig third party package.

```java
import org.joda.time.*;
```
Please make sure to download the latest version from http://www.joda.org/joda-time/ and copy it to your class path.

##Configuring the Utility

Application configuration is stored in a file called config.properties. This file contains the following configration,

###Configuration File Contents

```properties
directory=/Users/archieg/Documents/TEST
clean_up_after_unit=MINUTES
clean_up_after_value=1
data_file=data.dat
dry_run=true
```

###Configuration File Explanied

| Configuration Value | Value Useage |
| directory | Directory to clean up |
| clean_up_after_unit | Clean up after unit |
| clean_up_after_value | Delete any files older than this many untis (clean_up_after_unit) |
| data_file | Database to store history of the directory |
| dry_run | Test mode, only print the output, no files will be deleted |

You have following opitons avaiable for clean_up_after_unit variable.
* MINUTES
* HOURS
* DAYS
* MONTHS

##Running the Utility

Run the utility from the commandline or schedule a task to run it. This can be archived by using Cron jobs on Unix and Task Schedular on Windows.
