package net.dv8tion.jda.internal.requests.restaction.interactions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.exceptions.InteractionFailureException;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.Route.Interactions;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.RequestBody;












public abstract class InteractionCallbackActionImpl
  extends RestActionImpl<InteractionHook>
  implements InteractionCallbackAction
{
  protected final InteractionHookImpl hook;
  protected final Map<String, InputStream> files = new HashMap();
  
  public InteractionCallbackActionImpl(InteractionHookImpl hook)
  {
    super(hook.getJDA(), Route.Interactions.CALLBACK.compile(new String[] { hook.getInteraction().getId(), hook.getInteraction().getToken() }));
    this.hook = hook;
  }
  

  protected abstract DataObject toData();
  
  protected RequestBody finalizeData()
  {
    DataObject json = toData();
    if (files.isEmpty()) {
      return getRequestBody(json);
    }
    MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
    int i = 0;
    for (Map.Entry<String, InputStream> file : files.entrySet())
    {
      RequestBody stream = IOUtil.createRequestBody(Requester.MEDIA_TYPE_OCTET, (InputStream)file.getValue());
      body.addFormDataPart("file" + i++, (String)file.getKey(), stream);
    }
    body.addFormDataPart("payload_json", json.toString());
    files.clear();
    return body.build();
  }
  

  protected void handleSuccess(Response response, Request<InteractionHook> request)
  {
    hook.ready();
    request.onSuccess(hook);
  }
  

  public void handleResponse(Response response, Request<InteractionHook> request)
  {
    if (!response.isOk())
      hook.fail(new InteractionFailureException());
    super.handleResponse(response, request);
  }
  






  private IllegalStateException tryAck()
  {
    return hook.ack() ? new IllegalStateException("This interaction has already been acknowledged or replied to. You can only reply or acknowledge an interaction (or slash command) once!") : 
      null;
  }
  

  public void queue(Consumer<? super InteractionHook> success, Consumer<? super Throwable> failure)
  {
    IllegalStateException exception = tryAck();
    if (exception != null)
    {
      if (failure != null) {
        failure.accept(exception);
      } else
        RestAction.getDefaultFailure().accept(exception);
      return;
    }
    
    super.queue(success, failure);
  }
  

  @Nonnull
  public CompletableFuture<InteractionHook> submit(boolean shouldQueue)
  {
    IllegalStateException exception = tryAck();
    if (exception != null)
    {
      CompletableFuture<InteractionHook> future = new CompletableFuture();
      future.completeExceptionally(exception);
      return future;
    }
    
    return super.submit(shouldQueue);
  }
}
