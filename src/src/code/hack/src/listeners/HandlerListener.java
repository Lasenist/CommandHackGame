package code.hack.src.listeners;

/**
 * Created by Lasen on 24/12/15.
 * Listener for when handlers are changed in the shell.
 */
public interface HandlerListener
{
  void handlerAdded( final Object handler );
  void handlerRemoved( final Object handler );
}
