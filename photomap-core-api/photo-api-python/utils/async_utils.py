import asyncio
import logging

logger = logging.getLogger(__name__)

class AsyncUtils:
    
    @staticmethod
    def run_async_task(coro):
        try:
            # Try to get existing event loop
            loop = asyncio.get_event_loop()
            if loop.is_running():
                # If loop is already running, create a task
                asyncio.create_task(coro)
            else:
                # If no loop is running, run the coroutine
                loop.run_until_complete(coro)
        except RuntimeError:
            # No event loop in current thread, create new one
            asyncio.run(coro) 