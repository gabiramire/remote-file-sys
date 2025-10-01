package remote;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.57.2)",
    comments = "Source: remote_file.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RemoteFileServiceGrpc {

  private RemoteFileServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "remote.RemoteFileService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<remote.OpenRequest,
      remote.OpenResponse> getOpenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Open",
      requestType = remote.OpenRequest.class,
      responseType = remote.OpenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<remote.OpenRequest,
      remote.OpenResponse> getOpenMethod() {
    io.grpc.MethodDescriptor<remote.OpenRequest, remote.OpenResponse> getOpenMethod;
    if ((getOpenMethod = RemoteFileServiceGrpc.getOpenMethod) == null) {
      synchronized (RemoteFileServiceGrpc.class) {
        if ((getOpenMethod = RemoteFileServiceGrpc.getOpenMethod) == null) {
          RemoteFileServiceGrpc.getOpenMethod = getOpenMethod =
              io.grpc.MethodDescriptor.<remote.OpenRequest, remote.OpenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Open"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.OpenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.OpenResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RemoteFileServiceMethodDescriptorSupplier("Open"))
              .build();
        }
      }
    }
    return getOpenMethod;
  }

  private static volatile io.grpc.MethodDescriptor<remote.ReadRequest,
      remote.ReadResponse> getReadMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Read",
      requestType = remote.ReadRequest.class,
      responseType = remote.ReadResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<remote.ReadRequest,
      remote.ReadResponse> getReadMethod() {
    io.grpc.MethodDescriptor<remote.ReadRequest, remote.ReadResponse> getReadMethod;
    if ((getReadMethod = RemoteFileServiceGrpc.getReadMethod) == null) {
      synchronized (RemoteFileServiceGrpc.class) {
        if ((getReadMethod = RemoteFileServiceGrpc.getReadMethod) == null) {
          RemoteFileServiceGrpc.getReadMethod = getReadMethod =
              io.grpc.MethodDescriptor.<remote.ReadRequest, remote.ReadResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Read"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.ReadRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.ReadResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RemoteFileServiceMethodDescriptorSupplier("Read"))
              .build();
        }
      }
    }
    return getReadMethod;
  }

  private static volatile io.grpc.MethodDescriptor<remote.WriteRequest,
      remote.WriteResponse> getWriteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Write",
      requestType = remote.WriteRequest.class,
      responseType = remote.WriteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<remote.WriteRequest,
      remote.WriteResponse> getWriteMethod() {
    io.grpc.MethodDescriptor<remote.WriteRequest, remote.WriteResponse> getWriteMethod;
    if ((getWriteMethod = RemoteFileServiceGrpc.getWriteMethod) == null) {
      synchronized (RemoteFileServiceGrpc.class) {
        if ((getWriteMethod = RemoteFileServiceGrpc.getWriteMethod) == null) {
          RemoteFileServiceGrpc.getWriteMethod = getWriteMethod =
              io.grpc.MethodDescriptor.<remote.WriteRequest, remote.WriteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Write"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.WriteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.WriteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RemoteFileServiceMethodDescriptorSupplier("Write"))
              .build();
        }
      }
    }
    return getWriteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<remote.CloseRequest,
      remote.CloseResponse> getCloseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Close",
      requestType = remote.CloseRequest.class,
      responseType = remote.CloseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<remote.CloseRequest,
      remote.CloseResponse> getCloseMethod() {
    io.grpc.MethodDescriptor<remote.CloseRequest, remote.CloseResponse> getCloseMethod;
    if ((getCloseMethod = RemoteFileServiceGrpc.getCloseMethod) == null) {
      synchronized (RemoteFileServiceGrpc.class) {
        if ((getCloseMethod = RemoteFileServiceGrpc.getCloseMethod) == null) {
          RemoteFileServiceGrpc.getCloseMethod = getCloseMethod =
              io.grpc.MethodDescriptor.<remote.CloseRequest, remote.CloseResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Close"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.CloseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  remote.CloseResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RemoteFileServiceMethodDescriptorSupplier("Close"))
              .build();
        }
      }
    }
    return getCloseMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RemoteFileServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RemoteFileServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RemoteFileServiceStub>() {
        @java.lang.Override
        public RemoteFileServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RemoteFileServiceStub(channel, callOptions);
        }
      };
    return RemoteFileServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RemoteFileServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RemoteFileServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RemoteFileServiceBlockingStub>() {
        @java.lang.Override
        public RemoteFileServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RemoteFileServiceBlockingStub(channel, callOptions);
        }
      };
    return RemoteFileServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RemoteFileServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RemoteFileServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RemoteFileServiceFutureStub>() {
        @java.lang.Override
        public RemoteFileServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RemoteFileServiceFutureStub(channel, callOptions);
        }
      };
    return RemoteFileServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void open(remote.OpenRequest request,
        io.grpc.stub.StreamObserver<remote.OpenResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getOpenMethod(), responseObserver);
    }

    /**
     */
    default void read(remote.ReadRequest request,
        io.grpc.stub.StreamObserver<remote.ReadResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReadMethod(), responseObserver);
    }

    /**
     */
    default void write(remote.WriteRequest request,
        io.grpc.stub.StreamObserver<remote.WriteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWriteMethod(), responseObserver);
    }

    /**
     */
    default void close(remote.CloseRequest request,
        io.grpc.stub.StreamObserver<remote.CloseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCloseMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RemoteFileService.
   */
  public static abstract class RemoteFileServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RemoteFileServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RemoteFileService.
   */
  public static final class RemoteFileServiceStub
      extends io.grpc.stub.AbstractAsyncStub<RemoteFileServiceStub> {
    private RemoteFileServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RemoteFileServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RemoteFileServiceStub(channel, callOptions);
    }

    /**
     */
    public void open(remote.OpenRequest request,
        io.grpc.stub.StreamObserver<remote.OpenResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getOpenMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void read(remote.ReadRequest request,
        io.grpc.stub.StreamObserver<remote.ReadResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReadMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void write(remote.WriteRequest request,
        io.grpc.stub.StreamObserver<remote.WriteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWriteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void close(remote.CloseRequest request,
        io.grpc.stub.StreamObserver<remote.CloseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCloseMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RemoteFileService.
   */
  public static final class RemoteFileServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RemoteFileServiceBlockingStub> {
    private RemoteFileServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RemoteFileServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RemoteFileServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public remote.OpenResponse open(remote.OpenRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getOpenMethod(), getCallOptions(), request);
    }

    /**
     */
    public remote.ReadResponse read(remote.ReadRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReadMethod(), getCallOptions(), request);
    }

    /**
     */
    public remote.WriteResponse write(remote.WriteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getWriteMethod(), getCallOptions(), request);
    }

    /**
     */
    public remote.CloseResponse close(remote.CloseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCloseMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RemoteFileService.
   */
  public static final class RemoteFileServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<RemoteFileServiceFutureStub> {
    private RemoteFileServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RemoteFileServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RemoteFileServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<remote.OpenResponse> open(
        remote.OpenRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getOpenMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<remote.ReadResponse> read(
        remote.ReadRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReadMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<remote.WriteResponse> write(
        remote.WriteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWriteMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<remote.CloseResponse> close(
        remote.CloseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCloseMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_OPEN = 0;
  private static final int METHODID_READ = 1;
  private static final int METHODID_WRITE = 2;
  private static final int METHODID_CLOSE = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_OPEN:
          serviceImpl.open((remote.OpenRequest) request,
              (io.grpc.stub.StreamObserver<remote.OpenResponse>) responseObserver);
          break;
        case METHODID_READ:
          serviceImpl.read((remote.ReadRequest) request,
              (io.grpc.stub.StreamObserver<remote.ReadResponse>) responseObserver);
          break;
        case METHODID_WRITE:
          serviceImpl.write((remote.WriteRequest) request,
              (io.grpc.stub.StreamObserver<remote.WriteResponse>) responseObserver);
          break;
        case METHODID_CLOSE:
          serviceImpl.close((remote.CloseRequest) request,
              (io.grpc.stub.StreamObserver<remote.CloseResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getOpenMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              remote.OpenRequest,
              remote.OpenResponse>(
                service, METHODID_OPEN)))
        .addMethod(
          getReadMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              remote.ReadRequest,
              remote.ReadResponse>(
                service, METHODID_READ)))
        .addMethod(
          getWriteMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              remote.WriteRequest,
              remote.WriteResponse>(
                service, METHODID_WRITE)))
        .addMethod(
          getCloseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              remote.CloseRequest,
              remote.CloseResponse>(
                service, METHODID_CLOSE)))
        .build();
  }

  private static abstract class RemoteFileServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RemoteFileServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return remote.RemoteFileProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RemoteFileService");
    }
  }

  private static final class RemoteFileServiceFileDescriptorSupplier
      extends RemoteFileServiceBaseDescriptorSupplier {
    RemoteFileServiceFileDescriptorSupplier() {}
  }

  private static final class RemoteFileServiceMethodDescriptorSupplier
      extends RemoteFileServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    RemoteFileServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RemoteFileServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RemoteFileServiceFileDescriptorSupplier())
              .addMethod(getOpenMethod())
              .addMethod(getReadMethod())
              .addMethod(getWriteMethod())
              .addMethod(getCloseMethod())
              .build();
        }
      }
    }
    return result;
  }
}
