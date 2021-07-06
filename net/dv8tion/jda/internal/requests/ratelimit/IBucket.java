package net.dv8tion.jda.internal.requests.ratelimit;

import java.util.Queue;
import net.dv8tion.jda.api.requests.Request;

public abstract interface IBucket
{
  public abstract Queue<Request> getRequests();
}
