---
layout: post
title: "resteasy-grpc: Handling Collections"
subtitle: ""
date: 2025-02-14
author: Ron Sigal
---

Release 1.0.0.Alpha6 of [resteasy-grpc](https://resteasy.dev/docs/grpc/)
(see also [gRPC Bridge Project: User Guide](https://resteasy.dev/docs/grpc/))
has a new facility for handling implementations of `java.util.List`
and `java.util.Set`. In order to handle arbitrary implementations, idiosyncratic details of particular
implementating classes are ignored and all implementations are assigned the least common nature of lists and sets. That is,
an implementation of `java.util.List` is considered to be an ordered sequence and is translated to a protobuf
message type of the form
```
    message java_util___ArrayList16 {
      string classname = 1;
      repeated int32 data = 2;
    }
```
and an implementation of `java.util.Set` is considered to be an unordered collection and is translated to a
protobuf message type of the form
```
    message java_util___HashSet3 {
      string classname = 1;
      repeated string data = 2;
    }
``` 
The `classname` field holds the name of the Java class and is used for translating the class in the protobuf
world (which we call the **javabuf** class) back to its
Java counterpart. We'll elaborate in due course. First, however, we want to be able to distinguish between,
for example, `ArrayList<String>` and `ArrayList<Integer>`, which means introducing a treatment of generic types
and type variables into resteasy-grpc. Note that the suffix numbers like 16 and 3 are used to distinguish the
protubuf versions of, for example, `ArrayList<String>` and `ArrayList<Integer>`.

### Generic types
Generic types are yet another semantic concept in Java that has no direct counterpart in protobuf, so we have to find
a workaround. The important thing is to define a protobuf message whose repeated field matches the type of the list or set. Consider
`java_util___ArrayList16`. That would be a reasonable representation of `ArrayList<Integer>`. Similarly, `java_util___HashSet3`
would be a reasonable representation of `HashSet<String>`. The `JavaToProtobufGenerator` class in the
[grpc-bridge](https://github.com/resteasy/resteasy-grpc) module generates the protobuf messages and decorates them as follows:
```
    // List: java.util.ArrayList<java.lang.Integer>
    message java_util___ArrayList16 {
      string classname = 1;
      //java.lang.Integer
      repeated int32 data = 2;
    }

    // Set: java.util.HashSet<java.lang.String>
    message java_util___HashSet3 {
      string classname = 1;
      //java.lang.String
      repeated string data = 2;
    }
```
These are two simple examples. Consider something a little more complicated: `java.util.ArrayList<java.util.HashSet<java.lang.String>>`:
```
    // List: java.util.ArrayList<java.util.HashSet<java.lang.String>>
    message java_util___ArrayList14 {
      string classname = 1;
      //java.util.HashSet<java.lang.String>
      repeated java_util___HashSet3 data = 2;
    }
```
Recall that the javabuf analog of `java.util.HashSet<java.lang.String>` has already been defined, so that the `data`
field in `java_util___ArrayList14` is of type `java_util___HashSet3`.

A complication arises in the form of type variables and wildcards. The solution adopted in resteasy-grpc is to map unassigned
type variables and wildcards to `java.lang.Object`, which makes sense, since they can take any types at runtime. The protobuf
analog to `java.lang.Object` is `google.protobuf.Any`, which is defined
```
    message Any {
       string type_url = 1;
       bytes value = 2;
    }
```
The `type_url` field indicates the type, e.g., `type.googleapis.com/x.y.java_util___ArrayList14`, where `x.y` is the
package declared in the .proto file.  The value field has built-in type bytes, which "May contain any arbitrary sequence
of bytes no longer than 2^32", according to https://developers.google.com/protocol-buffers/docs/proto3. 

Suppose we have the Jakarta REST resource methods
```
    package x.y;

    @GET
    @Path("grimble/raw")
    public void gr_raw(Grimble g1) {
    }

    @GET
    @Path("grimble/wildcard")
    public void gr_wildcard(Grimble<?> g1) {
    }

    @GET
    @Path("grimble/variable")
    public <T> void gr_variable(Grimble<T> g1) {
    }

    @GET
    @Path("grimble/string")
    public void gr_string(Grimble<String> g1) {
    }

    @GET
    @Path("grimble/integer")
    public void gr_integer(Grimble<Integer> g1) {
    }
```
where x.y.Grimble is
```
    public class Grimble<T> {
        T t;
    }
```
`JavaToProtobufGenerator` would create the rpc and message definitions
```
    // p/grimble/raw x_y___Grimble google.protobuf.Empty GET sync
      rpc gr_raw (GeneralEntityMessage) returns (GeneralReturnMessage);

    // p/grimble/wildcard x_y___Grimble18 google.protobuf.Empty GET sync
      rpc gr_wildcard (GeneralEntityMessage) returns (GeneralReturnMessage);

    // p/grimble/variable x_y___Grimble18 google.protobuf.Empty GET sync
      rpc gr_variable (GeneralEntityMessage) returns (GeneralReturnMessage);

    // p/grimble/string x_y___Grimble19 google.protobuf.Empty GET sync
      rpc gr_string (GeneralEntityMessage) returns (GeneralReturnMessage);

    // p/grimble/integer x_y___Grimble20 google.protobuf.Empty GET sync
      rpc gr_integer (GeneralEntityMessage) returns (GeneralReturnMessage);

    // Type: x.y.Grimble
    message x_y___Grimble {
      google.protobuf.Any t = 1;
    }

    // Type: x.y.Grimble<java.lang.Object>
    message x_y___Grimble18 {
      google.protobuf.Any t = 1;
    }

    // Type: x.y.Grimble<java.lang.String>
    message x_y___Grimble19 {
      string t = 1;
    }

    // Type: x.y.Grimble<java.lang.Integer>
    message x_y___Grimble20 {
      int32 t = 1;
    }

```
The details about the rpc comments are described elsewhere ([gRPC Bridge Project: User Guide](https://resteasy.dev/docs/grpc/)),
but here it's enough to know that the rpc definition
```
    // p/grimble/variable x_y___Grimble18 google.protobuf.Empty GET sync
      rpc gr_variable (GeneralEntityMessage) returns (GeneralReturnMessage);
```
for example, indicates that the entity type for method `gr_variable` is `x_y_Grimble18`.

**Notes:**

1. There are four different variations on `x_y___Grimble` here, one for each of `x.y.Grimble`, `x.y.Grimble<java.lang.Object>`,
`x.y.Grimble<java.lang.String>`, and `x.y.Grimble<java.lang.Integer>`.

2. The comments on the rpc definitions of `gr_wildcard()` and `gr_variable()` indicate that both take input parameters
`x_y_Grimble18`, which is the generated protobuf representation of `x.y.Grimble<java.lang.Object>`. This convergence
follows from the fact that the wildcard and the open type variable are both represented by `java.lang.Object`.

3. The definition of `x_y___Grimble18`, which represents `x.y.Grimble<java.lang.Object>`, has a single element of
type `google.protobuf.Any`, which, as discussed above, represents an arbitrary type, making it an appropriate translation
`of java.lang.Object`.

The discussion about generic types and type variables applies to lists and sets. For example,
```
    @Path("arraylist/hashset/wildcard")
    @POST
    public ArrayList<HashSet<?>> arraylistHashsetTest2(ArrayList<HashSet<?>> l) {
        return l;
    }
```
gives rise to
```
    // List: java.util.ArrayList<java.util.HashSet<java.lang.Object>>
    message java_util___ArrayList13 {
      string classname = 1;
      //java.util.HashSet<java.lang.Object>
      repeated java_util___HashSet2 data = 2;
  
    // Set: java.util.HashSet<java.lang.Object>
    message java_util___HashSet2 {
      string classname = 1;
      //java.lang.Object
      repeated google.protobuf.Any data = 2;
    }
```
### Lists and sets at runtime

Here we'll discuss a gRPC client intending to communicate with a Jakarta REST server. The subject is covered in detail in 
[gRPC Bridge Project: User Guide](https://resteasy.dev/docs/grpc/), but here we will look at sending and receiving
`Collection`s. For example, consider the resource method
```
    @GET
    @Path("arraylist/integer")
    public ArrayList<?> listArray0(ArrayList<Integer> list) {
        return list;
    }
```
We've seen that `java.util.ArrayList<java.lang.Integer>` translates to javabuf class `java_util___ArrayList16`. So the client
has to create an instance of `java_util___ArrayList16` to send to the server. There are two possible strategies. One is to
work in the javabuf world:
```
    java_util___ArrayList16.Builder juaBuilder = java_util___ArrayList16.newBuilder();
    juaBuilder.setClassname("java.util.ArrayList");
    juaBuilder.addData(3);
    juaBuilder.addData(7);
    java_util___ArrayList16 jua = juaBuilder.build();
```
Alternatively, one could create an `ArrayList` and translate it to an `java_util___ArrayList16`:
```
    ArrayList<Integer> list = new ArrayList<Integer>();
 	list.add(3);
    list.add(7);
    GenericType<java.util.ArrayList<java.lang.Integer>> type = new GenericType<java.util.ArrayList<java.lang.Integer>>() { };
    java_util___ArrayList16 jua = (java_util___ArrayList16) translator.translateToJavabuf(list, type);
```
where `translator` is an instance of `dev.resteasy.grpc.bridge.runtime.protobuf.JavabufTranslator`.
The next step is to build a `GeneralEntityMessage`:
```
    GeneralEntityMessage.Builder gemBuilder = GeneralEntityMessage.newBuilder();
    gemBuilder.setJavaUtilArrayList16Field(gemBuilder.build());
```
Then the remote method can be invoked:
```
    GeneralReturnMessage response = stub.listArray0(gem);
```
where `stub` is the client side representative of the server methods.
Finally, the result can be extracted from the `GeneralReturnMessage`. Note that `listArray0` returns
an instance of `ArrayList<?>`, which translates to javabuf class
```
    // List: java.util.ArrayList<java.lang.Object>
    message java_util___ArrayList17 {
       string classname = 1;
       //java.lang.Object
       repeated google.protobuf.Any data = 2;
    }
```
This complicates things a bit since we have to extract the returned list from an `Any`.
```
    java_util___ArrayList17 result = response.getJavaUtilArrayList17Field();
    Any any = response.getAnyField();
    Message result = any.unpack((Class) Utility.extractClassFromAny(any, translator));
    list = (ArrayList<Object>) translator.translateFromJavabuf(result);
```
### Maps
Stay tuned for the next release, which which implementations of `java.util.Map` will be treated in a similar way.