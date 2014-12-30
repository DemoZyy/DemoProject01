# Data Sync API Reference for Java

## New object creation
### using sync()

```java
SyncedObject home = pubnub.sync("home");
SyncedObject thermostat = pubnub.sync("home.living_room.thermostat");
SyncedObject occupants = pubnub.sync("home.occupants");
```

### using child()

```java
SyncedObject home = pubnub.sync("home");
SyncedObject thermostat = home.child("living_room.thermostat");
SyncedObject occupants = home.child("occupants");
```

### Value getters.
If value doesn't exist or cannot be casted to getter's type, null will be returned.

#### getString()
```java
String state = thermostat.getString("state");
// state is a string "on"
```

#### getInteger()
```java
Integer temperature = thermostat.getInteger("temperature");
// temperature is an integer 68
```

#### getBoolean()
```java
Boolean connected = thermostat.getBoolean("connected");
// temperature is a boolean true
```

#### getInteger() on string value
```java
Integer state = thermostat.getInteger("state");
// state is null
```

#### getMap()
```java
List stateList = home.getMap();
```

#### getList()
```java
HashMap occupantsMap = home.getMap("occupants");
HashMap occupantsMap = occupants.getMap();
```

### Mutate data methods
#### Merge

```java
// merge(), unlike replace(), will add data to your object WITHOUT truncating existing child data.
JSONObject scoresUpdate = new JSONObject();
scoresUpdate.put("Chauncy", 10);

scores.merge(scoresUpdate);

// or without instantiating SyncedObject
Hashtable<String, Object> args = new Hashtable<String, Object>();

args.put("location", "scores");
args.put("data", scoresUpdate);

pubnub.merge(args, new Callback());
```

#### Merge

```java
// replace(), unlike merge(), will add data to your object, WHILE truncating existing child data.
JSONObject scoresUpdate = new JSONObject();
scoresUpdate.put("Scotty", 3);

scores.replace(scoresUpdate);

// or without instantiating SyncedObject
Hashtable<String, Object> args = new Hashtable<String, Object>();

args.put("location", "scores");
args.put("data", scoresUpdate);

pubnub.replace(args, new Callback());
```

#### Remove

```java
// remove() deletes data
scores.remove();

// or without instantiating SyncedObject
Hashtable<String, Object> args = new Hashtable<String, Object>();

args.put("location", "scores");

pubnub.remove(args, new Callback());
```

#### Push

```java
// push() appends data to the end of a list container
JSONObject playersUpdate = new JSONObject();
JSONObject player1 = new JSONObject();

player1.put("name", "Randy");
player1.put("weapon", "dagger");

playersUpdate.put("Player_1", player1);

players.push(playersUpdate);

// or without instantiating SyncedObject
Hashtable<String, Object> args = new Hashtable<String, Object>();

args.put("location", "players");
args.put("data", scoresUpdate);

pubnub.push(args, new Callback());
```