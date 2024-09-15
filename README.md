# HTTP Server

## Task: 

* Implement part of HTTP 1.1 protocol using ServerSocketChannel (java.nio)
* Methods: 

  GET

  POST

  PUT

  PATCH

  Delete
* Headers (should be accesible as Map)
* Library should support:

  Create and httpserver on specified host+port
 
  Add listener to specific path and method

  Access to request parameters (headers, method, etc)

  Create and send http response back

## Brief description of the implementation:

### HttpRequest:

Processing a request received from a client.

### HttpResponse:

Creating a response to be sent to the client.

### Router

It contains a resource tree, provides the ability 
to create new resources, add client handlers to each of the resources.

### Resource

Contains data stored on the resource.

### HttpHandler 

An interface for implementing a client request handler

### HttpHandlerDefault 

The handler that is added by default.

## Examples of using:

The TestHttpMethodsDefaultHandler provides examples work HTTP methods with default handler;

The TestCustomHandler provides examples of creating custom handler.