package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.25.0)",
    comments = "Source: services.proto")
public final class ChattingGrpc {

  private ChattingGrpc() {}

  public static final String SERVICE_NAME = "grpc.Chatting";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.Services.SimpleGreetingRequest,
      grpc.Services.SimpleGreetingResponse> getSimpleGreetingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SimpleGreeting",
      requestType = grpc.Services.SimpleGreetingRequest.class,
      responseType = grpc.Services.SimpleGreetingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.SimpleGreetingRequest,
      grpc.Services.SimpleGreetingResponse> getSimpleGreetingMethod() {
    io.grpc.MethodDescriptor<grpc.Services.SimpleGreetingRequest, grpc.Services.SimpleGreetingResponse> getSimpleGreetingMethod;
    if ((getSimpleGreetingMethod = ChattingGrpc.getSimpleGreetingMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getSimpleGreetingMethod = ChattingGrpc.getSimpleGreetingMethod) == null) {
          ChattingGrpc.getSimpleGreetingMethod = getSimpleGreetingMethod =
              io.grpc.MethodDescriptor.<grpc.Services.SimpleGreetingRequest, grpc.Services.SimpleGreetingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SimpleGreeting"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.SimpleGreetingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.SimpleGreetingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("SimpleGreeting"))
              .build();
        }
      }
    }
    return getSimpleGreetingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ChattingStub newStub(io.grpc.Channel channel) {
    return new ChattingStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ChattingBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ChattingBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ChattingFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ChattingFutureStub(channel);
  }

  /**
   */
  public static abstract class ChattingImplBase implements io.grpc.BindableService {

    /**
     */
    public void simpleGreeting(grpc.Services.SimpleGreetingRequest request,
        io.grpc.stub.StreamObserver<grpc.Services.SimpleGreetingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSimpleGreetingMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSimpleGreetingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.SimpleGreetingRequest,
                grpc.Services.SimpleGreetingResponse>(
                  this, METHODID_SIMPLE_GREETING)))
          .build();
    }
  }

  /**
   */
  public static final class ChattingStub extends io.grpc.stub.AbstractStub<ChattingStub> {
    private ChattingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChattingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChattingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChattingStub(channel, callOptions);
    }

    /**
     */
    public void simpleGreeting(grpc.Services.SimpleGreetingRequest request,
        io.grpc.stub.StreamObserver<grpc.Services.SimpleGreetingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSimpleGreetingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ChattingBlockingStub extends io.grpc.stub.AbstractStub<ChattingBlockingStub> {
    private ChattingBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChattingBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChattingBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChattingBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.Services.SimpleGreetingResponse simpleGreeting(grpc.Services.SimpleGreetingRequest request) {
      return blockingUnaryCall(
          getChannel(), getSimpleGreetingMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ChattingFutureStub extends io.grpc.stub.AbstractStub<ChattingFutureStub> {
    private ChattingFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChattingFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChattingFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChattingFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.SimpleGreetingResponse> simpleGreeting(
        grpc.Services.SimpleGreetingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSimpleGreetingMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SIMPLE_GREETING = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ChattingImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ChattingImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SIMPLE_GREETING:
          serviceImpl.simpleGreeting((grpc.Services.SimpleGreetingRequest) request,
              (io.grpc.stub.StreamObserver<grpc.Services.SimpleGreetingResponse>) responseObserver);
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

  private static abstract class ChattingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ChattingBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.Services.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Chatting");
    }
  }

  private static final class ChattingFileDescriptorSupplier
      extends ChattingBaseDescriptorSupplier {
    ChattingFileDescriptorSupplier() {}
  }

  private static final class ChattingMethodDescriptorSupplier
      extends ChattingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ChattingMethodDescriptorSupplier(String methodName) {
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
      synchronized (ChattingGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ChattingFileDescriptorSupplier())
              .addMethod(getSimpleGreetingMethod())
              .build();
        }
      }
    }
    return result;
  }
}
