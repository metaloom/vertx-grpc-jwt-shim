package io.vertx.grpc.server.auth.impl;

import com.google.common.net.HttpHeaders;

import io.grpc.MethodDescriptor;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.client.GrpcClientRequest;
import io.vertx.grpc.server.auth.JWTGrpcClient;

public class JWTGrpcClientImpl implements JWTGrpcClient {

  private final GrpcClient delegate;
  private TokenCredentials credentials;

  public JWTGrpcClientImpl(GrpcClient client) {
    this.delegate = client;
  }

  @Override
  public Future<GrpcClientRequest<Buffer, Buffer>> request(SocketAddress server) {
    if (credentials != null) {
      return delegate.request(server).map(request -> {
        request.headers().add(HttpHeaders.AUTHORIZATION, "Bearer " + credentials.getToken());
        return request;
      });
    } else {
      return delegate.request(server);
    }
  }

  @Override
  public <Req, Resp> Future<GrpcClientRequest<Req, Resp>> request(SocketAddress server, MethodDescriptor<Req, Resp> service) {
    if (credentials != null) {
      return delegate.request(server, service).map(request -> {
        request.headers().add(HttpHeaders.AUTHORIZATION, "Bearer " + credentials.getToken());
        return request;
      });
    } else {
      return delegate.request(server, service);
    }
  }

  @Override
  public Future<Void> close() {
    return delegate.close();
  }

  @Override
  public JWTGrpcClient withCredentials(TokenCredentials credentials) {
    this.credentials = credentials;
    return this;
  }

}
