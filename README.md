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
directory=/Users/archieg/Documents/AWS_TEST
clean_up_after_unit=MINUTES
clean_up_after_value=1
data_file=data.dat
dry_run=true
```


src/Config/CleanUpAfterUnit.java

public enum Units {
        MINUTES,
        HOURS,
        DAYS,
        MONTHS
    }
