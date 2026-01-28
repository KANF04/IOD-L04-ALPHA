# IOD-LO4-ALPHA
https://trello.com/invite/b/69766649462bced4bae02e17/ATTIdb4c25765a7677a4d84b03c92c6dec4d50A6A2B6/io-alpha-sprint-2
https://trello.com/invite/b/69404551cfeccba2d602444a/ATTIaad8d7b110864a93726254449d49fb72ACBF915C/io-alpha-backlog
# Project description

BuildingInfo is a specialized application, created for those who would like to optimize building management costs. 

# Input data structure:

In order to make correct calculations user needs to provide correct file in JSON format:

```
{
  "building": {
    "id": <int>,
    "name": <string>,
    "levels": [
      {
        "id": <int>,
        "name": <string>,
        "rooms": [
          {
            "id": <int>,
            "name": <string>,
            "area": <float>,
            "cube": <float>,
            "heating": <float>,
            "light": <float>
          }
        ]
      }
    ]
  }
}
```
It is possible to load multiple floors and rooms in one file.

# Running application:

To run application user needs to run the command in the directory which contains .jar file:
```
java -jar io-project-architecture-X.Y.jar
```
X.Y - is the version of application


# Sprint Backlog:

https://trello.com/invite/b/6940424a184ad9de13d46705/ATTI3b910bd3814165589ee031342c3996bd49D4FDEB/io-alpha-sprint

# Product Backlog:

https://trello.com/invite/b/69404551cfeccba2d602444a/ATTIaad8d7b110864a93726254449d49fb72ACBF915C/io-alpha-backlog
