package net.dv8tion.jda.internal.requests;

import java.io.IOException;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.IOBiConsumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

















public class FunctionalCallback
  implements Callback
{
  private final BiConsumer<Call, IOException> failure;
  private final IOBiConsumer<Call, Response> success;
  
  public FunctionalCallback(BiConsumer<Call, IOException> failure, IOBiConsumer<Call, Response> success)
  {
    this.failure = failure;
    this.success = success;
  }
  
  public static Builder onSuccess(IOBiConsumer<Call, Response> callback)
  {
    return new Builder().onSuccess(callback);
  }
  
  public static Builder onFailure(BiConsumer<Call, IOException> callback)
  {
    return new Builder().onFailure(callback);
  }
  

  public void onFailure(@Nonnull Call call, @Nonnull IOException e)
  {
    if (failure != null) {
      failure.accept(call, e);
    }
  }
  
  public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException
  {
    if (success != null)
      success.accept(call, response);
  }
  
  public static class Builder {
    private BiConsumer<Call, IOException> failure;
    private IOBiConsumer<Call, Response> success;
    
    public Builder() {}
    
    public Builder onSuccess(IOBiConsumer<Call, Response> callback) {
      success = callback;
      return this;
    }
    
    public Builder onFailure(BiConsumer<Call, IOException> callback)
    {
      failure = callback;
      return this;
    }
    
    public FunctionalCallback build()
    {
      return new FunctionalCallback(failure, success);
    }
  }
}
