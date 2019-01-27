# SimpleRouter
Simple router for delegating http requests
## Introduction
Most router implementations are part of a framework. It is such a shame that we don't have a market of small handy components, like PHP's Packagist (Composer). I do hope that we will see something similar within the Java community in the near future, a kind of market place for small, well tested components. Let's be honest: most often we don't need a ship (like Spring for example) to cross a ditch. Something smaller, build from well tested components would most often suit my needs.

Maybe this will be a start (and there is a lot more, I know). This very simple Router that can be used within Servlets or any other frontcontroller that needs routing capabilities (actually it is not bound to a specific environmentby means of generics).

## How does it work
Its usage is very simple (I do hope you like its simplicity). First you must have a good idea which paths you need for your requests, most likely it will be REST or REST like.

The request is identified by its path and method. The methods are currently limited to GET, POST, PUT and DELETE. If you need any other method, just add it to the enum class ActionMethod.

Furthermore you need a target. A target is a functional interface and it can be actually anything. Since I'm using method, it is not a general as I would like it to be.

So let's define a functional interface <T>

```java
 public interface Handler {
        void execute(HttpServletRequest request, HttpServletResponse response, MatchedValues matchedValues) throws ServletException , IOException;
 }
```
The class responsible for defining route entries is WebRouting. WebRouting contains a Builder class to build a WebRouting instance.
The routes are stored in a `List<RouteEntry<T>>`. Once you are done, it will create an unmodifiable List which will be the reentrant list for all requests (thread safety is important).

A definition could look as follows (see unit test):

```java
WebRouting<Handler> router = new WebRouting
                .Builder<>()
                .addRoute(MethodAction.GET, "/info", RouteController.info)
                .addRoute(MethodAction.GET, "/var/(var1:int)/next/(var2:string)", RouteController.vardata)
                .addRoute(MethodAction.POST, "/transfer", RouteController.payment)
                .build();
```
When a requests comes in for /info, then the request will be delegated to method reference info (it should be possible to build a Controller class using annotations, like this is done with JAX-RS. But I keep it as simple as possible).

The paths are matched in sequential order (using a stream). So the order is significant. This is mothing you should be aware of.

The second route looks different. It contains a regex expression. A regex expression is enclosed within braces. It consists of two parts, a name and a type identifier. The type identifier can be int or string. 

The class WebRouting uses two route definitions. One is used for plain-vanilla paths - `RouteStringKey` -, the other one parses the path in order to extract data that is needed for the delegated method - `RoutePatternKey` -.

## WebRouting in action
```java
// the second route is selected
 MatchedValues matched = router.matchEntry(MethodAction.valueOf("GET"), "/var/12345/next/abcde");
 assertTrue(matched.isMatch());
 
 Handler handler = matched.getTarget();
 handler.execute(request, response, matched);
```

The handler (lambda expression) looks as follows
```
public static Handler vardata = (request, response, matched) -> {
    String intValue    = matched.getParameter("var1");
    String stringValue = matched.getParameter("var2");

    assertEquals("12345", intValue);
    assertEquals("abcde", stringValue);
};

```
## And finally
This component is not complete yet, but it works pretty well.







