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

  private static volatile io.grpc.MethodDescriptor<grpc.Services.DeliveryAssignationMessage,
      grpc.Services.DeliveryAssignationResponse> getDeliveryAssignationServiceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeliveryAssignationService",
      requestType = grpc.Services.DeliveryAssignationMessage.class,
      responseType = grpc.Services.DeliveryAssignationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.DeliveryAssignationMessage,
      grpc.Services.DeliveryAssignationResponse> getDeliveryAssignationServiceMethod() {
    io.grpc.MethodDescriptor<grpc.Services.DeliveryAssignationMessage, grpc.Services.DeliveryAssignationResponse> getDeliveryAssignationServiceMethod;
    if ((getDeliveryAssignationServiceMethod = ChattingGrpc.getDeliveryAssignationServiceMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getDeliveryAssignationServiceMethod = ChattingGrpc.getDeliveryAssignationServiceMethod) == null) {
          ChattingGrpc.getDeliveryAssignationServiceMethod = getDeliveryAssignationServiceMethod =
              io.grpc.MethodDescriptor.<grpc.Services.DeliveryAssignationMessage, grpc.Services.DeliveryAssignationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeliveryAssignationService"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.DeliveryAssignationMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.DeliveryAssignationResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("DeliveryAssignationService"))
              .build();
        }
      }
    }
    return getDeliveryAssignationServiceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.Services.DeliveryComplete,
      grpc.Services.DeliveryCompleteResponse> getDeliveryCompleteServiceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeliveryCompleteService",
      requestType = grpc.Services.DeliveryComplete.class,
      responseType = grpc.Services.DeliveryCompleteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.DeliveryComplete,
      grpc.Services.DeliveryCompleteResponse> getDeliveryCompleteServiceMethod() {
    io.grpc.MethodDescriptor<grpc.Services.DeliveryComplete, grpc.Services.DeliveryCompleteResponse> getDeliveryCompleteServiceMethod;
    if ((getDeliveryCompleteServiceMethod = ChattingGrpc.getDeliveryCompleteServiceMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getDeliveryCompleteServiceMethod = ChattingGrpc.getDeliveryCompleteServiceMethod) == null) {
          ChattingGrpc.getDeliveryCompleteServiceMethod = getDeliveryCompleteServiceMethod =
              io.grpc.MethodDescriptor.<grpc.Services.DeliveryComplete, grpc.Services.DeliveryCompleteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeliveryCompleteService"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.DeliveryComplete.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.DeliveryCompleteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("DeliveryCompleteService"))
              .build();
        }
      }
    }
    return getDeliveryCompleteServiceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.Services.Empty,
      grpc.Services.Empty> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ping",
      requestType = grpc.Services.Empty.class,
      responseType = grpc.Services.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.Empty,
      grpc.Services.Empty> getPingMethod() {
    io.grpc.MethodDescriptor<grpc.Services.Empty, grpc.Services.Empty> getPingMethod;
    if ((getPingMethod = ChattingGrpc.getPingMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getPingMethod = ChattingGrpc.getPingMethod) == null) {
          ChattingGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<grpc.Services.Empty, grpc.Services.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("Ping"))
              .build();
        }
      }
    }
    return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.Services.ElectionMessage,
      grpc.Services.Empty> getElectionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Election",
      requestType = grpc.Services.ElectionMessage.class,
      responseType = grpc.Services.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.ElectionMessage,
      grpc.Services.Empty> getElectionMethod() {
    io.grpc.MethodDescriptor<grpc.Services.ElectionMessage, grpc.Services.Empty> getElectionMethod;
    if ((getElectionMethod = ChattingGrpc.getElectionMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getElectionMethod = ChattingGrpc.getElectionMethod) == null) {
          ChattingGrpc.getElectionMethod = getElectionMethod =
              io.grpc.MethodDescriptor.<grpc.Services.ElectionMessage, grpc.Services.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Election"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.ElectionMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("Election"))
              .build();
        }
      }
    }
    return getElectionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.Services.ElectedMessage,
      grpc.Services.Empty> getElectedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Elected",
      requestType = grpc.Services.ElectedMessage.class,
      responseType = grpc.Services.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.ElectedMessage,
      grpc.Services.Empty> getElectedMethod() {
    io.grpc.MethodDescriptor<grpc.Services.ElectedMessage, grpc.Services.Empty> getElectedMethod;
    if ((getElectedMethod = ChattingGrpc.getElectedMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getElectedMethod = ChattingGrpc.getElectedMethod) == null) {
          ChattingGrpc.getElectedMethod = getElectedMethod =
              io.grpc.MethodDescriptor.<grpc.Services.ElectedMessage, grpc.Services.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Elected"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.ElectedMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("Elected"))
              .build();
        }
      }
    }
    return getElectedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.Services.RechargePermission,
      grpc.Services.RechargePermissionResponse> getRequireRechargePermissionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequireRechargePermission",
      requestType = grpc.Services.RechargePermission.class,
      responseType = grpc.Services.RechargePermissionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.RechargePermission,
      grpc.Services.RechargePermissionResponse> getRequireRechargePermissionMethod() {
    io.grpc.MethodDescriptor<grpc.Services.RechargePermission, grpc.Services.RechargePermissionResponse> getRequireRechargePermissionMethod;
    if ((getRequireRechargePermissionMethod = ChattingGrpc.getRequireRechargePermissionMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getRequireRechargePermissionMethod = ChattingGrpc.getRequireRechargePermissionMethod) == null) {
          ChattingGrpc.getRequireRechargePermissionMethod = getRequireRechargePermissionMethod =
              io.grpc.MethodDescriptor.<grpc.Services.RechargePermission, grpc.Services.RechargePermissionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequireRechargePermission"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.RechargePermission.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.RechargePermissionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("RequireRechargePermission"))
              .build();
        }
      }
    }
    return getRequireRechargePermissionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.Services.Drone,
      grpc.Services.Empty> getGetDataAfterRechargeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getDataAfterRecharge",
      requestType = grpc.Services.Drone.class,
      responseType = grpc.Services.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.Services.Drone,
      grpc.Services.Empty> getGetDataAfterRechargeMethod() {
    io.grpc.MethodDescriptor<grpc.Services.Drone, grpc.Services.Empty> getGetDataAfterRechargeMethod;
    if ((getGetDataAfterRechargeMethod = ChattingGrpc.getGetDataAfterRechargeMethod) == null) {
      synchronized (ChattingGrpc.class) {
        if ((getGetDataAfterRechargeMethod = ChattingGrpc.getGetDataAfterRechargeMethod) == null) {
          ChattingGrpc.getGetDataAfterRechargeMethod = getGetDataAfterRechargeMethod =
              io.grpc.MethodDescriptor.<grpc.Services.Drone, grpc.Services.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getDataAfterRecharge"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.Drone.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.Services.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new ChattingMethodDescriptorSupplier("getDataAfterRecharge"))
              .build();
        }
      }
    }
    return getGetDataAfterRechargeMethod;
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

    /**
     */
    public void deliveryAssignationService(grpc.Services.DeliveryAssignationMessage request,
        io.grpc.stub.StreamObserver<grpc.Services.DeliveryAssignationResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDeliveryAssignationServiceMethod(), responseObserver);
    }

    /**
     */
    public void deliveryCompleteService(grpc.Services.DeliveryComplete request,
        io.grpc.stub.StreamObserver<grpc.Services.DeliveryCompleteResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDeliveryCompleteServiceMethod(), responseObserver);
    }

    /**
     */
    public void ping(grpc.Services.Empty request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     */
    public void election(grpc.Services.ElectionMessage request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getElectionMethod(), responseObserver);
    }

    /**
     */
    public void elected(grpc.Services.ElectedMessage request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getElectedMethod(), responseObserver);
    }

    /**
     */
    public void requireRechargePermission(grpc.Services.RechargePermission request,
        io.grpc.stub.StreamObserver<grpc.Services.RechargePermissionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRequireRechargePermissionMethod(), responseObserver);
    }

    /**
     */
    public void getDataAfterRecharge(grpc.Services.Drone request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getGetDataAfterRechargeMethod(), responseObserver);
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
          .addMethod(
            getDeliveryAssignationServiceMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.DeliveryAssignationMessage,
                grpc.Services.DeliveryAssignationResponse>(
                  this, METHODID_DELIVERY_ASSIGNATION_SERVICE)))
          .addMethod(
            getDeliveryCompleteServiceMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.DeliveryComplete,
                grpc.Services.DeliveryCompleteResponse>(
                  this, METHODID_DELIVERY_COMPLETE_SERVICE)))
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.Empty,
                grpc.Services.Empty>(
                  this, METHODID_PING)))
          .addMethod(
            getElectionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.ElectionMessage,
                grpc.Services.Empty>(
                  this, METHODID_ELECTION)))
          .addMethod(
            getElectedMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.ElectedMessage,
                grpc.Services.Empty>(
                  this, METHODID_ELECTED)))
          .addMethod(
            getRequireRechargePermissionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.RechargePermission,
                grpc.Services.RechargePermissionResponse>(
                  this, METHODID_REQUIRE_RECHARGE_PERMISSION)))
          .addMethod(
            getGetDataAfterRechargeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                grpc.Services.Drone,
                grpc.Services.Empty>(
                  this, METHODID_GET_DATA_AFTER_RECHARGE)))
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

    /**
     */
    public void deliveryAssignationService(grpc.Services.DeliveryAssignationMessage request,
        io.grpc.stub.StreamObserver<grpc.Services.DeliveryAssignationResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeliveryAssignationServiceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deliveryCompleteService(grpc.Services.DeliveryComplete request,
        io.grpc.stub.StreamObserver<grpc.Services.DeliveryCompleteResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeliveryCompleteServiceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ping(grpc.Services.Empty request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void election(grpc.Services.ElectionMessage request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getElectionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void elected(grpc.Services.ElectedMessage request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getElectedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void requireRechargePermission(grpc.Services.RechargePermission request,
        io.grpc.stub.StreamObserver<grpc.Services.RechargePermissionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequireRechargePermissionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getDataAfterRecharge(grpc.Services.Drone request,
        io.grpc.stub.StreamObserver<grpc.Services.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetDataAfterRechargeMethod(), getCallOptions()), request, responseObserver);
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

    /**
     */
    public grpc.Services.DeliveryAssignationResponse deliveryAssignationService(grpc.Services.DeliveryAssignationMessage request) {
      return blockingUnaryCall(
          getChannel(), getDeliveryAssignationServiceMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.Services.DeliveryCompleteResponse deliveryCompleteService(grpc.Services.DeliveryComplete request) {
      return blockingUnaryCall(
          getChannel(), getDeliveryCompleteServiceMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.Services.Empty ping(grpc.Services.Empty request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.Services.Empty election(grpc.Services.ElectionMessage request) {
      return blockingUnaryCall(
          getChannel(), getElectionMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.Services.Empty elected(grpc.Services.ElectedMessage request) {
      return blockingUnaryCall(
          getChannel(), getElectedMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.Services.RechargePermissionResponse requireRechargePermission(grpc.Services.RechargePermission request) {
      return blockingUnaryCall(
          getChannel(), getRequireRechargePermissionMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.Services.Empty getDataAfterRecharge(grpc.Services.Drone request) {
      return blockingUnaryCall(
          getChannel(), getGetDataAfterRechargeMethod(), getCallOptions(), request);
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

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.DeliveryAssignationResponse> deliveryAssignationService(
        grpc.Services.DeliveryAssignationMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getDeliveryAssignationServiceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.DeliveryCompleteResponse> deliveryCompleteService(
        grpc.Services.DeliveryComplete request) {
      return futureUnaryCall(
          getChannel().newCall(getDeliveryCompleteServiceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.Empty> ping(
        grpc.Services.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.Empty> election(
        grpc.Services.ElectionMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getElectionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.Empty> elected(
        grpc.Services.ElectedMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getElectedMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.RechargePermissionResponse> requireRechargePermission(
        grpc.Services.RechargePermission request) {
      return futureUnaryCall(
          getChannel().newCall(getRequireRechargePermissionMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.Services.Empty> getDataAfterRecharge(
        grpc.Services.Drone request) {
      return futureUnaryCall(
          getChannel().newCall(getGetDataAfterRechargeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SIMPLE_GREETING = 0;
  private static final int METHODID_DELIVERY_ASSIGNATION_SERVICE = 1;
  private static final int METHODID_DELIVERY_COMPLETE_SERVICE = 2;
  private static final int METHODID_PING = 3;
  private static final int METHODID_ELECTION = 4;
  private static final int METHODID_ELECTED = 5;
  private static final int METHODID_REQUIRE_RECHARGE_PERMISSION = 6;
  private static final int METHODID_GET_DATA_AFTER_RECHARGE = 7;

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
        case METHODID_DELIVERY_ASSIGNATION_SERVICE:
          serviceImpl.deliveryAssignationService((grpc.Services.DeliveryAssignationMessage) request,
              (io.grpc.stub.StreamObserver<grpc.Services.DeliveryAssignationResponse>) responseObserver);
          break;
        case METHODID_DELIVERY_COMPLETE_SERVICE:
          serviceImpl.deliveryCompleteService((grpc.Services.DeliveryComplete) request,
              (io.grpc.stub.StreamObserver<grpc.Services.DeliveryCompleteResponse>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((grpc.Services.Empty) request,
              (io.grpc.stub.StreamObserver<grpc.Services.Empty>) responseObserver);
          break;
        case METHODID_ELECTION:
          serviceImpl.election((grpc.Services.ElectionMessage) request,
              (io.grpc.stub.StreamObserver<grpc.Services.Empty>) responseObserver);
          break;
        case METHODID_ELECTED:
          serviceImpl.elected((grpc.Services.ElectedMessage) request,
              (io.grpc.stub.StreamObserver<grpc.Services.Empty>) responseObserver);
          break;
        case METHODID_REQUIRE_RECHARGE_PERMISSION:
          serviceImpl.requireRechargePermission((grpc.Services.RechargePermission) request,
              (io.grpc.stub.StreamObserver<grpc.Services.RechargePermissionResponse>) responseObserver);
          break;
        case METHODID_GET_DATA_AFTER_RECHARGE:
          serviceImpl.getDataAfterRecharge((grpc.Services.Drone) request,
              (io.grpc.stub.StreamObserver<grpc.Services.Empty>) responseObserver);
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
              .addMethod(getDeliveryAssignationServiceMethod())
              .addMethod(getDeliveryCompleteServiceMethod())
              .addMethod(getPingMethod())
              .addMethod(getElectionMethod())
              .addMethod(getElectedMethod())
              .addMethod(getRequireRechargePermissionMethod())
              .addMethod(getGetDataAfterRechargeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
