package net.dv8tion.jda.api.utils;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA.ShardInfo;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.internal.utils.Helpers;
import org.slf4j.Logger;






























public class ConcurrentSessionController
  extends SessionControllerAdapter
  implements SessionController
{
  private Worker[] workers = new Worker[1];
  
  public ConcurrentSessionController() {}
  
  public void setConcurrency(int level)
  {
    assert ((level > 0) && (level < Integer.MAX_VALUE));
    workers = new Worker[level];
  }
  

  public void appendSession(@Nonnull SessionController.SessionConnectNode node)
  {
    getWorker(node).enqueue(node);
  }
  

  public void removeSession(@Nonnull SessionController.SessionConnectNode node)
  {
    getWorker(node).dequeue(node);
  }
  

  private synchronized Worker getWorker(SessionController.SessionConnectNode node)
  {
    int i = node.getShardInfo().getShardId() % workers.length;
    Worker worker = workers[i];
    if (worker == null)
    {
      log.debug("Creating new worker handle for shard pool {}", Integer.valueOf(i)); void 
        tmp54_51 = new Worker(i);worker = tmp54_51;workers[i] = tmp54_51;
    }
    return worker;
  }
  
  private static class Worker implements Runnable
  {
    private final Queue<SessionController.SessionConnectNode> queue = new ConcurrentLinkedQueue();
    private final int id;
    private Thread thread;
    
    public Worker(int id)
    {
      this.id = id;
    }
    
    public synchronized void start()
    {
      if (thread == null)
      {
        thread = new Thread(this, "ConcurrentSessionController-Worker-" + id);
        SessionControllerAdapter.log.debug("Running worker");
        thread.start();
      }
    }
    
    public synchronized void stop()
    {
      thread = null;
      if (!queue.isEmpty()) {
        start();
      }
    }
    
    public void enqueue(SessionController.SessionConnectNode node) {
      SessionControllerAdapter.log.trace("Appending node to queue {}", node.getShardInfo());
      queue.add(node);
      start();
    }
    
    public void dequeue(SessionController.SessionConnectNode node)
    {
      SessionControllerAdapter.log.trace("Removing node from queue {}", node.getShardInfo());
      queue.remove(node);
    }
    

    public void run()
    {
      try
      {
        while (!queue.isEmpty())
        {
          processQueue();
          
          TimeUnit.SECONDS.sleep(5L);
        }
      }
      catch (InterruptedException ex)
      {
        SessionControllerAdapter.log.error("Worker failed to process queue", ex);
      }
      finally
      {
        stop();
      }
    }
    
    private void processQueue() throws InterruptedException
    {
      SessionController.SessionConnectNode node = null;
      try
      {
        node = (SessionController.SessionConnectNode)queue.remove();
        SessionControllerAdapter.log.debug("Running connect node for shard {}", node.getShardInfo());
        node.run(false);

      }
      catch (NoSuchElementException localNoSuchElementException) {}catch (InterruptedException e)
      {
        queue.add(node);
        throw e;
      }
      catch (IllegalStateException|ErrorResponseException e)
      {
        if (Helpers.hasCause(e, OpeningHandshakeException.class)) {
          SessionControllerAdapter.log.error("Failed opening handshake, appending to queue. Message: {}", e.getMessage());
        } else if ((!(e instanceof ErrorResponseException)) || (!(e.getCause() instanceof IOException)))
          if (Helpers.hasCause(e, UnknownHostException.class)) {
            SessionControllerAdapter.log.error("DNS resolution failed: {}", e.getMessage());
          } else if ((e.getCause() != null) && (!JDA.Status.RECONNECT_QUEUED.name().equals(e.getCause().getMessage()))) {
            SessionControllerAdapter.log.error("Failed to establish connection for a node, appending to queue", e);
          } else
            SessionControllerAdapter.log.error("Unexpected exception when running connect node", e);
        if (node != null) {
          queue.add(node);
        }
      }
    }
  }
}
