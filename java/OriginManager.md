# Origin Manager

## How to enable/disable

You should explicitly enable OriginManager to use it. This can be done by two ways:

* Using `#enableOriginManager()` method:
  ``` java
  pubnub.enableOriginManager();
  ```

* Using `#setOriginsPool()` method. Second parameter should be true.

  ``` java
  pubnub.setOriginsPool(new String(){"origin1", "origin2"}, true);
  ```

To explicitly disable OriginManager use `#disableOriginManager()` method:

  ``` java
  pubnub.disableOriginManager();
  ```

## Origins pool

Origins pool is implemented as LinkedHashSet, so sequence order is important. At least 2 origins should be specified, otherwise `PubnubError` will be thrown. Use `#setOriginsPool()` method for this purpose:

* setup using `String` array:

  ``` java
  pubnub.setOriginsPool(new String[]{"origin1", "origin2"});
  ```

* or using `LinkedHashSet`:

  ``` java
  LinkedHashSet originsPool = new LinkedHashSet();

  originsPool.add("origins1");
  originsPool.add("origins2");

  pubnub.setOriginsPool(originsPool);
  ```

## Errors callback
To be able to handle OriginManager errors, happened while OriginManager working, pass in `Callback` instance to `#enableOriginManager()` method:

``` java
  pubnub.enableOriginManager(new Callback() {
      @Override
      public void errorCallback(String channel, PubnubError error) {
          // handle PubnubError
      }
  });
```

If OriginManager cannot be enabled, `error` callback will be triggered.

## Configs
OriginManager has also a few default configs that can be overridden:

```java
// default 5
pubnub.setOriginManagerInterval(60);

// default 5
pubnub.setOriginManagerIntervalAfterFailure(30);

// default 5
pubnub.setOriginManagerMaxRetries(10);
```
